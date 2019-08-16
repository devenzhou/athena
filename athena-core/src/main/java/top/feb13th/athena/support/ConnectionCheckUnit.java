package top.feb13th.athena.support;

import io.netty.channel.ChannelHandlerContext;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

/**
 * 连接检查单元
 *
 * @author zhoutaotao
 * @date 2019/8/16 13:23
 */
@Getter
public class ConnectionCheckUnit implements Delayed {

  // 延迟单元出队时间
  private long executeTime;
  private ChannelHandlerContext context;

  public ConnectionCheckUnit(long executeTime, ChannelHandlerContext context) {
    this.executeTime = executeTime;
    this.context = context;
  }

  @Override
  public long getDelay(TimeUnit unit) {
    return unit.convert(executeTime - System.currentTimeMillis(), TimeUnit.MICROSECONDS);
  }

  @Override
  public int compareTo(Delayed o) {
    ConnectionCheckUnit other = (ConnectionCheckUnit) o;
    return (int) (this.getExecuteTime() - other.getExecuteTime());
  }

}
