package top.feb13th.athena.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 默认的消息编码器
 *
 * @author zhoutaotao
 * @date 2019/8/20 23:56
 */
public class DefaultMessageEncoder extends MessageToByteEncoder<DefaultMessage> {

  @Override
  protected void encode(ChannelHandlerContext channelHandlerContext, DefaultMessage defaultMessage,
      ByteBuf byteBuf) throws Exception {
    // 写出标记
    byteBuf.writeInt(DefaultMessage.FLAG);
    // 写出数据长度
    byteBuf.writeInt(defaultMessage.getLength());
    // 写出数据体
    byteBuf.writeBytes(defaultMessage.getBody());
  }

}
