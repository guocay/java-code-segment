package com.github.guocay.springboot;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.util.logging.Logger;

/**
 * @author GuoCay
 * @since 2023/2/25
 */
public class CustomSpringListener implements ApplicationListener<ApplicationStartedEvent> {

    private static final Logger LOGGER = Logger.getLogger(CustomSpringListener.class.getName());

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        LOGGER.info("应用启动...");
    }
}
