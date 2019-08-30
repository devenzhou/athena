package top.feb13th.athena.core.support;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类测试
 *
 * @author zhoutaotao
 * @date 2019/6/28
 */
public class LogbackSupportTest {

    @Test
    public void testLoadLogbackXMLInClasspath() {
        LogbackSupport.loadLogbackXMLInClasspath("logback.xml");
        Logger logger = LoggerFactory.getLogger("testLogback");
        logger.info("输出测试日志");
    }

}
