package top.feb13th.athena.support;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 可命名的线程工厂
 *
 * @author zhoutaotao
 * @date 2019/8/27 16:42
 */
public class NamedThreadFactory implements ThreadFactory {

  private static final AtomicLong increaseNum = new AtomicLong(0);
  // 线程名
  private String name;

  public NamedThreadFactory(String name) {
    this.name = name;
  }

  @Override
  public Thread newThread(Runnable r) {
    Thread thread = new Thread(r);
    thread.setName(name + "-" + increaseNum.getAndIncrement());
    return thread;
  }
}
