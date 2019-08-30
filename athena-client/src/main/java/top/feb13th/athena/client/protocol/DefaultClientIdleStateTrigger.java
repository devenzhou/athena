package top.feb13th.athena.client.protocol;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.net.InetSocketAddress;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.feb13th.athena.core.message.Request;

/**
 * 客户端发送心跳 - 使用心跳触发器
 *
 * @author zhoutaotao
 * @date 2019/8/19 20:12
 */
@Getter
@Setter
public class DefaultClientIdleStateTrigger extends ChannelInboundHandlerAdapter {

  // 日志
  private static final Logger logger = LoggerFactory.getLogger(DefaultClientIdleStateTrigger.class);

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      IdleState idleState = ((IdleStateEvent) evt).state();
      if (idleState == IdleState.WRITER_IDLE) {
        Channel channel = ctx.channel();
        InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
        String hostName = address.getHostName();
        String hostString = address.getHostString();
        int port = address.getPort();
        if (logger.isDebugEnabled()) {
          logger.debug("Ping server, Server is active, hostName:{}, host:{}, port:{}", hostName,
              hostString, port);
        }
        Request request = new Request();
        channel.writeAndFlush(request);
      }
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }

}
