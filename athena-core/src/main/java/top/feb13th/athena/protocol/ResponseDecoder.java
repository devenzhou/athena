package top.feb13th.athena.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 * 请求解码器
 *
 * @author zhoutaotao
 * @date 2019/8/15 15:39
 */
public class ResponseDecoder extends ByteToMessageDecoder {

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    // 检测当前读取到的字符长度是否大于基础数据长度
    if (in.readableBytes() < Response.BASE_LENGTH) {
      return;
    }

    // 检测字节流是否超出了默认设置的最大值
    if (in.readableBytes() > Response.MAX_LENGTH) {
      in.skipBytes(in.readableBytes());
    }

    // 开始的读取下标
    int beginReader;
    for (; ; ) {
      beginReader = in.readerIndex();
      // 标记当前读取的下标
      in.markReaderIndex();

      // 检测是否开始标记
      if (in.readInt() == Response.FLAG) {
        break;
      }

      // 当前未读取到开始标记, 重置读取下标, 并跳过单个字节
      in.resetReaderIndex();
      in.readByte();

      // 检测剩余的字节是否足够解析
      if (in.readableBytes() < Response.BASE_LENGTH) {
        return;
      }

      // 读取必要的数据 - 模块号,命令号,状态码,长度
      int module = in.readInt();
      int command = in.readInt();
      int code = in.readInt();
      int length = in.readInt();

      // 检测数据包是否到齐
      if (in.readableBytes() < length) {
        in.readerIndex(beginReader);
        return;
      }

      // 读取body
      byte[] body = new byte[length];
      in.readBytes(body);

      // 构建Response
      Response response = new Response(module, command, code, body);
      out.add(response);
    }

  }
}
