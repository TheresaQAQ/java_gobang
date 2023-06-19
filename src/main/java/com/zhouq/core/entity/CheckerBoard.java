package com.zhouq.core.entity;

import java.util.ArrayList;
import java.util.Arrays;

public class CheckerBoard {

    final static String WHITE_COLOR = "\33[0m";  //设置棋子的颜色为白
    final static String BLANK_PIECE = WHITE_COLOR + " · "; //默认棋子

    final static int DEFAULT_INIT = 16;
    final static int DEFAULT_LENGTH = DEFAULT_INIT; //默认棋盘长
    final static int DEFAULT_WIDTH = DEFAULT_INIT; //默认棋盘宽
    private int length;
    private int width;
    private String blankPiece = BLANK_PIECE;
    private ArrayList<String[]> body = new ArrayList<>();

    //空参构造使用默认值
    public CheckerBoard() {
        this(DEFAULT_LENGTH, DEFAULT_WIDTH);
    }

    public CheckerBoard(int length, int width) {

        this.length = length;
        this.width = width;

        //空白棋子填充棋盘二维数组
        for (int w = 0; w < width; w++) {
            String[] strings = new String[length];
            Arrays.fill(strings, BLANK_PIECE);
            this.body.add(strings);
        }

    }

    /**
     * @param x x坐标
     * @param y y坐标
     * @return 如果(x, y)为空白返回false 否则ture
     */
    public boolean isHasPiece(int x, int y) {

        if (body.get(y - 1)[x - 1].equals(BLANK_PIECE)) {
            return false;
        }
        return true;
    }

    public boolean isHasPiece(int[] i) {
        return this.isHasPiece(i[0], i[1]);
    }

    public boolean isHasPiece(String[] s) {
        //应该加try
        return this.isHasPiece(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
    }


    public String getLineString(int line) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < this.width + 1; i++) {
            stringBuilder.append(this.getPositionContent(i, line));
        }
        return stringBuilder.toString();
    }

    public String getRankString(int rank) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < this.length + 1; i++) {
            stringBuilder.append(this.getPositionContent(rank, i));
        }
        return stringBuilder.toString();
    }

    public String getObliqueUpwardString(int b) {
        StringBuilder stringBuilder = new StringBuilder();
        int j = b;
        for (int i = 1; i <= b; i++) {
            stringBuilder.append(this.getPositionContent(i, j));
            j--;
        }
        return stringBuilder.toString();
    }

    public String getObliqueDownwardString(int b) {
        if (b > this.length) {
            System.out.println("数据错误");
        }

        StringBuilder stringBuilder = new StringBuilder();
        int j = this.width;
        for (int i = b; i >= 1; i--) {
            stringBuilder.append(this.getPositionContent(i, j));
            j--;
        }
        return stringBuilder.toString();
    }

    public String getPositionContent(int x, int y) {
        return body.get(y - 1)[x - 1].replace(" ", "");
    }

    public String getPositionContent(int[] ints) {
        return this.getPositionContent(ints[0], ints[1]);
    }

    /**
     * 用于显示棋盘
     */
    public void show() {
        //打印第一行索引部分
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append("  ");
        for (int i = 0; i < this.length; i++) {
            if (i >= 10) {
                stringBuilder1.append((i + 1) + " ");
            } else {
                stringBuilder1.append(" " + (i + 1) + " ");
            }
        }
        System.out.println(stringBuilder1);


        //开始打印棋盘部分
        int count = 1; //行计数器
        for (String[] strings : body) {

            StringBuilder stringBuilder2 = new StringBuilder();
            if (count >= 10) {
                stringBuilder2.append(count);
            } else {
                stringBuilder2.append(count + " ");
            }
            for (int i = 0; i < strings.length; i++) {
                stringBuilder2.append(strings[i]);
            }
            System.out.println(stringBuilder2);

            count++;
        }
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public ArrayList<String[]> getBody() {
        return body;
    }

    public void setBody(ArrayList<String[]> body) {
        this.body = body;
    }

    public String getBlankPosition() {
        return blankPiece;
    }

    public void setBlankPosition(String blankPosition) {
        this.blankPiece = blankPosition;
    }
}