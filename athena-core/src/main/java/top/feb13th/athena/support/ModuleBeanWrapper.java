package top.feb13th.athena.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 模块bean包装类
 *
 * @author zhoutaotao
 * @date 2019/8/15 17:19
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ModuleBeanWrapper {

  // 模块号
  private int module;
  // 命令号
  private int command;
  // 是否是入口函数
  private boolean enter;
  // 对象
  private Object bean;
  // 对象名称
  private String beanName;
  // (模块号 + 命令号) -> 方法
  private Method method;
  // 方法名
  private String methodName;
  // 方法返回值
  private Class<?> returnType;
  // 方法参数
  private List<Class<?>> parameterTypes;
  // 方法参数类型对应的接口
  private Map<Class<?>, List<Class<?>>> parameterInterfaceMap;
  // 方法参数类型对应的父类
  private Map<Class<?>, List<Class<?>>> parameterSuperClassMap;

  public void setParameterTypes(List<Class<?>> parameterTypes) {
    this.parameterTypes = parameterTypes;
    parseMethodParameter();
  }

  /**
   * 解析方法参数
   */
  private void parseMethodParameter() {
    parameterInterfaceMap = new HashMap<>();
    parameterSuperClassMap = new HashMap<>();
    for (Class<?> parameterType : parameterTypes) {
      Class<?>[] interfaces = parameterType.getInterfaces();
      parameterInterfaceMap.put(parameterType, Arrays.asList(interfaces));
      List<Class<?>> superClassList = new ArrayList<>();
      addSuperClass(parameterType, superClassList);
    }
  }

  /**
   * 递归获取父类
   *
   * @param clazz 用于获取父类的对象
   * @param list 用于存储所有父类对象的列表
   */
  private void addSuperClass(Class<?> clazz, List<Class<?>> list) {
    Class<?> superclass = clazz.getSuperclass();
    list.add(superclass);
    if (superclass == Object.class) {
      return;
    }
    addSuperClass(superclass, list);
  }

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
