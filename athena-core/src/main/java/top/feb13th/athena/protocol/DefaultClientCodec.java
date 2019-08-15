package top.feb13th.athena.protocol;

import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * 默认的客户端编解码
 *
 * @author zhoutaotao
 * @date 2019/8/15 14:55
 */
public class DefaultClientCodec extends
    CombinedChannelDuplexHandler<ResponseDecoder, RequestEncoder> {

  /**
   * 无参构造器, 默认使用 {@link ResponseDecoder} {@link RequestEncoder}
   */
  public DefaultClientCodec() {
    this(new ResponseDecoder(), new RequestEncoder());
  }

  /**
   * 提供扩展的构造器
   *
   * @param inboundHandler 解码器
   * @param outboundHandler 编码器
   */
  public DefaultClientCodec(ResponseDecoder inboundHandler,
      RequestEncoder outboundHandler) {
    super(inboundHandler, outboundHandler);
  }
}
