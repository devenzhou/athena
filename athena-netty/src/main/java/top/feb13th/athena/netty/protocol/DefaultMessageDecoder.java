package top.feb13th.athena.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 * 默认的消息解码器
 *
 * @author zhoutaotao
 * @date 2019/8/21 23:58
 */
public class DefaultMessageDecoder extends ByteToMessageDecoder {

  @Override
  protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf,
      List<Object> list) throws Exception {

    // 检测当前读取的字节是否大于基本的协议长度
    if (byteBuf.readableBytes() >= DefaultMessage.BASE_LENGTH) {

      // 过滤过大的消息
      // 防止socket字节流攻击
      if (byteBuf.readableBytes() > DefaultMessage.MAX_LENGTH) {
        byteBuf.skipBytes(byteBuf.readableBytes());
      }

      // 记录消息包开头的下标
      int beginIndex;

      // 循环读取数据
      for (; ; ) {
        // 获取消息包开始的位置
        beginIndex = byteBuf.readerIndex();
        // 标记开始位置
        byteBuf.markReaderIndex();

        // 读取到开始标记, 跳出循环
        if (byteBuf.readInt() == DefaultMessage.FLAG) {
          break;
        }

        // 否则 - 未读取到消息开始标记, 忽略一个字节
        byteBuf.resetReaderIndex();
        byteBuf.readByte();

        // 如果剩余的字节数量不足, 终止读取
        if (byteBuf.readableBytes() < DefaultMessage.BASE_LENGTH) {
          return;
        }

        // 处理读取到消息
        int length = byteBuf.readInt();

        // 检测数据包是否到齐
        if (byteBuf.readableBytes() < length) {
          // 还原读指针
          byteBuf.readerIndex(beginIndex);
          return;
        }

        // 读取字节
        byte[] data = new byte[length];
        byteBuf.readBytes(data);

        // 构建消息
        DefaultMessage message = new DefaultMessage(data);
        list.add(message);
      }

    }
  }

}
