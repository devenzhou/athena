package top.feb13th.athena.core.exception;

/**
 * 断言错误码生成接口
 *
 * @author zhoutaotao
 * @date 2019/8/21 14:15
 */
public interface Assertion {

  /**
   * 错误码
   */
  int errorCode();

  /**
   * 错误码对应的描述
   */
  String desc();
}
