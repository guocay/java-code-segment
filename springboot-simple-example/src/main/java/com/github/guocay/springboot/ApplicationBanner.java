package com.github.guocay.springboot;

import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

/**
 * 自定义 Banner
 * @author Cay
 * @since 2023/2/25
 */
public class ApplicationBanner implements Banner {

    public static final ApplicationBanner INSTANCE = new ApplicationBanner();

    private static final String BANNER_MSG = """
               
               [....                         [..
             [.    [..                    [..   [..
            [..        [..  [..   [..    [..          [..    [..   [..
            [..        [..  [.. [..  [.. [..        [..  [..  [.. [..
            [..   [....[..  [..[..    [..[..       [..   [..    [...
             [..    [. [..  [.. [..  [..  [..   [..[..   [..     [..
              [.....     [..[..   [..       [....    [.. [...   [..
                                                              [..
            """;

    private ApplicationBanner(){}

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        out.println(BANNER_MSG);
    }
}
