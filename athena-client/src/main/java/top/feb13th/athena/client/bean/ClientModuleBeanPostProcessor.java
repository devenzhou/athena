package top.feb13th.athena.client.bean;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import top.feb13th.athena.client.annotation.ClientCommand;
import top.feb13th.athena.client.annotation.ClientModule;

/**
 * 模块bean后置处理器, 用于解析 {@link top.feb13th.athena.client.annotation.ClientModule} 和 {@link
 * top.feb13th.athena.client.annotation.ClientCommand} 注解
 *
 * @author zhoutaotao
 * @date 2019/8/15 16:22
 */
public class ClientModuleBeanPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    Class<?> beanClass = bean.getClass();
    ClientModule moduleAnnotation = beanClass.getAnnotation(ClientModule.class);
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
      ClientCommand commandAnnotation = method.getAnnotation(ClientCommand.class);
      if (commandAnnotation == null) {
        continue;
      }
      // 命令号
      int command = commandAnnotation.value();
      // 方法名
      String methodName = method.getName();
      // 方法参数
      Class<?>[] parameterTypes = method.getParameterTypes();
      // 返回值类型
      Class<?> returnType = method.getReturnType();
      ClientModuleBeanWrapper wrapper = new ClientModuleBeanWrapper();
      wrapper.setBean(bean);
      wrapper.setBeanName(beanName);
      wrapper.setModule(module);
      wrapper.setCommand(command);
      wrapper.setMethod(method);
      wrapper.setReturnType(returnType);
      wrapper.setMethodName(methodName);
      wrapper.setParameterTypes(Arrays.asList(parameterTypes));
      ClientModuleBeanHolder.set(wrapper);
    }

    return bean;
  }
}
