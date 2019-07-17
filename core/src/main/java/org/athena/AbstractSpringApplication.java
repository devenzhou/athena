package org.athena;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * spring 抽象启动类
 *
 * @author zhoutaotao
 * @date 2019/7/17
 */
public abstract class AbstractSpringApplication {

    public AbstractSpringApplication() {

    }

    /**
     * 初始化spring
     * TODO 添加自定义注解
     */
    private void initialize() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext();
    }

    public void run(Class<?> configClass) {
        // TODO  获取自定义注解

    }
}
