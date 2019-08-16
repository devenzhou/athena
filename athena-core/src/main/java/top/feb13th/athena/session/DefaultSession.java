package top.feb13th.athena.session;

import io.netty.channel.ChannelHandlerContext;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import top.feb13th.athena.protocol.Response;

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
  private ChannelHandlerContext context;

  /**
   * 默认的构造器
   *
   * @param context 管道处理器上下文
   */
  public DefaultSession(ChannelHandlerContext context) {
    this.context = context;
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
    context.writeAndFlush(response);
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
  public ChannelHandlerContext getContext() {
    return context;
  }
}
