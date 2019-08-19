package top.feb13th.athena.util;

/**
 * 对象工具类
 *
 * @author zhoutaotao
 * @date 2019/5/15
 */
public class ObjectUtil {

  /**
   * 校验入参是否为null
   */
  public static boolean isNull(Object obj) {
    return obj == null;
  }

  /**
   * 校验入参是否不为null
   */
  public static boolean nonNull(Object obj) {
    return !isNull(obj);
  }
}
