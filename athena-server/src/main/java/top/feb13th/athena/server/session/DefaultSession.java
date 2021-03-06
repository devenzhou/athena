package top.feb13th.athena.server.session;

import io.netty.channel.Channel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import top.feb13th.athena.core.message.Response;

/**
 * 简单的会话实现
 *
 * @author zhoutaotao
 * @date 2019/8/16 11:02
 */
public class DefaultSession implements Session {

  // 会话id
  private String id;
  // map
  private final ConcurrentMap<Object, Object> map = new ConcurrentHashMap<>();

  // 管道处理器上下文
  private Channel channel;
  // 连接是否成功 true:连接成功
  private AtomicBoolean connectStatus = new AtomicBoolean(false);

  /**
   * 默认的构造器
   *
   * @param channel 管道
   */
  public DefaultSession(Channel channel) {
    this.channel = channel;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public void write(Response response) {
    if (channel.isOpen()) {
      channel.writeAndFlush(response);
    }
  }

  @Override
  public void setAttribute(Object key, Object value) {
    map.put(key, value);
  }

  @Override
  public Object getAttribute(Object key) {
    return map.get(key);
  }

  @Override
  public Channel getChannel() {
    return channel;
  }

  @Override
  public void connectSuccess() {
    connectStatus.set(true);
  }

  @Override
  public boolean isConnectSuccess() {
    return connectStatus.get();
  }
}
