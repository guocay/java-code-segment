package com.github.guocay.algorithms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 汉罗塔 实现
 * @author GuoCay
 * @since 2023.03.22
 */
public class HanoiTower {

	private static final Logger LOGGER = LoggerFactory.getLogger(HanoiTower.class);

    private static int num = 1;

    public static void main(String[] args) {
        hanoiTower(30, 'A', 'B', 'C');
    }

    private static void hanoiTower(int index, char primitive, char auxiliary, char target) {
        if (index == 1){
			LOGGER.info("第{}次移动: {}号盘子从 {} 到 {}\n", num++, index, primitive, target);
        } else {
            hanoiTower(index - 1, primitive, target, auxiliary);
			LOGGER.info("第{}次移动: {}号盘子从 {} 到 {}\n", num++, index, primitive, target);
            hanoiTower(index - 1, auxiliary, primitive, target);
        }
    }
}
