package com.github.guocay.lang;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JVM 关闭回调
 * @author GuoCay
 * @since 2023/3/2
 */
public class ShutDownHookUse {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutDownHookUse.class);

    public static void main(String[] args) {
        LOGGER.info("main() 开始运行...");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOGGER.info("执行JVM的关闭回调...")));
        LOGGER.info("main() 运行结束...");
    }
}
