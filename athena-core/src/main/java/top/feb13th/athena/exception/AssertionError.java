package top.feb13th.athena.exception;

import lombok.Getter;

/**
 * 断言错误
 *
 * @author zhoutaotao
 * @date 2019/8/21 14:12
 */
public class AssertionError extends RuntimeException {

  @Getter
  private Assertion assertion;

  public AssertionError(Assertion assertion) {
    this.assertion = assertion;
  }

  public AssertionError(String message, Assertion assertion) {
    super(message);
    this.assertion = assertion;
  }

  public AssertionError(String message, Throwable cause,
      Assertion assertion) {
    super(message, cause);
    this.assertion = assertion;
  }

  public AssertionError(Throwable cause, Assertion assertion) {
    super(cause);
    this.assertion = assertion;
  }

  public AssertionError(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace, Assertion assertion) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.assertion = assertion;
  }
}
