package com.github.guocay.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * {@link Optional} 使用示例
 * @author GuoCay
 * @since 2023.05.20
 */
public class OptionalUse {

	private static final Logger LOGGER = LoggerFactory.getLogger(OptionalUse.class);

	private static final Object EXAMPLE = new Object();

	@SuppressWarnings("all")
	public static void main(String[] args) {
		// 创建一个Optional实例
		Optional<Object> maybeNull = Optional.of(EXAMPLE);
		Optional<Object> maybeNull1 = Optional.empty();
		Optional<Object> maybeNull2 = Optional.ofNullable(null);

		// 获取容器中的对象
		Object example = maybeNull.get();

		// 判断容器中的对象是否为空
		boolean exist = maybeNull.isPresent();

		// 容器非空则执行
		maybeNull.ifPresent(obj -> LOGGER.info("{}", obj));

		// 过滤, 当符合条件后返回本身. 否则返回一个空的容器
		Optional<Object> variable1 = maybeNull.filter(obj -> obj.equals(""));

		// 通过容器中的对象调用lambda表达式并接收返回.
		Optional<Integer> integer = maybeNull.map(Object::hashCode);

		// 当容器中的对象为空时, 设置值. 否则不做任何操作.
		Object variable2 = maybeNull.orElse(EXAMPLE);


	}
}
