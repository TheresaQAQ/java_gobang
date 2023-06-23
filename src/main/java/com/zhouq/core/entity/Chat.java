package com.zhouq.core.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/23 1:16
 */
@Getter
@Setter
public class Chat {
    private LocalDateTime time;
    private String role;
    private String content;

    public static String SYSTEM = "系统";
    public static String WHITE = "白方";
    public static String BLACK = "黑方";

    public Chat(LocalDateTime time,String role, String content) {
        this.time=time;
        this.role = role;
        this.content = content;
    }

    public Chat(String role, String content) {
        this(LocalDateTime.now(),role,content);
    }

    public Chat(String content) {
        this(Chat.SYSTEM, content);
    }

    @Override
    public String toString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String s = time.format(dateTimeFormatter);
        return "[" + s + "]" + role + ":" + content;
    }
}
