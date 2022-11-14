package io.github.yanshenwei.cos.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**********************************
 * @Author YSW
 * @Description
 * @Date 2022/11/8 - 15:26
 **********************************/

@Configuration
@ComponentScan("io.github.yanshenwei.cos")
@EnableConfigurationProperties(CosConstants.class)
public class CosAutoConfiguration {
}
