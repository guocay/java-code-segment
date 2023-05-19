package com.github.guocay.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * {@link LocalDateTime}
 * @author GuoCay
 * @since 2023.05.19
 */
public class LocalDateTimeUse {

	private static final Logger LOGGER = LoggerFactory.getLogger(LocalDateTimeUse.class);

	public static void main(String[] args) {
		// 获取当前时间戳
		long currentTimeMillis = System.currentTimeMillis();
		LOGGER.info("当前Unix时间戳的值是: {}", currentTimeMillis);

		// 获取当前日期时间
		LocalDateTime now = LocalDateTime.now();

		// 将日期格式化输出
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
		String format = now.format(formatter);
		LOGGER.info("当前日期时间是: {}", format);

	}
}
