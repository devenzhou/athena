package top.feb13th.athena.server.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import top.feb13th.athena.core.message.IdentityType;
import top.feb13th.athena.core.message.Request;

/**
 * 默认的心跳处理器
 * <p>
 * 使用该类需要将 ${@link io.netty.handler.timeout.IdleStateHandler} 添加到该处理器之前
 *
 * @author zhoutaotao
 * @date 2019/8/19 18:56
 */
public class DefaultServerIdleStateTrigger extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof Request) {
      Request request = (Request) msg;
      // 检测当前请求是否是心跳请求。
      if (IdentityType.get(request.getIdentity()) != IdentityType.HEARTBEAT) {
        super.channelRead(ctx, msg);
      }
    } else {
      super.channelRead(ctx, msg);
    }
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
      IdleState state = idleStateEvent.state();
      // 检查读写状态
      if (state == IdleState.ALL_IDLE) {
        ctx.disconnect();
      }
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }
}
