package com.zhouq.core.entity;

import java.util.ArrayList;

public class Player1 {

    final public static String RED_COLOR = "\33[1m\33[31m";
    final public static String GREEN_COLOR = "\33[1m\33[32m";
    private String piece;
    private String name;
    private String color;


    //获取到输入后是字符串，转为int
    public boolean playPiece(CheckerBoard checkerBoard, String[] position) {
        int x = Integer.parseInt(position[0]);
        int y = Integer.parseInt(position[1]);
        return this.playPiece(checkerBoard, x, y);
    }

    /**
     * @param checkerBoard 玩家的棋下到哪个棋盘
     * @param x            x坐标
     * @param y            y坐标
     * @return 落子后的新棋盘对象
     */
    public boolean playPiece(CheckerBoard checkerBoard, int x, int y) {
        ArrayList<String[]> body;
        body = checkerBoard.getBody();

        body.get(y - 1)[x - 1] = this.color + this.piece;
        checkerBoard.setBody(body);

        return this.isWinner(checkerBoard);
    }

    //是否胜利，返回布尔
    public boolean isWinner(CheckerBoard checkerBoard) {
        ArrayList<String[]> body = checkerBoard.getBody();

        //目标字符串(五个棋子) 用于字符串匹配 kmp
        StringBuilder toSearchString = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            toSearchString.append(this.color + this.piece.replace(" ", ""));
        }
        //同一行
        for (int i = 1; i < checkerBoard.getWidth() + 1; i++) {
            if (checkerBoard.getLineString(i).indexOf(toSearchString.toString()) != -1) {
                return true;
            }
        }

        //同一列
        for (int i = 1; i < checkerBoard.getLength() + 1; i++) {
            if (checkerBoard.getRankString(i).indexOf(toSearchString.toString()) != -1) {
                return true;
            }
        }

        //斜向上
        for (int i = 5; i <= checkerBoard.getWidth(); i++) {
            if (checkerBoard.getObliqueUpwardString(i).indexOf(toSearchString.toString()) != -1) {
                return true;
            }
        }

        //斜向下
        for (int i = 5; i <= checkerBoard.getLength(); i++) {
            if (checkerBoard.getObliqueDownwardString(i).indexOf(toSearchString.toString()) != -1) {
                return true;
            }
        }

        //不满足上述情况
        return false;
    }

    public Player1() {
    }
    public Player1(String piece, String name, String color) {
        this.piece = " " + piece + " ";
        this.name = name;
        this.color = color;
    }

    public String getPiece() {
        return piece;
    }

    public void setPiece(String piece) {
        this.piece = this.color + " " + piece + " ";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
