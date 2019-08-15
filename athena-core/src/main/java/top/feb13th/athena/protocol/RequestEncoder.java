package top.feb13th.athena.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 请求编码
 *
 * @author zhoutaotao
 * @date 2019/8/15 15:29
 */
public class RequestEncoder extends MessageToByteEncoder<Request> {

  @Override
  protected void encode(ChannelHandlerContext ctx, Request msg, ByteBuf out) throws Exception {
    // 写出开始标记
    out.writeInt(Request.FLAG);
    // 模块号
    out.writeInt(msg.getModule());
    // 命令号
    out.writeInt(msg.getCommand());
    // body长度
    out.writeInt(msg.getLength());
    // body
    out.writeBytes(msg.getBody());
    // 直接刷新出缓存
    ctx.flush();
  }
}
