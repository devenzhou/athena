package top.feb13th.athena.protocol;

import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * 默认的服务器编解码
 *
 * @author zhoutaotao
 * @date 2019/8/15 9:56
 */
public class DefaultServerCodec extends
    CombinedChannelDuplexHandler<RequestDecoder, ResponseEncoder> {

  /**
   * 无参构造器, 默认使用 {@link RequestDecoder} {@link ResponseEncoder}
   */
  public DefaultServerCodec() {
    this(new RequestDecoder(), new ResponseEncoder());
  }

  /**
   * 提供扩展的构造器
   *
   * @param inboundHandler 解码器
   * @param outboundHandler 编码器
   */
  public DefaultServerCodec(RequestDecoder inboundHandler,
      ResponseEncoder outboundHandler) {
    super(inboundHandler, outboundHandler);
  }
}
