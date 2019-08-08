package top.feb13th.athena.netty;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * netty 自动配置类
 *
 * @author zhoutaotao
 * @date 2019/8/8 20:31
 */
@Configuration
@EnableConfigurationProperties(NettyProperties.class)
public class NettyAutoConfiguration {

}
