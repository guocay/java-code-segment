package com.github.guocay.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.logging.Logger;

/**
 * netty 的 处理器
 * @author GuoCay
 * @since 2023/3/2
 */
public class Handler1 extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = Logger.getLogger(Handler1.class.getName());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("进入第一个处理器...");
        LOGGER.info("第一个处理器打印" + msg.getClass());
        Attribute<Object> guo = ctx.channel().attr(AttributeKey.valueOf("guo"));
        if (guo.get() == null){
            guo.compareAndSet(null, "处理器一放入的值");
        }else {
            LOGGER.info("处理器一的guo.get() = " + guo.get());
        }

        ctx.fireChannelRead(123321);
    }
}
