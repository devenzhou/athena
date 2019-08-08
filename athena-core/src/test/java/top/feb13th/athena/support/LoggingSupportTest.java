package top.feb13th.athena.support;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类测试
 *
 * @author zhoutaotao
 * @date 2019/6/28
 */
public class LoggingSupportTest {

    @Test
    public void testLoadLogbackXMLInClasspath() {
        LoggingSupport.loadLogbackXMLInClasspath("logback.xml");
        Logger logger = LoggerFactory.getLogger("testLogback");
        logger.info("输出测试日志");
    }

}
