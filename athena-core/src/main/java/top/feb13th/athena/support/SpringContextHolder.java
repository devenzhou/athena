package top.feb13th.athena.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * spring context 存储器
 *
 * @author zhoutaotao
 * @date 2019/8/15 16:26
 */
public class SpringContextHolder {

  private static final Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);

  // spring 上下文
  private volatile static ApplicationContext applicationContext;

  /**
   * 设置spring上下文
   */
  public static synchronized void set(ApplicationContext context) {
    applicationContext = context;
  }

  /**
   * 获取spring上下文
   */
  public static ApplicationContext get() {
    Assert.notNull(applicationContext, "ApplicationContext not initialization");
    return applicationContext;
  }

}
