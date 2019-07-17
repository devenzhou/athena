package org.athena.support;

import org.springframework.context.ApplicationContext;

/**
 * spring ApplicationContext 容器
 *
 * @author zhoutaotao
 * @date 2019/7/17
 */
public class SpringContextSupport {

    // 全局持有的spring 上下文对象
    private static ApplicationContext _applicationContext;

    /**
     * 设置spring 上下文对象
     *
     * @param applicationContext 上下文对象
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        _applicationContext = applicationContext;
    }

    /**
     * 获取 spring 上下文对象
     *
     * @return ApplicationContext
     */
    public ApplicationContext getApplicationContext() {
        return _applicationContext;
    }

}
