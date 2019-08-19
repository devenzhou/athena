package top.feb13th.athena.protocol;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.feb13th.athena.message.JsonMessageConvert;
import top.feb13th.athena.message.MessageConvert;
import top.feb13th.athena.message.Request;
import top.feb13th.athena.message.Response;
import top.feb13th.athena.session.Session;
import top.feb13th.athena.session.SessionHolder;
import top.feb13th.athena.support.ConnectionChecker;
import top.feb13th.athena.support.ModuleBeanHolder;
import top.feb13th.athena.support.ModuleBeanWrapper;
import top.feb13th.athena.support.SystemStatusCode;
import top.feb13th.athena.util.ObjectUtil;

/**
 * 默认的服务器分发器
 *
 * @author zhoutaotao
 * @date 2019/8/15 16:00
 */
@Getter
@Setter
public class DefaultServerDispatcher extends MessageToMessageDecoder<Request> {

  private static final Logger logger = LoggerFactory.getLogger(DefaultServerDispatcher.class);

  // channel 连接检查器
  private ConnectionChecker connectionChecker;
  // 消息转换器
  private MessageConvert messageConvert;

  /**
   * 默认延时策略以及消息转换器
   */
  public DefaultServerDispatcher() {
    this(ConnectionChecker.DEFAULT_DELAY_TIME_MILLISECOND, new JsonMessageConvert());
  }

  /**
   * 可以指定延时策略
   *
   * @param delayTimeMilli 延迟时间, 单位:毫秒
   * @param messageConvert 消息转换器
   */
  public DefaultServerDispatcher(long delayTimeMilli, MessageConvert messageConvert) {
    this.connectionChecker = new ConnectionChecker(delayTimeMilli);
    this.messageConvert = messageConvert;
  }

  @Override
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    // 客户端接入后对当前会话进行监听,
    // 如果超过时间未进行绑定session,
    // 关闭该链接
    connectionChecker.offer(ctx.channel());
  }

  @Override
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    Channel channel = ctx.channel();
    // 客户端断开连接时,将session移除
    AttributeKey<Session> attributeKey = AttributeKey.newInstance(Session.SESSION_KEY_CHANNEL);
    Attribute<Session> attribute = channel.attr(attributeKey);
    Session session = attribute.get();
    if (ObjectUtil.nonNull(session)) {
      attribute.set(null);
      String id = session.id();
      SessionHolder.remove(id);
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, Request msg, List<Object> out) throws Exception {
    int module = msg.getModule();
    int command = msg.getCommand();

    if (logger.isDebugEnabled()) {
      logger.debug("Receive Request, module:{}, command:{}", module, command);
    }

    ModuleBeanWrapper wrapper = ModuleBeanHolder.get(module, command);
    Response response = new Response(module, command);
    if (wrapper == null) {
      moduleCommandNotFound(response, out);
      return;
    }


  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    logger.error("发送未知错误", cause);
    Response response = new Response(-1, -1, SystemStatusCode.FAILURE.getCode());
    ctx.channel().writeAndFlush(response);
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
