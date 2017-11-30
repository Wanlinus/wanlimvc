package cn.wanlinus.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于定义初始化参数(和)
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WebInitParam {
    //初始化参数名
    String name();

    //初始化参数值
    String value();

    //描述信息
    String description() default "";
}
