package top.feb13th.athena.server;

import io.netty.channel.ChannelFuture;

/**
 * 服务器启动类
 *
 * @author zhoutaotao
 * @date 2019/8/30 10:57
 */
public interface ServerApplication {

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
