package top.feb13th.athena.support;

import io.netty.channel.Channel;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import top.feb13th.athena.message.Request;
import top.feb13th.athena.message.Response;
import top.feb13th.athena.util.ObjectUtil;

/**
 * 客户端请求工具
 *
 * @author zhoutaotao
 * @date 2019/8/27 16:40
 */
public class ClientRequest {

  // 请求等待超时时间 - 默认超时时间为1秒
  private static int expireMillisecond = 1000;
  // 过期的会话
  private static final Set<Integer> expireSession = new HashSet<>();
  // 消息容器
  private static final ConcurrentMap<Integer, Response> messageMap = new ConcurrentHashMap<>();
  // 自增id
  private static final AtomicInteger increaseIdentity = new AtomicInteger(0);

  /**
   * 设置请求默认超时时间
   *
   * @param millisecond 超时时间
   */
  public static void setExpireMillisecond(int millisecond) {
    expireMillisecond = millisecond;
  }

  /**
   * 添加响应信息
   *
   * @param response 响应
   */
  public static void addResponse(Response response) {
    int identity = response.getIdentity();
    if (expireSession.contains(identity)) {
      expireSession.remove(identity);
      return;
    }
    messageMap.put(response.getIdentity(), response);
  }

  /**
   * 发送请求到服务器
   *
   * @param channel 与服务器连接的管道
   * @param request 请求
   * @return 服务器响应
   */
  public static Future<Response> execute(Channel channel, Request request) {
    // 校验数据
    if (ObjectUtil.isNull(channel)) {
      throw new NullPointerException("Channel must not null");
    }
    if (ObjectUtil.isNull(request)) {
      throw new NullPointerException("Request must not null");
    }
    // 设置标识
    int identity = increaseIdentity.getAndIncrement();
    request.setIdentity(identity);

    Callable<Response> callable = () -> {
      // 写出数据
      channel.writeAndFlush(request);
      long beforeTime = System.currentTimeMillis();
      try {
        do {
          // 检测线程是否被打断
          if (Thread.interrupted()) {
            throw new InterruptedException("线程被打断");
          }
          // 检测线程是否超时
          if (System.currentTimeMillis() - beforeTime > expireMillisecond) {
            throw new InterruptedException("线程超时");
          }
        } while (!messageMap.containsKey(request.getIdentity()));
        return messageMap.get(request.getIdentity());
      } catch (InterruptedException e) {
        expireSession.add(request.getIdentity());
      } finally {
        messageMap.remove(request.getIdentity());
      }
      return null;
    };

    // 执行任务
    FutureTask<Response> futureTask = new FutureTask<>(callable);
    ThreadPoolExecutor.execute(futureTask);

    return futureTask;
  }

}
