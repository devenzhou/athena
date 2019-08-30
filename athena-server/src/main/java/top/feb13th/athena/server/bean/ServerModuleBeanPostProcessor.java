package top.feb13th.athena.server.bean;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import top.feb13th.athena.server.annotation.ServerCommand;
import top.feb13th.athena.server.annotation.ServerModule;

/**
 * 模块bean后置处理器, 用于解析 {@link top.feb13th.athena.server.annotation.ServerModule} 和 {@link
 * top.feb13th.athena.server.annotation.ServerCommand} 注解
 *
 * @author zhoutaotao
 * @date 2019/8/15 16:22
 */
public class ServerModuleBeanPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    Class<?> beanClass = bean.getClass();
    ServerModule moduleAnnotation = beanClass.getAnnotation(ServerModule.class);
    if (moduleAnnotation == null) {
      return bean;
    }
    // 模块号
    int module = moduleAnnotation.value();
    Method[] declaredMethods = beanClass.getDeclaredMethods();
    if (declaredMethods == null || declaredMethods.length == 0) {
      return bean;
    }

    for (Method method : declaredMethods) {
      ServerCommand commandAnnotation = method.getAnnotation(ServerCommand.class);
      if (commandAnnotation == null) {
        continue;
      }
      // 命令号
      int command = commandAnnotation.value();
      // 是否是入口函数
      boolean enter = commandAnnotation.enter();
      // 方法名
      String methodName = method.getName();
      // 方法参数
      Class<?>[] parameterTypes = method.getParameterTypes();
      // 返回值类型
      Class<?> returnType = method.getReturnType();
      ServerModuleBeanWrapper wrapper = new ServerModuleBeanWrapper();
      wrapper.setBean(bean);
      wrapper.setBeanName(beanName);
      wrapper.setModule(module);
      wrapper.setCommand(command);
      wrapper.setEnter(enter);
      wrapper.setMethod(method);
      wrapper.setReturnType(returnType);
      wrapper.setMethodName(methodName);
      wrapper.setParameterTypes(Arrays.asList(parameterTypes));
      ServerModuleBeanHolder.set(wrapper);
    }

    return bean;
  }
}
