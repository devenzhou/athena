package top.feb13th.athena.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import top.feb13th.athena.protocol.Response;

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
   * 获取底层管道处理上下文
   */
  ChannelHandlerContext getContext();

  /**
   * 客户端连接成功
   */
  default void connectionSuccess() {
    ChannelHandlerContext context = getContext();
    Channel channel = context.channel();
    AttributeKey<Session> attributeKey = AttributeKey.newInstance(SESSION_KEY_CHANNEL);
    Attribute<Session> attribute = channel.attr(attributeKey);
    attribute.set(this);
  }
}
