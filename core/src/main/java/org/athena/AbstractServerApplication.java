package org.athena;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * 抽象的服务器实现类
 *
 * @author zhoutaotao
 * @date 2019/7/17
 */
public abstract class AbstractServerApplication extends AbstractSpringApplication {

    // 服务器启动使用的端口号
    private int port;
    // 服务线程组
    private EventLoopGroup bossGroup;
    // 客户端连接使用的线程组
    private EventLoopGroup workerGroup;

    /**
     * 默认的无参构造器
     *
     * @param port 端口号
     */
    public AbstractServerApplication(int port) {
        this(port, new NioEventLoopGroup(), new NioEventLoopGroup());
    }

    /**
     * 用于构造线程组的构造器
     *
     * @param port        端口号
     * @param bossGroup   服务端线程组
     * @param workerGroup 连接处理的线程组
     */
    public AbstractServerApplication(int port, EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
        this.port = port;
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
    }


    /*--------------------------------------------------- 获取线程组 --------------------------------------------------*/
    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    /*--------------------------------------------------- 设置线程组 --------------------------------------------------*/
    public void setBossGroup(EventLoopGroup bossGroup) {
        this.bossGroup = bossGroup;
    }

    public void setWorkerGroup(EventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
    }
}
