package com.zhouq.core.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    private boolean hasWinner;
    private List<PlayChessHistory> playChessHistory = new ArrayList<>();

    public Player getPlay(int i) {
        return i == Player.BLACK_CHESS ? this.blackPlayer : whitePlayer;
    }
}
