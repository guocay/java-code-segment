package com.github.guocay.springboot;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.logging.Logger;

/**
 *
 * @author GuoCay
 * @since 2023/2/25
 */
public class CustomSpringInitializer implements ApplicationContextInitializer {

    private static final Logger LOGGER = Logger.getLogger(CustomSpringInitializer.class.getName());

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        LOGGER.info("初始化上下文");
    }
}
