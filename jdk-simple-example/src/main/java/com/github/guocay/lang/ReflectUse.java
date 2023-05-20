package com.github.guocay.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * 反射相关API 使用
 * @author GuoCay
 * @since 2023.05.20
 */
public class ReflectUse {

	private static final Class<Object> OBJECT_CLASS = Object.class;

	@SuppressWarnings("all")
	public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
		// 获取构造器
		Constructor<?>[] constructors = OBJECT_CLASS.getConstructors();

		// 获取字段
		Field field = OBJECT_CLASS.getField("");

		// 获取字段值
		Object fieldValue = field.get(new Object());

		// 设置字段值
		field.set(new Object(), null);

	}
}
