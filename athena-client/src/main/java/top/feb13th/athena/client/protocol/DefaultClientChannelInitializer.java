package top.feb13th.athena.client.protocol;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import top.feb13th.athena.core.message.MessageConvert;

/**
 * @author zhoutaotao
 * @date 2019/8/30 16:08
 */
@Getter
public class DefaultClientChannelInitializer extends ChannelInitializer<SocketChannel> {

  // ping 服务器的间隔时间
  private int pingDurationSecond;
  // 消息转换器
  private MessageConvert messageConvert;

  public DefaultClientChannelInitializer(int pingDurationSecond,
      MessageConvert messageConvert) {
    if (pingDurationSecond <= 0) {
      throw new IllegalArgumentException("pingDurationSecond must more than zero");
    }
    this.pingDurationSecond = pingDurationSecond;
    this.messageConvert = messageConvert;
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();
    // 心跳检测
    pipeline.addLast(new IdleStateHandler(0, getPingDurationSecond(), 0));
    pipeline.addLast(new DefaultClientIdleStateTrigger());

    // 设置编解码器
    pipeline.addLast(new DefaultClientCodec());

    // 设置消息分发器
    pipeline.addLast(new DefaultClientDispatcher(messageConvert));

  }
}
