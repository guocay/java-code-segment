package com.github.guocay.dubbo.consumer;

import com.github.guocay.dubbo.metadata.IService;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;

import java.util.logging.Logger;

import static com.github.guocay.dubbo.ConstantPool.PROTOCOL_CONFIG;
import static com.github.guocay.dubbo.ConstantPool.REGISTRY_CONFIG;

/**
 * @author GuoCay
 * @since 2023/2/27
 */
public class ConsumerClient {

    private static final Logger LOGGER = Logger.getLogger(ConsumerClient.class.getName());

    public static void main(String[] args) {
        ReferenceConfig<IService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setInterface(IService.class);

        DubboBootstrap bootstrap = DubboBootstrap.getInstance()
                .application("dubbo-demo-consumer")
                .registry(REGISTRY_CONFIG)
                .protocol(PROTOCOL_CONFIG)
                .reference(referenceConfig);
        bootstrap.start();

        IService iService = referenceConfig.get();
        String guocay = iService.sayHello("guocay");
        LOGGER.info("返回的消息是: " + guocay);

    }
}
