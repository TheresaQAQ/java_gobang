package com.zhouq.core.entity;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

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
public class Game {
    private int[][] maps = new int[15][15];
    private Player whitePlayer;
    private Player blackPlayer;
    private boolean isBlack;
}
