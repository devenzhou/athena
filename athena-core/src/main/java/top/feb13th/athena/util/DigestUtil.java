package top.feb13th.athena.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密工具
 *
 * @author zhoutaotao
 * @date 2019/5/23
 */
public class DigestUtil {

  /**
   * MD5 加密
   *
   * @param source 源加密字符串
   */
  public static String md5(String source) {
    if (StringUtil.isBlank(source)) {
      throw new IllegalArgumentException("被加密的数据不能为null");
    }
    try {
      MessageDigest instance = MessageDigest.getInstance("md5");
      byte[] digest = instance.digest(source.getBytes());
      return ByteUtil.byte2Hex(digest);
    } catch (NoSuchAlgorithmException e) {
      throw ExceptionUtil.unchecked(e);
    }
  }

}
