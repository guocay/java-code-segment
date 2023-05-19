package com.github.guocay.dubbo.metadata;

import java.util.logging.Logger;

/**
 * @author GuoCay
 * @since 2023/2/27
 */
public class ServiceImpl implements IService {

    private static final Logger LOGGER = Logger.getLogger(ServiceImpl.class.getName());

    @Override
    public String sayHello(String name) {
        LOGGER.info("我接受到了一个请求!");
        return "hello " + name;
    }
}
