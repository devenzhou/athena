package top.feb13th.athena.protocol;

import com.google.common.collect.Lists;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.feb13th.athena.message.IdentityType;
import top.feb13th.athena.message.JsonMessageConvert;
import top.feb13th.athena.message.MessageConvert;
import top.feb13th.athena.message.Response;
import top.feb13th.athena.support.ClientRequest;
import top.feb13th.athena.support.ModuleBeanHolder;
import top.feb13th.athena.support.ModuleBeanWrapper;
import top.feb13th.athena.util.ObjectUtil;

/**
 * 默认的客户端消息分发器
 *
 * @author zhoutaotao
 * @date 2019/8/15 16:00
 */
@Getter
@Setter
public class DefaultClientDispatcher extends MessageToMessageDecoder<Response> {

  private static final Logger logger = LoggerFactory.getLogger(DefaultClientDispatcher.class);

  // 消息转换器
  private MessageConvert messageConvert;

  /**
   * 默认构造器, 使用json格式转换数据
   */
  public DefaultClientDispatcher() {
    this(new JsonMessageConvert());
  }

  /**
   * 可以指定消息格式转换器的构造器
   *
   * @param messageConvert 消息转换器
   */
  public DefaultClientDispatcher(MessageConvert messageConvert) {
    this.messageConvert = messageConvert;
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, Response msg, List<Object> out)
      throws Exception {
    int id = msg.getIdentity();
    int module = msg.getModule();
    int command = msg.getCommand();
    int code = msg.getCode();

    if (logger.isDebugEnabled()) {
      logger.debug("Receive Request, module:{}, command:{}, code:{}", module, command, code);
    }

    // 检查收到的服务端信息是否是同步
    IdentityType identityType = IdentityType.get(id);
    if (ObjectUtil.nonNull(identityType) && identityType != IdentityType.ASYNCHRONOUS) {
      // 处理同步请求
      ClientRequest.addResponse(msg);
      return;
    }

    ModuleBeanWrapper wrapper = ModuleBeanHolder.get(module, command);
    if (wrapper == null) {
      // 资源找不到
      if (logger.isErrorEnabled()) {
        logger.error("Resource not found, module:{}, command:{}", module, command);
      }
      return;
    }

    // 执行方法调用
    try {
      invokeMethod(wrapper, msg);
    } catch (Exception exception) {
      if (logger.isErrorEnabled()) {
        logger.error(exception.getMessage(), exception);
      }
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    logger.error("发送未知错误", cause);
  }

  /**
   * 调用模块号和命令号指定的方法
   *
   * @param wrapper 方法包装器
   * @param response 请求对象
   */
  private void invokeMethod(ModuleBeanWrapper wrapper, Response response)
      throws InvocationTargetException, IllegalAccessException {
    // 输入数据
    int module = response.getModule();
    int command = response.getCommand();
    byte[] byteData = response.getBody();

    // 方法封装信息
    Object bean = wrapper.getBean();
    Method method = wrapper.getMethod();
    List<Class<?>> parameterTypes = wrapper.getParameterTypes();

    // 方法传入对象集合
    List<Object> parameterObjects = Lists.newArrayListWithCapacity(parameterTypes.size());

    if (logger.isDebugEnabled()) {
      logger.debug("Set parameter value, module:{}, command:{}, beanName:{}, methodName:{}", module,
          command, wrapper.getBeanName(), wrapper.getMethodName());
    }

    for (Class<?> parameterType : parameterTypes) {
      // 检测当前参数类型是否是 Response
      if (wrapper.isSameClass(Response.class, parameterType)) {
        parameterObjects.add(response);
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
    method.invoke(bean, parameterObjects);
  }

}
