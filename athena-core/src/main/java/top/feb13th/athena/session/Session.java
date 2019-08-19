package top.feb13th.athena.session;

import io.netty.channel.Channel;
import top.feb13th.athena.message.Response;

/**
 * 客户端会话
 *
 * @author zhoutaotao
 * @date 2019/8/16 9:46
 */
public interface Session {

  /**
   * 用于绑定在channel上的session key
   */
  String SESSION_KEY_CHANNEL = "SESSION_KEY_4DD03BA3_EA34_4F7E_BC87_02AD488568CB";

  /**
   * 获取会话id
   */
  String id();

  /**
   * 设置会话id
   */
  void setId(String id);

  /**
   * 写出数据到客户端
   *
   * @param response 相应对象
   */
  void write(Response response);

  /**
   * 设置属性
   *
   * @param key 属性键
   * @param value 属性值
   */
  void setAttribute(Object key, Object value);

  /**
   * 获取属性
   *
   * @param key 属性key
   */
  Object getAttribute(Object key);

  /**
   * 获取底层管道
   */
  Channel getChannel();

  /**
   * 客户端连接成功
   */
  void connectSuccess();
}
