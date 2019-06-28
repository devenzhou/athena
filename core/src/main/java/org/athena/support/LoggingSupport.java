package org.athena.support;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 *
 * @author zhoutaotao
 * @date 2019/6/28
 */
public class LoggingSupport {

    /**
     * 从给定的绝对地址加载logback配置文件
     *
     * @param abstractPath 绝对地址
     */
    public static void loadLogbackXML(String abstractPath) {
        try {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            configurator.doConfigure(abstractPath);
            StatusPrinter.printInCaseOfErrorsOrWarnings(context);
        } catch (JoranException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 从类路径下加载logback配置文件
     *
     * @param path 相对路径
     */
    public static void loadLogbackXMLInClasspath(String path) {
        loadLogbackXML(Thread.currentThread().
                getContextClassLoader().getResource(path).getPath());
    }
}
