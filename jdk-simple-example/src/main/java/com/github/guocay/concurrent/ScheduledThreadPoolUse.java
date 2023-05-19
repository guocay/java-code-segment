package com.github.guocay.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link ScheduledThreadPoolExecutor} 使用
 * 用于演示 调度线程池的任务运行顺序与机制
 * @author GuoCay
 * @since 2023.03.16
 */
public class ScheduledThreadPoolUse {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledThreadPoolUse.class);

    private static final AtomicInteger integer = new AtomicInteger(0);

	private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(10);

    public static void main(String[] args) {
		EXECUTOR.allowCoreThreadTimeOut(true);
		EXECUTOR.schedule(() -> {
			LOGGER.info("[{}]: 执行延迟任务1", Thread.currentThread().getId());
            while (true){
				LOGGER.info("integer.getAndIncrement() = {}", integer.getAndIncrement());
            }
        }, 10L, TimeUnit.SECONDS);

		EXECUTOR.schedule(() -> {
            integer.set(0);
			LOGGER.info("[{}]: 执行延迟任务2", Thread.currentThread().getId());
        }, 15L, TimeUnit.SECONDS);
    }
}
