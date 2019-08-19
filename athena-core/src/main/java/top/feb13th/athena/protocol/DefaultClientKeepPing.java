package top.feb13th.athena.protocol;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.feb13th.athena.message.Request;

/**
 * 客户端发送心跳
 *
 * @author zhoutaotao
 * @date 2019/8/19 20:12
 */
@Getter
@Setter
public class DefaultClientKeepPing extends ChannelInboundHandlerAdapter {

  // 默认心跳包发送间隔
  private static final int DEFAULT_PING_MILLISECOND = 1000 * 3;

  // 心跳包发送时间间隔
  private int milliSecond;

  /**
   * 带有默认时间间隔的ping
   */
  public DefaultClientKeepPing() {
    this(DEFAULT_PING_MILLISECOND);
  }

  /**
   * 可以指定心跳发送间隔时间构造器
   *
   * @param milliSecond 心跳间隔时间, 单位:毫秒
   */
  public DefaultClientKeepPing(int milliSecond) {
    this.milliSecond = milliSecond;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
    Channel channel = ctx.channel();
    ScheduledFuture<?> scheduledFuture = channel.eventLoop()
        .schedule(new PingTask(channel), milliSecond, TimeUnit.MICROSECONDS);
    scheduledFuture.addListener(future -> {
      if (!future.isSuccess()) {
        future.cancel(true);
        scheduledFuture.cancel(true);
      }
    });
  }


  /**
   * ping 任务
   */
  public static class PingTask implements Runnable {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(PingTask.class);

    // 当前连接的服务器
    private Channel channel;

    /**
     * 用于指定channel
     *
     * @param channel channel
     */
    public PingTask(Channel channel) {
      this.channel = channel;
    }

    @Override
    public void run() {
      if (channel.isActive()) {
        if (logger.isDebugEnabled()) {
          InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
          String hostName = address.getHostName();
          String hostString = address.getHostString();
          int port = address.getPort();
          logger.debug("Ping server, Server is active, hostName:{}, host:{}, port:{}", hostName,
              hostString, port);
        }
        Request request = new Request();
        channel.writeAndFlush(request);
      } else {
        if (logger.isDebugEnabled()) {
          InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
          String hostName = address.getHostName();
          String hostString = address.getHostString();
          int port = address.getPort();
          logger.debug("Ping server, Server is inactive, hostName:{}, host:{}, port:{}", hostName,
              hostString, port);
        }
        channel.close();
        throw new IllegalStateException("Channel is inactive");
      }
    }
  }

}
