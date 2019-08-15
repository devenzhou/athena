package top.feb13th.athena.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

/**
 * 默认的服务器分发器
 *
 * @author zhoutaotao
 * @date 2019/8/15 16:00
 */
public class DefaultServerDispatcher extends MessageToMessageDecoder<Request> {

  @Override
  protected void decode(ChannelHandlerContext ctx, Request msg, List<Object> out) throws Exception {
    
  }
}
