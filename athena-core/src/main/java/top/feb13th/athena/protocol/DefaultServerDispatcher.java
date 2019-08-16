package top.feb13th.athena.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import top.feb13th.athena.support.ModuleBeanHolder;
import top.feb13th.athena.support.ModuleBeanWrapper;
import top.feb13th.athena.support.SystemStatusCode;

/**
 * 默认的服务器分发器
 *
 * @author zhoutaotao
 * @date 2019/8/15 16:00
 */
public class DefaultServerDispatcher extends MessageToMessageDecoder<Request> {

  @Override
  protected void decode(ChannelHandlerContext ctx, Request msg, List<Object> out) throws Exception {
    int module = msg.getModule();
    int command = msg.getCommand();
    ModuleBeanWrapper wrapper = ModuleBeanHolder.get(module, command);
    Response response = new Response(module, command);
    if (wrapper == null) {
      moduleCommandNotFound(response, out);
      return;
    }


  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    Response response = new Response(-1, -1);
    super.exceptionCaught(ctx, cause);
  }

  /**
   * 资源未找到
   *
   * @param response 响应对象
   * @param out 输出
   */
  private void moduleCommandNotFound(Response response, List<Object> out) {
    response.setCode(SystemStatusCode.RESOURCE_NOT_FOUND.getCode());
    out.add(response);
  }


}
