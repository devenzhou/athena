package top.feb13th.athena.core.util;

/**
 * 字符串工具类
 *
 * @author zhoutaotao
 * @date 2019/5/15
 */
public class StringUtil {

  /**
   * 判读字符串是否是空串
   */
  public static boolean isBlank(String str) {
    return ObjectUtil.isNull(str) || "".equalsIgnoreCase(str.trim());
  }

  /**
   * 判断字符串是否不为null,且不为空串
   */
  public static boolean nonBlank(String str) {
    if (ObjectUtil.nonNull(str)) {
      return !"".equals(str.trim());
    }
    return false;
  }
}
