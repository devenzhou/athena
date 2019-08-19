package top.feb13th.athena.message;

import lombok.Getter;
import lombok.Setter;

/**
 * 客户端发送到服务端的请求协议
 *
 * @author zhoutaotao
 * @date 2019/8/15 15:02
 */
@Getter
@Setter
public class Request {

  // 消息分割符
  public static final int FLAG = Integer.MAX_VALUE;
  // 基础消息长度 - 标记 + 模块号 + 命令号 + 长度
  public static final int BASE_LENGTH = 4 + 4 + 4 + 4;
  // 消息的最大长度
  public static final int MAX_LENGTH = 1024 * 1024;

  // 模块号
  private int module;
  // 命令号
  private int command;
  // 消息体长度
  private int length;
  // 消息体
  private byte[] body;

  public Request() {
    this(0, 0);
  }

  public Request(int module, int command) {
    this(module, command, 0);
  }

  public Request(int module, int command, int length) {
    this(module, command, new byte[length]);
  }

  public Request(int module, int command, byte[] body) {
    this.module = module;
    this.command = command;
    this.length = body.length;
    this.body = body;
  }
}
