package com.github.guocay.dubbo.provider;

import com.github.guocay.dubbo.metadata.IService;
import com.github.guocay.dubbo.metadata.ServiceImpl;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;

import static com.github.guocay.dubbo.ConstantPool.PROTOCOL_CONFIG;
import static com.github.guocay.dubbo.ConstantPool.REGISTRY_CONFIG;

/**
 * @author GuoCay
 * @since 2023/2/27
 */
public class ProviderClient {

    public static void main(String[] args) {
        ServiceConfig<IService> serviceConfig = new ServiceConfig<>();
        serviceConfig.setInterface(IService.class);
        serviceConfig.setRef(new ServiceImpl());

        DubboBootstrap bootstrap = DubboBootstrap.getInstance()
                .application("dubbo-demo-provider")
                .registry(REGISTRY_CONFIG)
                .protocol(PROTOCOL_CONFIG)
                .service(serviceConfig);
        // 启动
        bootstrap.start().await();

    }
}
