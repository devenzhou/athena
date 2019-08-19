package top.feb13th.athena.session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.feb13th.athena.message.Response;
import top.feb13th.athena.util.ObjectUtil;
import top.feb13th.athena.util.StringUtil;

/**
 * 会话管理器
 *
 * @author zhoutaotao
 * @date 2019/8/16 10:06
 */
public class SessionHolder {

  // 日志
  private static final Logger logger = LoggerFactory.getLogger(SessionHolder.class);

  // id -> session
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
      logger.error("Session id must not empty");
      throw new IllegalArgumentException("Session id must not empty");
    }
    String sessionId = session.id();
    Session existsSession = sessionMap.putIfAbsent(sessionId, session);
    if (!ObjectUtil.isNull(existsSession)) {
      logger.error("The session id exists, id:{}", sessionId);
      throw new IllegalArgumentException("The session id exists, id:" + sessionId);
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
    sessionMap.values().parallelStream().forEach(session -> session.write(response));
  }

}
