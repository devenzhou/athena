package top.feb13th.athena.support;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
  private Map<Class<?>, Set<Class<?>>> parameterInterfaceMap;
  // 方法参数类型对应的父类
  private Map<Class<?>, Set<Class<?>>> parameterSuperClassMap;

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
      Set<Class<?>> interfaceSet = new HashSet<>(Arrays.asList(interfaces));
      parameterInterfaceMap.put(parameterType, interfaceSet);
      Set<Class<?>> superClassSet = new HashSet<>();
      addSuperClass(parameterType, superClassSet, interfaceSet);
      parameterSuperClassMap.put(parameterType, superClassSet);
    }
  }

  /**
   * 递归获取父类
   *
   * @param clazz 用于获取父类的对象
   * @param superClassSet 用于存储所有父类对象的列表
   * @param interfaceSet 接口集合
   */
  private void addSuperClass(Class<?> clazz, Set<Class<?>> superClassSet,
      Set<Class<?>> interfaceSet) {
    Class<?> superclass = clazz.getSuperclass();
    superClassSet.add(superclass);
    Class<?>[] interfaces = superclass.getInterfaces();
    interfaceSet.addAll(Arrays.asList(interfaces));
    if (superclass == Object.class) {
      return;
    }
    addSuperClass(superclass, superClassSet, interfaceSet);
  }

  /**
   * 检查参数类型是否是指定类型
   *
   * @param clazz 指定的类型
   * @param parameterClazz 参数类型
   * @return true:类型一致
   */
  public boolean isSameClass(Class<?> clazz, Class<?> parameterClazz) {

    // 如果类型直接匹配, 则返回true
    if (clazz == parameterClazz) {
      return true;
    }

    // 如果是接口, 则判断接口集中是否存在该接口,否则判断参数类的父类是否是该类型
    if (clazz.isInterface()) {
      Set<Class<?>> interfaceSet = parameterInterfaceMap.get(parameterClazz);
      return interfaceSet.contains(clazz);
    }

    Set<Class<?>> superClassSet = parameterSuperClassMap.get(parameterClazz);

    return superClassSet.contains(clazz);
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
