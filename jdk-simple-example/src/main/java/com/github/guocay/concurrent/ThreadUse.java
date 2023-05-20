package com.github.guocay.concurrent;

/**
 * {@link Thread} 使用
 * @author GuoCay
 * @since 2023.05.20
 */
public class ThreadUse {

	@SuppressWarnings("all")
	public static void main(String[] args) {
		// 获取当前线程
		Thread thread = Thread.currentThread();

		// 获取线程名
		String threadName = thread.getName();
	}
}
