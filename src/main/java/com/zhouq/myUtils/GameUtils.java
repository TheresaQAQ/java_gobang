package com.zhouq.myUtils;

import com.zhouq.core.entity.Player;


/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/19 21:51
 */

public class GameUtils {
    public static int check(int[][] maps,int mapsX,int mapsY){
        int color = maps[mapsY][mapsX];
        boolean isWin;

        isWin = checkChess(1, 0, color,mapsY,mapsX,maps)
                || checkChess(0, 1, color,mapsY,mapsX,maps)
                || checkChess(1, 1, color,mapsY,mapsX,maps)
                || checkChess(1, -1, color,mapsY,mapsX,maps);
        if (isWin) {
            if (color == 1)
                return Player.BLACK_CHESS;
            else {
                return Player.WHITE_CHESS;
            }
        }
        return -1;
    }
    private static boolean checkChess(int xChange, int yChange, int color,int mapsY,int mapsX,int[][] maps) {
        boolean isWin = false;

        int count = 1, tempX = xChange, tempY = yChange;
        while ((mapsX + tempX) >= 0 && (mapsX + tempX) < 15 && (mapsY + tempY) >= 0 && (mapsY + tempY) < 15
                && maps[mapsY + tempY][mapsX + tempX] == color) {
            count++;
            if (tempX == 0 && tempY == 0)
                break;
            if (tempX > 0)
                tempX++;
            if (tempX < 0)
                tempX--;
            if (tempY > 0)
                tempY++;
            if (tempY < 0)
                tempY--;
        }
        tempX = xChange;
        tempY = yChange;
        while ((mapsX - tempX) >= 0 && (mapsX - tempX) < 15 && (mapsY - tempY) >= 0 && (mapsY - tempY) < 15
                && maps[mapsY - tempY][mapsX - tempX] == color) {
            count++;
            if (tempX == 0 && tempY == 0)
                break;
            if (tempX > 0)
                tempX++;
            if (tempX < 0)
                tempX--;
            if (tempY > 0)
                tempY++;
            if (tempY < 0)
                tempY--;
        }
        if (count >= 5) {
            return true;
        }
        return isWin;
    }
}
