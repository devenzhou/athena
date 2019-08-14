package top.feb13th.athena.netty.protocol;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 默认的消息
 *
 * @author zhoutaotao
 * @date 2019/8/20 23:57
 */
@Getter
@Setter
@NoArgsConstructor
public class DefaultMessage {

  // 消息分割符
  public static final int FLAG = Integer.MAX_VALUE;
  // 基础消息长度
  public static final int BASE_LENGTH = 4 + 4;
  // 消息的最大长度
  public static final int MAX_LENGTH = 4096;

  // 消息体长度
  private int length;

  // 消息体
  private byte[] body;

  /**
   * 有参构造器
   */
  public DefaultMessage(byte[] body) {
    this.length = body.length;
    this.body = body;
  }

}
