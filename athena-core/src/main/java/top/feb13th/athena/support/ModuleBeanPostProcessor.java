package top.feb13th.athena.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 模块bean后置处理器
 *
 * @author zhoutaotao
 * @date 2019/8/15 16:22
 */
public class ModuleBeanPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {

    return null;
  }
}
