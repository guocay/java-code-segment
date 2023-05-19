package com.github.guocay.lang;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * {@link Unsafe} 使用
 * @author GuoCay
 * @since 2023.05.19
 */
public class UnsafeUse {

	public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
		Field field = Unsafe.class.getDeclaredField("theUnsafe");
		field.setAccessible(true);
		Unsafe unsafe = (Unsafe) field.get(null);

		// 内存格栅
		unsafe.fullFence();
		unsafe.storeFence();
		unsafe.loadFence();

	}
}
