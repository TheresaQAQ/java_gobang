package com.zhouq.core;

import com.zhouq.core.entity.CheckerBoard;
import com.zhouq.core.entity.Player1;

import java.util.Scanner;

public class Running {

    public static void begin(CheckerBoard checkerBoard, Player1 player1, Player1 player2) {
        checkerBoard.show();
        while (true) {

            System.out.println("请玩家" + player1.getName() + "出棋");
            if (play(player1, checkerBoard, Running.positionInput(checkerBoard))) {
                break;
            }

            System.out.println("请玩家" + player2.getName() + "出棋");
            if (play(player2, checkerBoard, Running.positionInput(checkerBoard))) {
                break;
            }
        }
    }

    //下棋的方法，返回是否胜利
    public static boolean play(Player1 player, CheckerBoard checkerBoard, String[] position) {
        if (player.playPiece(checkerBoard, position)) {
            checkerBoard.show();
            System.out.println("恭喜" + player.getName() + "获胜！");
            return true;
        } else {
            checkerBoard.show();
            return false;
        }
    }

    //获取输入的方法
    public static String[] positionInput(CheckerBoard checkerBoard) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String[] position = scanner.nextLine().split(",");
            if (!checkerBoard.isHasPiece(position)) {
                return position;
            } else {
                System.out.println("这个位置已经有棋子了捏，重新输入吧");
            }
        }
    }
}
