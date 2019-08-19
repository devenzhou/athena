package top.feb13th.athena.message;

import top.feb13th.athena.util.JsonUtil;

/**
 * 将json字符串转换为对象
 *
 * @author zhoutaotao
 * @date 2019/8/19 18:47
 */
public class JsonMessageConvert implements MessageConvert {

  @Override
  public Object convert(byte[] origin, Class<?> clazz) {
    if (origin == null || origin.length == 0) {
      return null;
    }
    return JsonUtil.fromJson(origin, clazz);
  }
}
