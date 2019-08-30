package top.feb13th.athena.client.bean;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模块bean容器
 *
 * @author zhoutaotao
 * @date 2019/8/15 16:31
 */
public class ClientModuleBeanHolder {

  // 日志
  private static final Logger logger = LoggerFactory.getLogger(
      ClientModuleBeanHolder.class);

  // row:module  cel:command  value:ClientModuleBeanWrapper
  private static final Table<Integer, Integer, ClientModuleBeanWrapper> TABLE = HashBasedTable.create();

  /**
   * 设置module和command的对应关系
   */
  public synchronized static void set(ClientModuleBeanWrapper wrapper) {
    int module = wrapper.getModule();
    int command = wrapper.getCommand();
    if (TABLE.contains(module, command)) {
      throw new IllegalStateException(
          "The same Module and Command to different method, module:" + module + ", command:"
              + command + ", methodName:" + wrapper.getMethodName());
    }

    if (logger.isDebugEnabled()) {
      String beanName = wrapper.getBeanName();
      String methodName = wrapper.getMethodName();
      logger.debug(
          "Register Module Command Bean, module:{}, command:{}, beanName:{}, methodName:{}",
          module, command, beanName, methodName);
    }

    TABLE.put(module, command, wrapper);
  }

  /**
   * 根据模块号和命令号获取执行方法
   *
   * @param module 模块号
   * @param command 命令号
   */
  public static ClientModuleBeanWrapper get(int module, int command) {
    return TABLE.get(module, command);
  }
}
