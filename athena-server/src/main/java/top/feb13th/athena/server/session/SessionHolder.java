package top.feb13th.athena.server.session;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.feb13th.athena.core.message.Response;
import top.feb13th.athena.core.util.ObjectUtil;
import top.feb13th.athena.core.util.StringUtil;

/**
 * 会话管理器
 *
 * @author zhoutaotao
 * @date 2019/8/16 10:06
 */
public class SessionHolder {

  // 日志
  private static final Logger logger = LoggerFactory.getLogger(SessionHolder.class);

  // identity -> session
  private static final ConcurrentMap<String, Session> sessionMap = new ConcurrentHashMap<>();

  /**
   * 设置session
   *
   * @param session session
   */
  public static void set(Session session) {
    if (session == null) {
      logger.error("Session must not null");
      throw new IllegalArgumentException("Session must not null");
    }
    if (StringUtil.isBlank(session.id())) {
      logger.error("Session identity must not empty");
      throw new IllegalArgumentException("Session identity must not empty");
    }
    String sessionId = session.id();
    Session existsSession = sessionMap.putIfAbsent(sessionId, session);
    if (!ObjectUtil.isNull(existsSession)) {
      logger.error("The session identity exists, identity:{}", sessionId);
      throw new IllegalArgumentException("The session identity exists, identity:" + sessionId);
    }
  }

  /**
   * 根据sessionId获取session
   *
   * @param id sessionId
   */
  public static Session get(String id) {
    return sessionMap.get(id);
  }

  /**
   * 根据channel获取session
   *
   * @param channel 管道
   */
  public static Session get(Channel channel) {
    AttributeKey<Session> attributeKey = AttributeKey.newInstance(Session.SESSION_KEY_CHANNEL);
    Attribute<Session> attribute = channel.attr(attributeKey);
    return attribute.get();
  }

  /**
   * 根据sessionId移除session
   *
   * @param id sessionId
   */
  public static Session remove(String id) {
    return sessionMap.remove(id);
  }

  /**
   * 给指定的客户端发送消息
   *
   * @param id 客户端id
   * @param response 发送给指定客户端的消息
   * @return true:发送成功, false:接受的客户端不存在
   */
  public static boolean sendTo(String id, Response response) {
    if (ObjectUtil.isNull(response)) {
      throw new NullPointerException("response must not null");
    }
    Session session = sessionMap.get(id);
    if (ObjectUtil.isNull(session)) {
      return false;
    }
    session.write(response);
    return true;
  }

  /**
   * 广播消息 - 将消息推送给所有在线的客户端
   *
   * @param response 用于推送的消息
   */
  public static void broadcast(Response response) {
    if (ObjectUtil.isNull(response)) {
      throw new NullPointerException("response must not null");
    }
    sessionMap.values().parallelStream().forEach(session -> session.write(response));
  }

}
