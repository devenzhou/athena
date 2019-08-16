package top.feb13th.athena.support;

import lombok.Getter;

/**
 * 系统状态码
 *
 * @author zhoutaotao
 * @date 2019/8/16 11:30
 */
public enum SystemStatusCode {

  SUCCESS(200, "success"),
  RESOURCE_NOT_FOUND(404, "Resource not found"),
  FAILURE(500, "failure"),
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
