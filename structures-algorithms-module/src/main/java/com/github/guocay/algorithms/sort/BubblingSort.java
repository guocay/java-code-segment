package com.github.guocay.algorithms.sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 冒泡排序
 * @author GuoCay
 * @since 2023.05.19
 */
public class BubblingSort {

	private static final Logger LOGGER = LoggerFactory.getLogger(BubblingSort.class);

	public static void main(String[] args) {
		int[] arr = {3,4,1,223,46,31,65};

		for (int i = 0; i < arr.length - 1; i++){
			for (int j = 0; j < arr.length - 1 - i; j++){
				// 相邻元素两两对比
				if (arr[j] > arr[j+1]) {
					// 元素交换
					var temp = arr[j+1];
					arr[j+1] = arr[j];
					arr[j] = temp;
				}
			}
		}
		LOGGER.info("Arrays.toString(arr) = {}", Arrays.toString(arr));
	}
}
