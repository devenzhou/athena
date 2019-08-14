package top.feb13th.athena.util;

import java.time.LocalDateTime;
import org.junit.Test;

/**
 * 日期工具类测试
 *
 * @author zhoutaotao
 * @date 2019/8/14 16:51
 */
public class DateUtilTest {


  @Test
  public void testGetTimestamp() {
    System.out.println(System.currentTimeMillis());
    System.out.println(DateUtil.getTimestamp(LocalDateTime.now()));
  }

}
