package com.zhouq.gui;

import com.zhouq.gui.basic.BasicPage;
import com.zhouq.nio.client.SocketClient;
import com.zhouq.nio.server.SocketServer;

import javax.swing.*;
import java.awt.*;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/17 19:03
 */

public class CreateGamePage extends BasicPage {
    private JLabel portLabel;
    private JTextField portTextField;
    private JButton button;

    public CreateGamePage(String title, Integer width, Integer height) {
        super(title, width, height);
    }

    @Override
    public void initContent() {
        portLabel = new JLabel("端口号:");
        portTextField = new JTextField("8848");
        button = new JButton("确认");
    }

    @Override
    public void initActionListener() {
        button.addActionListener(button -> {
            new Thread(() -> SocketServer.runServer(Integer.parseInt(portTextField.getText()))).start();
            new Thread(() ->
                    SocketClient.runClient(
                            "localhost",
                            Integer.parseInt(portTextField.getText()),
                            null,
                            this))
                    .start();
        });
    }

    @Override
    public void addContent() {

        Box verticalBox = Box.createVerticalBox();

        Box portHorizontalBox = Box.createHorizontalBox();
        portHorizontalBox.add(portLabel, BorderLayout.WEST);
        portHorizontalBox.add(portTextField, BorderLayout.EAST);
        verticalBox.add(portHorizontalBox);

        verticalBox.add(button, BorderLayout.CENTER);

        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(verticalBox);

        this.getContentPane().add(horizontalBox);
    }
}
