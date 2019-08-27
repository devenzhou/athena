package top.feb13th.athena.message;

import top.feb13th.athena.support.SystemStatusCode;

/**
 * 消息生成器
 *
 * @author zhoutaotao
 * @date 2019/8/21 13:51
 */
public class ResponseGenerator {

  /**
   * 资源找不到
   *
   * @param module 模块号
   * @param command 命令号
   */
  public static Response resourceNotFound(int id, int module, int command) {
    return createResponse(id, module, command, SystemStatusCode.RESOURCE_NOT_FOUND);
  }

  /**
   * 未授权
   *
   * @param id identity
   * @param module 模块号
   * @param command 命令号
   */
  public static Response unauthorized(int id, int module, int command) {
    return createResponse(id, module, command, SystemStatusCode.UNAUTHORIZED);
  }

  /**
   * 服务错误
   *
   * @param id identity
   * @param module 模块号
   * @param command 命令号
   */
  public static Response serviceError(int id, int module, int command) {
    return createResponse(id, module, command, SystemStatusCode.FAILURE);
  }

  /**
   * 服务执行成功
   *
   * @param id identity
   * @param module 模块号
   * @param command 命令号
   */
  public static Response serviceSuccess(int id, int module, int command) {
    return createResponse(id, module, command, SystemStatusCode.SUCCESS);
  }

  /**
   * 构建响应对象
   *
   * @param id identity
   * @param module 模块号
   * @param command 命令号
   * @param code 状态码
   * @return 响应对象
   */
  private static Response createResponse(int id, int module, int command,
      SystemStatusCode code) {
    return createResponse(id, module, command, code.getCode());
  }

  /**
   * 构建响应对象
   *
   * @param id identity
   * @param module 模块号
   * @param command 命令号
   * @param code 状态码
   * @return 响应对象
   */
  public static Response createResponse(int id, int module, int command, int code) {
    return createResponse(id, module, command, code, new byte[0]);
  }

  /**
   * 构建响应对象
   *
   * @param id identity
   * @param module 模块号
   * @param command 命令号
   * @param code 状态码
   * @param data 数据
   * @return 响应对象
   */
  public static Response createResponse(int id, int module, int command, int code, byte[] data) {
    return new Response(id, module, command, code, data);
  }

}
