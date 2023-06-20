package com.zhouq.core.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/21 0:53
 */
@Getter
@Setter
@AllArgsConstructor
public class PlayChessHistory {
    private int mapX;
    private int mapY;
    private int playType;
}
