package top.feb13th.athena.protocol;

import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import top.feb13th.athena.message.Request;
import top.feb13th.athena.message.Response;
import top.feb13th.athena.message.ResponseGenerator;
import top.feb13th.athena.session.DefaultSession;
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
    int id = msg.getIdentity();
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
      out.add(ResponseGenerator.resourceNotFound(id, module, command));
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
      out.add(ResponseGenerator.unauthorized(id, module, command));
      return;
    }

    // 执行方法调用
    try {
      invokeMethod(wrapper, channel, msg, out);
    } catch (AssertionError assertionError) {
      Assertion assertion = assertionError.getAssertion();
      int errorCode = assertion.errorCode();
      out.add(ResponseGenerator.createResponse(id, module, command, errorCode));
    } catch (Exception exception) {
      if (logger.isErrorEnabled()) {
        logger.error(exception.getMessage(), exception);
      }
      out.add(ResponseGenerator.serviceError(id, module, command));
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
      List<Object> out) throws InvocationTargetException, IllegalAccessException {
    // 输入数据
    int id = request.getIdentity();
    int module = request.getModule();
    int command = request.getCommand();
    byte[] byteData = request.getBody();

    // 用于返回的相应对象
    Response response = ResponseGenerator.serviceSuccess(id, module, command);

    // 会话信息
    AttributeKey<Session> attributeKey = AttributeKey.newInstance(Session.SESSION_KEY_CHANNEL);
    Attribute<Session> sessionAttribute = channel.attr(attributeKey);
    Session session = sessionAttribute.setIfAbsent(new DefaultSession(channel));

    // 方法封装信息
    Object bean = wrapper.getBean();
    Method method = wrapper.getMethod();
    Class<?> returnType = wrapper.getReturnType();
    List<Class<?>> parameterTypes = wrapper.getParameterTypes();

    // 方法传入对象集合
    List<Object> parameterObjects = Lists.newArrayListWithCapacity(parameterTypes.size());

    if (logger.isDebugEnabled()) {
      logger.debug("Set parameter value, module:{}, command:{}, beanName:{}, methodName:{}", module,
          command, wrapper.getBeanName(), wrapper.getMethodName());
    }

    for (Class<?> parameterType : parameterTypes) {
      // 检测当前参数类型是否是 Request
      if (wrapper.isSameClass(Request.class, parameterType)) {
        parameterObjects.add(request);
        continue;
      }
      // 检测当前参数类型是否是 Response
      if (wrapper.isSameClass(Response.class, parameterType)) {
        parameterObjects.add(response);
        continue;
      }
      // 检测当前参数类型是否是 Session
      if (wrapper.isSameClass(Session.class, parameterType)) {
        parameterObjects.add(session);
        continue;
      }
      // 没有既定类型, 通过消息转换器转换
      Object message = messageConvert.byteToMessage(byteData, parameterType);
      parameterObjects.add(message);
    }

    if (logger.isDebugEnabled()) {
      logger.debug(
          "Invoke method, module:{}, command:{}, beanName:{}, methodName:{}, parameterValues:{}",
          module,
          command, wrapper.getBeanName(), wrapper.getMethodName(), parameterObjects);
    }

    // 调用方法
    Object returnObject = method.invoke(bean, parameterObjects);
    // 只有方法有返回值,且返回值不为null时,调用消息转换器转换为字节数组
    if (returnType != Void.class && ObjectUtil.nonNull(returnObject)) {
      byte[] bytes = messageConvert.messageToByte(returnObject);
      if (ObjectUtil.nonNull(bytes) && bytes.length > 0) {
        response.setLength(bytes.length);
        response.setBody(bytes);
      }
    }

    // 继续执行过滤链
    out.add(response);
  }

}
