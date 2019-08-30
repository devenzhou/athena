package top.feb13th.athena.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.feb13th.athena.client.protocol.DefaultClientChannelInitializer;
import top.feb13th.athena.core.message.JsonMessageConvert;
import top.feb13th.athena.core.message.MessageConvert;
import top.feb13th.athena.core.util.ExceptionUtil;
import top.feb13th.athena.core.util.ObjectUtil;
import top.feb13th.athena.core.util.StringUtil;

/**
 * 默认的客户端服务实现类
 *
 * @author zhoutaotao
 * @date 2019/8/30 15:49
 */
public class DefaultClientApplication implements ClientApplication {

  // 日志
  private static final Logger logger = LoggerFactory.getLogger(DefaultClientApplication.class);
  private static final int DEFAULT_PING_DURATION = 2;
  // ip
  @Getter
  private String host;
  // 端口
  @Getter
  private int port;
  // ping 服务器的间隔时间
  @Getter
  @Setter
  private int pingDurationSecond = DEFAULT_PING_DURATION;
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
  private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
  @Getter
  @Setter
  private Class<? extends Channel> channelClass = NioSocketChannel.class;
  // 管道参数
  private final Map<ChannelOption, Object> options = new HashMap<>();
  @Getter
  private ChannelFuture channelFuture;

  /**
   * 默认的构造器, 必须初始化host和port
   *
   * @param host 主机
   * @param port 端口
   */
  public DefaultClientApplication(String host, int port) {
    if (StringUtil.isBlank(host)) {
      throw new IllegalArgumentException("host must not null");
    }
    if (port <= 0) {
      throw new IllegalArgumentException("port must more then zero");
    }
    this.host = host;
    this.port = port;

    // 初始化部分
    addOption(ChannelOption.SO_KEEPALIVE, true);
  }

  @Override
  @SuppressWarnings("unchecked")
  public ChannelFuture run() {
    try {
      Bootstrap bootstrap = new Bootstrap();
      // 设置事件驱动连接池
      bootstrap.group(getEventLoopGroup());
      // 设置channel类型
      bootstrap.channel(getChannelClass());
      // 设置channel初始化器
      bootstrap.handler(getChannelInitializer());
      // 设置参数
      for (Entry<ChannelOption, Object> entry : options.entrySet()) {
        bootstrap.option(entry.getKey(), entry.getValue());
      }
      channelFuture = bootstrap.connect(getHost(), getPort()).sync();
    } catch (InterruptedException e) {
      logger.error("Connect to server error, host:{}, port:{}", getHost(), getPort(), e);
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
    if (ObjectUtil.nonNull(eventLoopGroup)) {
      eventLoopGroup.shutdownGracefully();
    }
  }

  /**
   * 获取channel初始化器
   */
  public ChannelInitializer<SocketChannel> getChannelInitializer() {
    if (ObjectUtil.isNull(channelInitializer)) {
      channelInitializer = new DefaultClientChannelInitializer(getPingDurationSecond(),
          getMessageConvert());
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
  public <T> void addOption(ChannelOption<T> option, T value) {
    if (option == null) {
      throw new NullPointerException("option");
    }
    if (value == null) {
      synchronized (options) {
        options.remove(option);
      }
    } else {
      synchronized (options) {
        options.put(option, value);
      }
    }
  }

}
