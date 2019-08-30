package top.feb13th.athena.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * 模块注解 - 用于标记类所属的模块
 *
 * @author zhoutaotao
 * @date 2019/8/15 14:34
 */
@Component
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServerModule {

  /**
   * 模块号
   */
  int value();
}
