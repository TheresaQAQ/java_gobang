package com.zhouq.gui;

import com.zhouq.gui.basic.BasicPage;
import com.zhouq.nio.client.SocketClient;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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


public class JoinGamePage extends BasicPage {
    private JLabel ipLabel;
    private JLabel portLabel;
    private JTextField ipTextField;
    private JTextField portTextField;
    private JLabel idLabel;
    private JTextField idTextField;
    private JButton button;

    public JoinGamePage(String title, Integer width, Integer height) {
        super(title, width, height);
    }

    @Override
    public void initContent() {
        ipLabel = new JLabel("IP地址:");
        portLabel = new JLabel("端口号:");
        idLabel = new JLabel("对局号:");
        ipTextField = new JTextField("127.0.0.1");
        portTextField = new JTextField("8848");
        idTextField = new JTextField();
        button = new JButton("确认");
    }

    @Override
    public void initActionListener() {
        button.addActionListener(button -> {
            //TODO 应该做一下输入校验的
            new Thread(() ->
                    SocketClient.runClient(
                            ipTextField.getText(),
                            Integer.parseInt(portTextField.getText()),
                            Integer.parseInt(idTextField.getText()),
                            this))
                    .start();
        });
    }

    @Override
    public void addContent() {
        Box verticalBox = Box.createVerticalBox();
        Box ipHorizontalBox = Box.createHorizontalBox();
        ipHorizontalBox.add(ipLabel, BorderLayout.WEST);
        ipHorizontalBox.add(ipTextField, BorderLayout.EAST);
        verticalBox.add(ipHorizontalBox);

        Box portHorizontalBox = Box.createHorizontalBox();
        portHorizontalBox.add(portLabel, BorderLayout.WEST);
        portHorizontalBox.add(portTextField, BorderLayout.EAST);
        verticalBox.add(portHorizontalBox);

        Box idHorizontalBox = Box.createHorizontalBox();
        idHorizontalBox.add(idLabel, BorderLayout.WEST);
        idHorizontalBox.add(idTextField, BorderLayout.EAST);
        verticalBox.add(idHorizontalBox);

        verticalBox.add(button, BorderLayout.CENTER);

        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(verticalBox);

        this.getContentPane().add(horizontalBox);
    }
}
