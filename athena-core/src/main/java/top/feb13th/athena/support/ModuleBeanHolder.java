package top.feb13th.athena.support;

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
public class ModuleBeanHolder {

  private static final Logger logger = LoggerFactory.getLogger(ModuleBeanWrapper.class);

  private static final Table<Integer, Integer, ModuleBeanWrapper> TABLE = HashBasedTable.create();

  /**
   * 设置module和command的对应关系
   */
  public synchronized static void set(ModuleBeanWrapper wrapper) {
    int module = wrapper.getModule();
    int command = wrapper.getCommand();
    if (TABLE.contains(module, command)) {
      throw new IllegalStateException(
          "The same Module and Command to different method, module:" + module + ", command:"
              + command + ", methodName:" + wrapper.getMethodName());
    }
    TABLE.put(module, command, wrapper);
  }

  public static ModuleBeanWrapper get(int module, int command) {
    return TABLE.get(module, command);
  }
}
