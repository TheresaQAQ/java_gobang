package com.zhouq.core.handlerProcess;

import com.zhouq.netty.message.basic.Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * <p>
 * 根据Message类型加载MessageHandler中对应的方法
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/21 12:05
 */
@Slf4j
public class FunctionLoader {
    public static void execute(String clazzName, Message message, ChannelHandlerContext ctx, Object... objects) {
        try {
            log.debug("接收到{}消息",Message.getMessageClass(message.getMessageType()).toString());
            log.debug("加载类:" + clazzName);
            int messageType = message.getMessageType();
            Class<?> clazz = Class.forName(clazzName);
            Method[] methods = clazz.getDeclaredMethods();
            Arrays.stream(methods)
                    .filter(method -> method.isAnnotationPresent(HandlerAnno.class))
                    .filter(method -> {
                        HandlerAnno annotation = method.getAnnotation(HandlerAnno.class);
                        return annotation.messageType() == messageType;
                    })
                    .peek(method -> method.setAccessible(false))
                    .findFirst().ifPresent(method -> {
                        log.debug("执行方法:{}:{}", clazzName, method.getName());
                        Object obj = null;
                        try {
                            Constructor<?> constructor = clazz.getConstructor();
                            obj = constructor.newInstance();
                        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                                 InvocationTargetException e) {
                            log.error("找不到类" + clazzName);
                        }
                        try {
                            Object[] params = Stream.concat(
                                    Stream.of(message, ctx),
                                    Arrays.stream(objects)
                            ).toArray();
                            //这里要把全部参数转成一个Object[]才能正常传参
                            method.invoke(obj, params);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalArgumentException e) {
                            log.error("参数传递错误，加载{}方法失败", method.getName());
                        }
                    });
        } catch (ClassNotFoundException e) {
            log.error("找不到类" + clazzName);
        }

    }
}
