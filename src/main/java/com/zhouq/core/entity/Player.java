package com.zhouq.core.entity;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/19 18:31
 */
@Getter
@Setter
@AllArgsConstructor
public class Player {
    private Integer playType;
    private ChannelHandlerContext channel;

    public static int BLACK_CHESS = 0;
    public static int WHITE_CHESS = 1;
    public static HashMap<Integer, String> NAMES = new HashMap<>();

    static {
        NAMES.put(BLACK_CHESS, "黑方");
        NAMES.put(WHITE_CHESS, "白方");
    }
}
