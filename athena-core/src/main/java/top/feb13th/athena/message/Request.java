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
  // 基础消息长度 - 标记 + identity + 模块号 + 命令号 + 长度
  public static final int BASE_LENGTH = 4 + 4 + 4 + 4 + 4;
  // 消息的最大长度
  public static final int MAX_LENGTH = 1024 * 1024;

  /**
   * identity
   * <ol>
   * <li>0:非同步协议, 直接使用模块号和命令号调用异步方法</li>
   * <li>-1:服务端主推协议</li>
   * <li>正数: 客户端传递的值</li>
   * </ol>
   * {@link IdentityType}
   */
  private int identity;
  // 模块号
  private int module;
  // 命令号
  private int command;
  // 消息体长度
  private int length;
  // 消息体
  private byte[] body;

  /**
   * 默认心跳请求使用该构造器
   */
  public Request() {
    this(-1, -1, -1);
  }

  public Request(int identity, int module, int command) {
    this(identity, module, command, 0);
  }

  public Request(int identity, int module, int command, int length) {
    this(identity, module, command, new byte[length]);
  }

  public Request(int identity, int module, int command, byte[] body) {
    this.identity = identity;
    this.module = module;
    this.command = command;
    this.length = body.length;
    this.body = body;
  }
}
