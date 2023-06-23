package com.zhouq.gui.basic;

import javax.swing.*;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/17 19:00
 */


public abstract class BasicPage extends JFrame {
    public Integer width;
    public Integer height;

    public abstract void initContent();

    public abstract void initActionListener();

    public abstract void addContent();

    public BasicPage(String title, Integer width, Integer height) {
        super(title);
        this.width = width;
        this.height = height;

        initContent();
        initActionListener();
        addContent();
        setLocationRelativeTo(null);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
