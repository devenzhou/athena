package top.feb13th.athena.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 默认的心跳处理器
 * <p>
 * 使用该类需要将 ${@link io.netty.handler.timeout.IdleStateHandler} 添加到该处理器之前
 *
 * @author zhoutaotao
 * @date 2019/8/19 18:56
 */
public class DefaultHeartbeatHandler extends ChannelInboundHandlerAdapter {

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
