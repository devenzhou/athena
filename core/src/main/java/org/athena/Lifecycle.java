package org.athena;

/**
 * 声明周期接口, 提供一些钩子函数
 * 可用于在不同的声明周期内做一些事情
 *
 * @author zhoutaotao
 * @date 2019/7/17
 */
public interface Lifecycle {

    /**
     * 初始化之前
     */
    void beforeInitialize();


}
