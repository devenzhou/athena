package top.feb13th.athena.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.net.InetSocketAddress;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.feb13th.athena.message.Request;
import top.feb13th.athena.message.Response;

/**
 * 请求解码器
 *
 * @author zhoutaotao
 * @date 2019/8/15 15:39
 */
@Getter
@Setter
public class RequestDecoder extends ByteToMessageDecoder {

  // 日志
  private static final Logger logger = LoggerFactory.getLogger(RequestDecoder.class);

  // 消息的最大长度
  private int messageMaxLength;

  /**
   * 默认构造器, 消息最大长度默认为: {@link Response#MAX_LENGTH}
   */
  public RequestDecoder() {
    this(Request.MAX_LENGTH);
  }

  /**
   * 用于设置消息最大长度的构造器
   *
   * @param messageMaxLength 消息最大长度
   */
  public RequestDecoder(int messageMaxLength) {
    this.messageMaxLength = messageMaxLength;
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    // 检测当前读取到的字符长度是否大于基础数据长度
    if (in.readableBytes() < Request.BASE_LENGTH) {
      return;
    }

    // 检测字节流是否超出了默认设置的最大值
    if (in.readableBytes() > getMessageMaxLength()) {
      if (logger.isErrorEnabled()) {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        String hostName = address.getHostName();
        String hostString = address.getHostString();
        int port = address.getPort();
        logger.error(
            "Find illegal message, receive message more than limit, hostName:{}, host:{}, port:{}",
            hostName, hostString, port);
      }
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

}
