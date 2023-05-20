package com.github.guocay.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * {@link LocalDateTime}
 * @author GuoCay
 * @since 2023.05.19
 */
public class LocalDateTimeUse {

	private static final Logger LOGGER = LoggerFactory.getLogger(LocalDateTimeUse.class);

	@SuppressWarnings("all")
	public static void main(String[] args) {
		// 获取当前时间戳
		long currentTimeMillis = System.currentTimeMillis();
		LOGGER.info("当前Unix时间戳的值是: {}", currentTimeMillis);

		// 日期格式化
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

		// 获取当前日期时间
		LocalDateTime now = LocalDateTime.now();

		// 设置指定的日期时间
		LocalDateTime variable1 = LocalDateTime.of(2023, 5, 30, 15, 2, 0);
		LocalDateTime variable2 = LocalDateTime.parse("2023-05-30 15:02:00.000", formatter);

		// 获取本月第一天
		LocalDateTime variable3 = now.with(TemporalAdjusters.firstDayOfMonth());
		LocalDateTime variable4 = now.withDayOfMonth(1);

		// 获取当前日期后一天
		LocalDateTime variable5 = now.plusDays(1);

		// 获取当前日期前一天
		LocalDateTime variable6 = now.minusDays(1);

		// 获取两个日期间的间隔
		long variable7 = now.until(variable6, ChronoUnit.DAYS);
		Period variable8 = Period.between(now.toLocalDate(), variable6.toLocalDate());
		long variable9 = ChronoUnit.DAYS.between(now.toLocalDate(), variable6.toLocalDate());
		Duration variable10 = Duration.between(now.toLocalDate(), variable6.toLocalDate());

		// 时区 默认时区,芝加哥时区
		Clock systemDefaultClock = Clock.systemDefaultZone();
		Clock variable111 = Clock.system(ZoneId.of(ZoneId.SHORT_IDS.get("CST")));

		// Date -> LocalDateTime
		Date date = new Date();
		LocalDateTime variable112 = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime variable113 = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

		// 将日期格式化输出
		String format = now.format(formatter);
		LOGGER.info("当前日期时间是: {}", format);

	}
}
