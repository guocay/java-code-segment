package com.github.guocay.game;

import java.io.File;
import java.util.Objects;

/**
 * 工具类
 * @author GuoCay
 * @since 2023.05.01
 */
public class Util {

    /**
     * 获取文件
     * @param fileName 文件名
     * @return 文件
     */
    public static File getFile(String fileName){
        String filePath = Objects.requireNonNull(Util.class.getClassLoader().getResource(fileName)).getFile();
        return new File(filePath);
    }
}
