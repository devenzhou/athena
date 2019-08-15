package top.feb13th.athena.support;

import java.lang.reflect.Method;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import top.feb13th.athena.annotation.Command;
import top.feb13th.athena.annotation.Module;

/**
 * 模块bean后置处理器, 用于解析 {@link Module} 和 {@link Command} 注解
 *
 * @author zhoutaotao
 * @date 2019/8/15 16:22
 */
public class ModuleBeanPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    Class<?> beanClass = bean.getClass();
    Module moduleAnnotation = beanClass.getAnnotation(Module.class);
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
      Command commandAnnotation = method.getAnnotation(Command.class);
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
      ModuleBeanWrapper wrapper = new ModuleBeanWrapper();
      wrapper.setBean(bean);
      wrapper.setBeanName(beanName);
      wrapper.setModule(module);
      wrapper.setCommand(command);
      wrapper.setEnter(enter);
      wrapper.setMethod(method);
      wrapper.setMethodName(methodName);
      wrapper.setParameterTypes(parameterTypes);
      ModuleBeanHolder.set(wrapper);
    }

    return bean;
  }
}
