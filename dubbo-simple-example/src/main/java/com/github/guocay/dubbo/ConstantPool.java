package com.github.guocay.dubbo;

import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;

/**
 * @author GuoCay
 * @since 2023/2/27
 */
public final class ConstantPool {

    private static final String ZOOKEEPER_HOST = System.getProperty("zookeeper.address", "127.0.0.1");
    private static final String ZOOKEEPER_PORT = System.getProperty("zookeeper.port", "2181");
    private static final String ZOOKEEPER_ADDRESS = "zookeeper://" + ZOOKEEPER_HOST + ":" + ZOOKEEPER_PORT;

    public static final RegistryConfig REGISTRY_CONFIG = new RegistryConfig(ZOOKEEPER_ADDRESS);
    public static final ProtocolConfig PROTOCOL_CONFIG = new ProtocolConfig("dubbo");
}
