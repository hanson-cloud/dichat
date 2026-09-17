package com.diqin.cloud.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 * @author hanson
 */
@SpringBootApplication(scanBasePackages = {"${dichat.info.base-package}.server", "${dichat.info.base-package}.module"})
public class DichatServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DichatServerApplication.class, args);
    }
}
