package top.feb13th.athena.client;

import io.netty.channel.ChannelFuture;

/**
 * 客户端应用程序
 * <p>
 * 每次连接一个服务器就会产生一个新的客户端
 * </p>
 *
 * @author zhoutaotao
 * @date 2019/8/30 11:48
 */
public interface ClientApplication {

  /**
   * 启动服务
   *
   * @return 服务启动后的future
   */
  ChannelFuture run();

  /**
   * 关闭服务
   */
  void close();

}
