package top.feb13th.athena.message;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * id类型
 *
 * @author zhoutaotao
 * @date 2019/8/27 15:30
 */
@Getter
public enum IdentityType {

  PUSH(-1, "服务端主推"),
  ASYNCHRONOUS(0, "异步处理"),
  ;

  private int identity;
  private String desc;

  IdentityType(int identity, String desc) {
    this.identity = identity;
    this.desc = desc;
  }

  // 填充数据
  private static final Map<Integer, IdentityType> STATIC_MAP = new HashMap<>();

  static {
    for (IdentityType identityType : values()) {
      STATIC_MAP.put(identityType.getIdentity(), identityType);
    }
  }

  /**
   * 根据id获取id类型
   *
   * @param identity id
   * @return id类型
   */
  public static IdentityType get(int identity) {
    return STATIC_MAP.get(identity);
  }
}
