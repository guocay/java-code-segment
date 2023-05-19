package com.github.guocay.mapstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MapStruct 使用示例
 * @author GuoCay
 * @since 2023.03.09
 */
public class Application {

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        Demo1 demo1 = new Demo1("GuoCay", 18);

        Demo2 demo2 = Convertor.INSTANCE.toDemo2(demo1);
		LOGGER.info("demo2 = {}", demo2);
        Demo3 demo3 = Convertor.INSTANCE.toDemo3(demo1);
		LOGGER.info("demo3 = {}", demo3);
        Demo4 demo4 = Convertor.INSTANCE.toDemo4(demo1);
		LOGGER.info("demo4 = {}", demo4);
    }
}
