package top.feb13th.athena.message;

import top.feb13th.athena.support.SystemStatusCode;

/**
 * 消息生成器
 *
 * @author zhoutaotao
 * @date 2019/8/21 13:51
 */
public class MessageGenerator {

  /**
   * 资源找不到
   *
   * @param module 模块号
   * @param command 命令号
   */
  public static Response resourceNotFound(int module, int command) {
    return createResponse(module, command, SystemStatusCode.RESOURCE_NOT_FOUND);
  }

  /**
   * 未授权
   *
   * @param module 模块号
   * @param command 命令号
   */
  public static Response unauthorized(int module, int command) {
    return createResponse(module, command, SystemStatusCode.UNAUTHORIZED);
  }

  /**
   * 服务错误
   *
   * @param module 模块号
   * @param command 命令号
   */
  public static Response serviceError(int module, int command) {
    return createResponse(module, command, SystemStatusCode.FAILURE);
  }

  /**
   * 构建响应对象
   *
   * @param module 模块号
   * @param command 命令号
   * @param code 状态码
   * @return 响应对象
   */
  private static Response createResponse(int module, int command, SystemStatusCode code) {
    return createResponse(module, command, code.getCode());
  }

  /**
   * 构建响应对象
   *
   * @param module 模块号
   * @param command 命令号
   * @param code 状态码
   * @return 响应对象
   */
  public static Response createResponse(int module, int command, int code) {
    return new Response(module, command, code);
  }

}
