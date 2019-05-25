package com.woodyhi.retrofit.converter.composite;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import retrofit2.Converter;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 在请求方法上设置请求体转换工厂
 * @auth June.C
 * @date 2019/05/23
 */

@Documented
@Target({METHOD})
@Retention(RUNTIME)
public @interface RequestConverter {
    Class<? extends Converter.Factory> value();
}