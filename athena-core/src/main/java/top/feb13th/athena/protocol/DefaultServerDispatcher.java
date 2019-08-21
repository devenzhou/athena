package top.feb13th.athena.protocol;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.net.InetSocketAddress;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.feb13th.athena.exception.Assertion;
import top.feb13th.athena.exception.AssertionError;
import top.feb13th.athena.message.JsonMessageConvert;
import top.feb13th.athena.message.MessageConvert;
import top.feb13th.athena.message.MessageGenerator;
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
    if (wrapper == null) {
      // 资源找不到
      if (logger.isDebugEnabled()) {
        logger.debug("Resource not found, module:{}, command:{}", module, command);
      }
      out.add(MessageGenerator.resourceNotFound(module, command));
      return;
    }

    Channel channel = ctx.channel();
    Session session = SessionHolder.get(channel);
    if (!wrapper.isEnter() && ObjectUtil.isNull(session)) {
      // 未授权
      if (logger.isDebugEnabled()) {
        InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
        String hostName = address.getHostName();
        int port = address.getPort();
        String hostString = address.getHostString();
        logger.debug("Unauthorized request, module:{}, command:{}, hostName:{}, port:{}, host:{}",
            module, command, hostName, port, hostString);
      }
      out.add(MessageGenerator.unauthorized(module, command));
      return;
    }

    // 执行方法调用
    try {
      invokeMethod(wrapper, channel, msg, out);
    } catch (AssertionError assertionError) {
      Assertion assertion = assertionError.getAssertion();
      int errorCode = assertion.errorCode();
      out.add(MessageGenerator.createResponse(module, command, errorCode));
    } catch (Exception exception) {
      out.add(MessageGenerator.serviceError(module, command));
    }

  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    logger.error("发送未知错误", cause);
    Response response = new Response(-1, -1, SystemStatusCode.FAILURE.getCode());
    ctx.channel().writeAndFlush(response);
  }

  /**
   * 调用模块号和命令号指定的方法
   *
   * @param wrapper 方法包装器
   * @param channel 管道
   * @param request 请求对象
   * @param out netty过滤链中传递的对象
   */
  private void invokeMethod(ModuleBeanWrapper wrapper, Channel channel, Request request,
      List<Object> out) {
    // TODO 调用执行方法
  }


}
