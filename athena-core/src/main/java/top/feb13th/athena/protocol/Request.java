package top.feb13th.athena.protocol;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 客户端发送到服务端的请求协议
 *
 * @author zhoutaotao
 * @date 2019/8/15 15:02
 */
@Getter
@Setter
@NoArgsConstructor
public class Request {

  // 消息分割符
  public static final int FLAG = Integer.MAX_VALUE;
  // 基础消息长度 - 标记 + 模块号 + 命令号 + 长度
  public static final int BASE_LENGTH = 4 + 4 + 4 + 4;
  // 消息的最大长度
  public static final int MAX_LENGTH = 4096;

  // 模块号
  private int module;
  // 命令号
  private int command;
  // 消息体长度
  private int length;
  // 消息体
  private byte[] body;

  public Request(int module, int command, byte[] body) {
    this.module = module;
    this.command = command;
    this.length = body.length;
    this.body = body;
  }
}
