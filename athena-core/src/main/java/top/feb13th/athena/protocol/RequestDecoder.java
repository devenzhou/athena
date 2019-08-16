package top.feb13th.athena.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.feb13th.athena.support.ConnectionChecker;

/**
 * 请求解码器
 *
 * @author zhoutaotao
 * @date 2019/8/15 15:39
 */
public class RequestDecoder extends ByteToMessageDecoder {

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    // 检测当前读取到的字符长度是否大于基础数据长度
    if (in.readableBytes() < Request.BASE_LENGTH) {
      return;
    }

    // 检测字节流是否超出了默认设置的最大值
    if (in.readableBytes() > Request.MAX_LENGTH) {
      in.skipBytes(in.readableBytes());
    }

    // 开始的读取下标
    int beginReader;
    for (; ; ) {
      beginReader = in.readerIndex();
      // 标记当前读取的下标
      in.markReaderIndex();

      // 检测是否开始标记
      if (in.readInt() == Request.FLAG) {
        break;
      }

      // 当前未读取到开始标记, 重置读取下标, 并跳过单个字节
      in.resetReaderIndex();
      in.readByte();

      // 检测剩余的字节是否足够解析
      if (in.readableBytes() < Request.BASE_LENGTH) {
        return;
      }

      // 读取必要的数据 - 模块号,命令号,长度
      int module = in.readInt();
      int command = in.readInt();
      int length = in.readInt();

      // 检测数据包是否到齐
      if (in.readableBytes() < length) {
        in.readerIndex(beginReader);
        return;
      }

      // 读取body
      byte[] body = new byte[length];
      in.readBytes(body);

      // 构建Request
      Request request = new Request(module, command, body);
      out.add(request);
    }
  }


  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    ConnectionChecker.offer(ctx);
    super.channelActive(ctx);
  }

}
