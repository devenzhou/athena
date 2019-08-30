package top.feb13th.athena.server.protocol;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import lombok.Setter;
import top.feb13th.athena.core.message.MessageConvert;
import top.feb13th.athena.core.util.ObjectUtil;

/**
 * 默认的无服务端管道初始化器
 *
 * @author zhoutaotao
 * @date 2019/8/30 13:43
 */
@Getter
@Setter
public class DefaultServerChannelInitializer extends ChannelInitializer<SocketChannel> {

  // 心跳检测时间, 单位:秒
  private int idleTimeSeconds;
  // 会话检查时间 - 超出该时间仍未进行登录的则会断开连接
  private int sessionCheckSeconds;
  // 消息转换器
  private MessageConvert messageConvert;

  public DefaultServerChannelInitializer(int idleTimeSeconds, int sessionCheckSeconds,
      MessageConvert messageConvert) {
    this.idleTimeSeconds = idleTimeSeconds;
    this.sessionCheckSeconds = sessionCheckSeconds;
    this.messageConvert = messageConvert;
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    // 检查参数的有效性
    if (idleTimeSeconds <= 0) {
      throw new IllegalArgumentException("idleTimeSecond must more then zero");
    }
    if (sessionCheckSeconds <= 0) {
      throw new IllegalArgumentException("sessionCheckSeconds must more then zero");
    }
    if (sessionCheckSeconds <= idleTimeSeconds) {
      throw new IllegalArgumentException("idleTimeSecond must more then sessionCheckSeconds");
    }
    if (ObjectUtil.isNull(messageConvert)) {
      throw new IllegalArgumentException("MessageConvert must not null");
    }

    ChannelPipeline pipeline = ch.pipeline();

    // 心跳检测
    pipeline.addLast(new IdleStateHandler(0, 0, getIdleTimeSeconds()));
    pipeline.addLast(new DefaultServerIdleStateTrigger());

    // 消息编解码
    pipeline.addLast(new DefaultServerCodec());

    // 事件分发
    pipeline.addLast(new DefaultServerDispatcher(sessionCheckSeconds * 1000, messageConvert));
  }
}
