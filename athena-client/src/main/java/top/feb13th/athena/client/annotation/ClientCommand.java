package top.feb13th.athena.client.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 模块内的命令
 *
 * @author zhoutaotao
 * @date 2019/8/15 14:44
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClientCommand {

  /**
   * 命令号
   */
  int value();

}
