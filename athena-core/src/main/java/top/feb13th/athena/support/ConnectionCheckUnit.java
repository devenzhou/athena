package top.feb13th.athena.support;

import io.netty.channel.Channel;
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
  private Channel channel;

  public ConnectionCheckUnit(long executeTime, Channel channel) {
    this.executeTime = executeTime;
    this.channel = channel;
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
