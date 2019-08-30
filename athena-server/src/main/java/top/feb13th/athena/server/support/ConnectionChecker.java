package top.feb13th.athena.server.support;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.util.concurrent.DelayQueue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.feb13th.athena.server.session.Session;
import top.feb13th.athena.core.util.ExceptionUtil;

/**
 * 连接检查器
 *
 * @author zhoutaotao
 * @date 2019/8/16 13:18
 */
@Getter
@Setter
@NoArgsConstructor
public class ConnectionChecker {

  // 日志
  private static final Logger logger = LoggerFactory.getLogger(ConnectionChecker.class);
  // 默认channel延时检查时间
  public static final int DEFAULT_DELAY_TIME_MILLISECOND = 1000 * 3;
  // 延迟队列, 用于检测所有连接但是未登录的请求
  private static final DelayQueue<ConnectionCheckUnit> delayQueue = new DelayQueue<>();
  // 延迟时间
  private long delayTimeMilli = 3 * 1000;

  /**
   * 默认构造器
   *
   * @param delayTimeMilli 延迟时间, 毫秒
   */
  public ConnectionChecker(long delayTimeMilli) {
    this.delayTimeMilli = delayTimeMilli;
  }

  /**
   * 入队
   *
   * @param channel 连接上下文
   */
  public void offer(Channel channel) {
    long time = System.currentTimeMillis() + delayTimeMilli;
    ConnectionCheckUnit unit = new ConnectionCheckUnit(time, channel);
    delayQueue.offer(unit);
  }

  static {
    // 启动一个守护线程, 用于检测context上是否存在session
    Runnable task = () -> {

      if (logger.isInfoEnabled()) {
        logger.info("Connection Checker is startup");
      }

      for (; ; ) {
        try {
          ConnectionCheckUnit unit = delayQueue.take();
          if (unit == null) {
            continue;
          }
          Channel channel = unit.getChannel();
          if (channel.isOpen()) {
            // 检测当前session是否登录
            AttributeKey<Session> attributeKey = AttributeKey
                .newInstance(Session.SESSION_KEY_CHANNEL);
            Attribute<Session> attribute = channel.attr(attributeKey);
            Session session = attribute.get();
            // session 为null时说明用户未登录
            if (session == null || !session.isConnectSuccess()) {
              if (logger.isDebugEnabled()) {
                logger
                    .debug("Close Connection, name:{}, remoteAddress:{}", channel.id().asLongText(),
                        channel.remoteAddress());
              }
              channel.close();
            }
          }
        } catch (InterruptedException e) {
          logger.error("Connection Checker is interrupted", e);
          throw ExceptionUtil.unchecked(e);
        }
      }
    };
    Thread connectionChecker = new Thread(task);
    connectionChecker.setDaemon(true);
    connectionChecker.setName("Connection Checker");
    connectionChecker.start();
  }

}
