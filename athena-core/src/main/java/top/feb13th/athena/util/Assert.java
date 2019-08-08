package top.feb13th.athena.util;

/**
 * @author zhoutaotao
 * @date 2019/7/17
 */
public class Assert {

  /**
   * 用于检测失败是抛异常
   *
   * @param desc 异常描述
   */
  private static void throwError(String desc) {
    throw new AssertionError(desc);
  }

  /**
   * 检测入参是否为空串
   *
   * @param str 入参
   * @param desc 为空串时的描述符
   */
  public static void notBlank(String str, String desc) {
    if (StringUtil.isBlank(str)) {
      throwError(desc);
    }
  }

  /**
   * 当对象为null时抛异常
   *
   * @param obj 入参检测对象
   * @param desc 异常描述
   */
  public static void notNull(Object obj, String desc) {
    if (ObjectUtil.isNull(obj)) {
      throwError(desc);
    }
  }

  /**
   * 当对象不为null时抛异常
   *
   * @param obj 入参检测对象
   * @param desc 异常描述
   */
  public static void isNull(Object obj, String desc) {
    if (!ObjectUtil.isNull(obj)) {
      throwError(desc);
    }
  }

  /**
   * 当表达式不为true时抛异常
   *
   * @param flag 表达式结果
   * @param desc 异常描述
   */
  public static void isTrue(boolean flag, String desc) {
    if (!flag) {
      throwError(desc);
    }
  }
}
