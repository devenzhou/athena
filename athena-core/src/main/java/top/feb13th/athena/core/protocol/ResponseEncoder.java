package top.feb13th.athena.core.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import top.feb13th.athena.core.message.Request;
import top.feb13th.athena.core.message.Response;

/**
 * 相应编码器
 *
 * @author zhoutaotao
 * @date 2019/8/15 15:37
 */
public class ResponseEncoder extends MessageToByteEncoder<Response> {

  @Override
  protected void encode(ChannelHandlerContext ctx, Response msg, ByteBuf out) throws Exception {
    // 写出开始标记
    out.writeInt(Request.FLAG);
    // identity
    out.writeInt(msg.getIdentity());
    // 模块号
    out.writeInt(msg.getModule());
    // 命令号
    out.writeInt(msg.getCommand());
    // 状态码
    out.writeInt(msg.getCode());
    // body长度
    out.writeInt(msg.getLength());
    // body
    out.writeBytes(msg.getBody());
    // 直接刷新出缓存
    ctx.flush();
  }
}
