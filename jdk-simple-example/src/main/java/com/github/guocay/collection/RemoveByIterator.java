package com.github.guocay.collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * 通过迭代器删除集合元素
 * @author GuoCay
 * @since 2023.05.19
 */
public class RemoveByIterator {

	private static final Logger LOGGER = LoggerFactory.getLogger(RemoveByIterator.class);

	public static void main(String[] args) {
		ArrayList<String> strings = new ArrayList<>(Arrays.asList("张三", "李四", "王五", "李四2", "赵六"));
		Iterator<String> iterator = strings.iterator();

		// 在迭代的过程中删除元素
		while (iterator.hasNext()){
			String next = iterator.next();
			if (next.startsWith("李"))
				iterator.remove();
		}
		LOGGER.info(strings.toString());
	}
}
