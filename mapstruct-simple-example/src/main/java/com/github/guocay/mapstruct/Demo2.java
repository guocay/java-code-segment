package com.github.guocay.mapstruct;

import java.util.StringJoiner;

/**
 * @author GuoCay
 * @since 2023.03.09
 */
public class Demo2 {

    private String name;

    private Integer age;

    public Demo2(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Demo1.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("age=" + age)
                .toString();
    }
}
