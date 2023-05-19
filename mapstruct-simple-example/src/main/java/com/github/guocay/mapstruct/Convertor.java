package com.github.guocay.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author GuoCay
 * @since 2023.03.09
 */
@Mapper
public interface Convertor {
    Convertor INSTANCE = Mappers.getMapper(Convertor.class);

    Demo2 toDemo2(Demo1 demo1);

    Demo3 toDemo3(Demo1 demo1);

    Demo4 toDemo4(Demo1 demo1);

    Demo3 bb(Demo1 demo1);
    Demo3 cc(Demo1 demo1);

}
