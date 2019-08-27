package top.feb13th.athena.support;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 线程执行器
 *
 * @author zhoutaotao
 * @date 2019/8/27 16:41
 */
public class ThreadPoolExecutor implements Closeable {

  // 线程池
  private static ExecutorService executorService = Executors
      .newCachedThreadPool(new NamedThreadFactory(ThreadPoolExecutor.class.getSimpleName()));

  /**
   * 开始执行任务
   *
   * @param runnable 任务
   * @return future
   */
  public static Future<?> execute(Runnable runnable) {
    return executorService.submit(runnable);
  }

  /**
   * 执行可回调的任务
   *
   * @param callable 任务
   * @param <V> 任务返回类型
   * @return future
   */
  public static <V> Future<V> execute(Callable<V> callable) {
    return executorService.submit(callable);
  }

  @Override
  public void close() throws IOException {
    executorService.shutdown();
  }
}
