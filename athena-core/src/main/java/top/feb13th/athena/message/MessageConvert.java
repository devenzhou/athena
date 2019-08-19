package top.feb13th.athena.message;

/**
 * 消息转换器,用于将 byte 转换为 Object
 *
 * @author zhoutaotao
 * @date 2019/8/19 17:58
 */
public interface MessageConvert {

  /**
   * 将字节消息转换为对应的class对象
   *
   * @param origin 字节
   * @param clazz 被转换的对象类型
   * @return 转换后的对象
   */
  Object convert(byte[] origin, Class<?> clazz);

}
