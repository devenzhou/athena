package top.feb13th.athena.message;

import lombok.Getter;
import lombok.Setter;
import top.feb13th.athena.support.SystemStatusCode;

/**
 * 服务端发送给客户端的协议
 *
 * @author zhoutaotao
 * @date 2019/8/15 15:22
 */
@Getter
@Setter
public class Response {

  // 消息分割符
  public static final int FLAG = Integer.MAX_VALUE;
  // 基础消息长度 - 标记 + identity + 模块号 + 命令号 + 状态码 + 长度
  public static final int BASE_LENGTH = 4 + 4 + 4 + 4 + 4 + 4;
  // 消息的最大长度
  public static final int MAX_LENGTH = 4096;

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
  // 状态码
  private int code;
  // 消息体长度
  private int length;
  // 消息体
  private byte[] body;

  public Response() {
    this(-1, -1, -1);
  }

  public Response(int identity, int module, int command) {
    this(identity, module, command, SystemStatusCode.SUCCESS.getCode());
  }

  public Response(int identity, int module, int command, int code) {
    this(identity, module, command, code, new byte[0]);
  }

  public Response(int identity, int module, int command, int code, byte[] body) {
    this.identity = identity;
    this.module = module;
    this.command = command;
    this.code = code;
    this.length = body.length;
    this.body = body;
  }

}
