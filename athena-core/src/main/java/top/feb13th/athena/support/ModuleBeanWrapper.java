package top.feb13th.athena.support;

import java.lang.reflect.Method;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 模块bean包装类
 *
 * @author zhoutaotao
 * @date 2019/8/15 17:19
 */
@Getter
@Setter
@NoArgsConstructor
public class ModuleBeanWrapper {

  // 模块号
  private int module;
  // 命令号
  private int command;
  // 是否是入口函数
  private boolean enter;
  // (模块号 + 命令号) -> 方法
  private Method method;
  // 方法名
  private String methodName;
  // 方法参数
  private Class<?>[] parameterTypes;
  // 对象
  private Object bean;
  // 对象名称
  private String beanName;

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }
    ModuleBeanWrapper that = (ModuleBeanWrapper) object;
    return module == that.module &&
        command == that.command &&
        method.equals(that.method);
  }

  @Override
  public int hashCode() {
    return Objects.hash(module, command, method);
  }
}
