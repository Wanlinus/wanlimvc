package cn.wanlinus.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 控制器标识符,可以取代WebServlet自带的初始化参数(当然我写得很鸡肋)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Controller {
    //忘记这个拿来干什么了 ('-')
    String value() default "";

    //编码
    String encode() default "UTF-8";

    //初始化参数
    WebInitParam[] initParams() default {};

    //描述信息
    String description() default "";

}
