package com.github.guocay.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring 启动类
 * @author GuoCay
 * @since 2023/3/2
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setBanner(ApplicationBanner.INSTANCE);
        app.addInitializers(new CustomSpringInitializer());
        app.addListeners(new CustomSpringListener());
        app.run(args);
    }

}
