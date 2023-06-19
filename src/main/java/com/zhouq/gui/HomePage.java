package com.zhouq.gui;

import com.zhouq.gui.basic.BasicPage;
import lombok.Data;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/17 19:06
 */

public class HomePage extends BasicPage {
    private JButton createGame;
    private JButton joinGame;

    public HomePage(String title, Integer width, Integer height) {
        super(title, width, height);
    }

    @Override
    public void initContent() {
        createGame = new JButton("创建游戏");
        joinGame = new JButton("加入游戏");
    }

    @Override
    public void initActionListener() {
        createGame.addActionListener(button -> {
            new CreateGamePage("创建游戏", 150, 85);
            this.setVisible(false);
        });
        joinGame.addActionListener(button -> {
            new JoinGamePage("加入游戏", 150, 110);
            this.setVisible(false);
        });
    }

    @Override
    public void addContent() {

        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(Box.createHorizontalGlue());
        horizontalBox.add(createGame);
        horizontalBox.add(Box.createHorizontalGlue());
        horizontalBox.add(joinGame);
        horizontalBox.add(Box.createHorizontalGlue());

        Container contentPane = this.getContentPane();


        contentPane.add(horizontalBox,BorderLayout.CENTER);
    }
}
