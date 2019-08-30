package top.feb13th.athena.core.support;

import lombok.Getter;

/**
 * 系统状态码
 *
 * @author zhoutaotao
 * @date 2019/8/16 11:30
 */
public enum SystemStatusCode {

  ERROR(100, "系统错误"),

  SUCCESS(200, "成功"),
  UNAUTHORIZED(403, "未授权"),
  RESOURCE_NOT_FOUND(404, "资源找不到"),
  FAILURE(500, "失败"),
  ;

  // 状态码
  @Getter
  private int code;
  // 描述
  @Getter
  private String desc;

  SystemStatusCode(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }
}
