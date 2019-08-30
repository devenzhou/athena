package top.feb13th.athena.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.feb13th.athena.core.message.JsonMessageConvert;
import top.feb13th.athena.core.message.MessageConvert;
import top.feb13th.athena.server.protocol.DefaultServerChannelInitializer;
import top.feb13th.athena.core.util.ExceptionUtil;
import top.feb13th.athena.core.util.ObjectUtil;

/**
 * 默认实现的服务器启动类
 *
 * @author zhoutaotao
 * @date 2019/8/30 11:10
 */
public class DefaultServerApplication implements ServerApplication {

  // 日志
  private static final Logger logger = LoggerFactory.getLogger(DefaultServerApplication.class);
  private static final int DEFAULT_PORT = 9111;
  private static final int DEFAULT_IDLE_TIME = 10;
  private static final int DEFAULT_SESSION_AUTH_EXPIRE_TIME = 3;

  // 端口
  @Getter
  @Setter
  private int port = DEFAULT_PORT;
  // 心跳检测时间, 单位:秒
  @Getter
  @Setter
  private int idleTimeSecond = DEFAULT_IDLE_TIME;
  // 会话授权过期时间
  @Getter
  @Setter
  private int sessionAuthExpireSecond = DEFAULT_SESSION_AUTH_EXPIRE_TIME;
  // 消息转换器
  @Getter
  @Setter
  private MessageConvert messageConvert = new JsonMessageConvert();
  // channel 初始化器
  @Setter
  private ChannelInitializer<SocketChannel> channelInitializer;

  // 事件驱动线程池
  @Getter
  @Setter
  private EventLoopGroup bossGroup = new NioEventLoopGroup();
  @Getter
  @Setter
  private EventLoopGroup workerGroup = new NioEventLoopGroup();
  @Getter
  @Setter
  private Class<? extends ServerChannel> channelClass = NioServerSocketChannel.class;
  // boss管道参数
  private final Map<ChannelOption, Object> bossOptions = new HashMap<>();
  // worker管道参数
  private final Map<ChannelOption, Object> workerOptions = new HashMap<>();
  @Getter
  private ChannelFuture channelFuture;

  public DefaultServerApplication() {
    // bean 创建完成后进行基础的初始化
    addBossOption(ChannelOption.SO_BACKLOG, 128);
    addWorkerOption(ChannelOption.SO_KEEPALIVE, true);
  }

  @Override
  @SuppressWarnings("unchecked")
  public ChannelFuture run() {

    try {
      // 服务器启动脚本
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      // 线程池
      serverBootstrap.group(getBossGroup(), getWorkerGroup());
      // 线程模型
      serverBootstrap.channel(getChannelClass());
      serverBootstrap.childHandler(getChannelInitializer());
      // channel 参数
      for (Entry<ChannelOption, Object> entry : bossOptions.entrySet()) {
        serverBootstrap.option(entry.getKey(), entry.getValue());
      }
      // 子channel参数
      for (Entry<ChannelOption, Object> entry : workerOptions.entrySet()) {
        serverBootstrap.childOption(entry.getKey(), entry.getValue());
      }
      // 启用服务器
      channelFuture = serverBootstrap.bind(port).sync();
    } catch (InterruptedException e) {
      logger.error("服务器启动失败", e);
      throw ExceptionUtil.unchecked(e);
    } finally {
      closeEventLoopGroup();
    }
    return channelFuture;
  }

  @Override
  public void close() {
    try {
      if (ObjectUtil.nonNull(channelFuture)) {
        channelFuture.channel().closeFuture().sync();
      }
    } catch (InterruptedException e) {
      throw ExceptionUtil.unchecked(e);
    } finally {
      closeEventLoopGroup();
    }
  }

  /**
   * 关闭事件线程池
   */
  private void closeEventLoopGroup() {
    if (ObjectUtil.nonNull(bossGroup)) {
      bossGroup.shutdownGracefully();
    }
    if (ObjectUtil.nonNull(workerGroup)) {
      workerGroup.shutdownGracefully();
    }
  }

  /**
   * 获取channel初始化器
   */
  public ChannelInitializer<SocketChannel> getChannelInitializer() {
    if (ObjectUtil.isNull(channelInitializer)) {
      channelInitializer = new DefaultServerChannelInitializer(getIdleTimeSecond(),
          getSessionAuthExpireSecond(), getMessageConvert());
    }
    return channelInitializer;
  }

  /**
   * 添加boss管道参数 - 值为null时则移除当前参数项
   *
   * @param option 参数项
   * @param value 参数值
   * @param <T> 值类型
   */
  public <T> void addBossOption(ChannelOption<T> option, T value) {
    if (option == null) {
      throw new NullPointerException("option");
    }
    if (value == null) {
      synchronized (bossOptions) {
        bossOptions.remove(option);
      }
    } else {
      synchronized (bossOptions) {
        bossOptions.put(option, value);
      }
    }
  }

  /**
   * 添加worker管道参数 - 值为null时则移除当前参数项
   *
   * @param option 参数项
   * @param value 参数值
   * @param <T> 值类型
   */
  public <T> void addWorkerOption(ChannelOption<T> option, T value) {
    if (option == null) {
      throw new NullPointerException("option");
    }
    if (value == null) {
      synchronized (workerOptions) {
        workerOptions.remove(option);
      }
    } else {
      synchronized (workerOptions) {
        workerOptions.put(option, value);
      }
    }
  }

}
