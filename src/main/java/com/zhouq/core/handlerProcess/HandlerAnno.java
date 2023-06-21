package com.zhouq.core.handlerProcess;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * <p>
 *  在MessageHandler中标记方法处理的请求
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/21 12:05
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface HandlerAnno {
    int messageType();
}
