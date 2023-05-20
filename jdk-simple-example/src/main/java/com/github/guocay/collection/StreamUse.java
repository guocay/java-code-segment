package com.github.guocay.collection;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * {@link java.util.stream.Stream}
 * @author GuoCay
 * @since 2023.05.20
 */
public class StreamUse {

	private static final String[] EXAMPLE_ARRAY = new String[]{"1", "2", "3", "4"};
	private static final List<String> EXAMPLE_LIST = Arrays.asList(EXAMPLE_ARRAY);

	@SuppressWarnings("all")
	public static void main(String[] args) {
		// 获取Stream流
		Stream<String> stream = EXAMPLE_LIST.stream();
		Stream<String> stream1 = EXAMPLE_LIST.parallelStream();
		Stream<String> stream2 = Arrays.stream(EXAMPLE_ARRAY);
		Stream<String> stream3 = Stream.of(EXAMPLE_ARRAY);
		IntStream chars = "123456".chars();

		// 无限流
		Stream<Integer> iterate = Stream.iterate(0, x -> x++);
		Stream<Integer> generate = Stream.generate(() -> 1);

		// 忽略第一个元素
		stream.skip(1);

		// 忽略最后一个元素
		stream.limit(1);

		// 过滤
		stream.filter(str -> str == "1");

		// 转换
		Stream<byte[]> stream4 = stream.map(String::getBytes);

		// 聚合
		String reduce1 = stream.reduce("", (reduce, item) -> reduce + item);
		List<String> collect = stream.collect(Collectors.toList());
	}
}
