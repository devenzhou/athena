package top.feb13th.athena.core.message;

import java.nio.charset.Charset;
import top.feb13th.athena.core.util.JsonUtil;

/**
 * 将json字符串转换为对象
 *
 * @author zhoutaotao
 * @date 2019/8/19 18:47
 */
public class JsonMessageConvert implements MessageConvert {

  @Override
  public Object byteToMessage(byte[] origin, Class<?> clazz) {
    if (origin == null || origin.length == 0) {
      return null;
    }
    return JsonUtil.fromJson(origin, clazz);
  }

  @Override
  public byte[] messageToByte(Object msg) {
    String json = JsonUtil.toJson(msg);
    return json.getBytes(Charset.forName("UTF-8"));
  }
}
