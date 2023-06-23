package com.zhouq.gui;

import com.zhouq.core.entity.Chat;
import com.zhouq.core.entity.Player;
import com.zhouq.gui.basic.BasicPage;
import com.zhouq.netty.message.requests.ChatRequestsMessage;
import com.zhouq.netty.message.requests.PlayChessRequestsMessage;
import com.zhouq.netty.message.requests.RetractChessRequestsMessage;
import com.zhouq.netty.message.requests.SuePeaceRequestsMessage;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/17 19:03
 */

@Getter
@Setter
@Slf4j
public class GamePage extends BasicPage {
    private Channel channel;
    private Integer gameId;
    private Integer playerType;

    private int mouseX = 0, mouseY = 0, mapsX = 0, mapsY = 0;// 鼠标坐标，鼠标在地图中的位置
    private BufferedImage bgImage = null;// 背景图片
    private final int chessBoardItemWidth = 25;// 棋盘每一小格的大小
    private Rectangle chessBoardRect = null;// 棋盘所在矩形
    private BufferedImage offsetImg;
    private int[][] maps = new int[15][15];// 0无棋子，1黑子，2白子
    private boolean isBlack = true;// 是否是黑方的回合
    private String message = "白方出棋";

    private JButton retractChess;
    private JButton sueSpace;
    private JButton sendMsg;
    private JTextArea chatArea;
    private JTextField textInput;

    public GamePage(String title, Integer width, Integer height) {
        super(title, width, height);
        addContent();
    }

    public GamePage(String title) {
        this(title, 600, 550);
    }

    @Override
    public void initContent() {
        offsetImg = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        retractChess = new JButton("悔棋");
        sueSpace = new JButton("求和");
        sendMsg = new JButton("发送");
        textInput = new JTextField();
        chatArea = new JTextArea();
    }

    @Override
    public void addContent() {
        // 获取屏幕宽高
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        // 棋盘位置矩形
        chessBoardRect = new Rectangle(50, 120, 370, 370);
        this.setLocation((screenWidth - width) / 2, (screenHeight - height) / 2);

        this.setLayout(null);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(null);
        jPanel.setSize(200, 100);
        sueSpace.setLayout(null);
        sueSpace.setBounds(425, 429, 60, 40);
        retractChess.setLayout(null);
        retractChess.setBounds(515, 429, 60, 40);
        this.getContentPane().add(sueSpace);
        this.getContentPane().add(retractChess);


        textInput.setLayout(null);
        textInput.setBounds(425,401,110,25);
        this.getContentPane().add(textInput);


        sendMsg.setFont(new Font("宋体", Font.BOLD, 8));
        sendMsg.setLayout(null);
        sendMsg.setBounds(525,400,55,25);
        this.getContentPane().add(sendMsg);


        chatArea.setEditable(false);
        chatArea.setFont(new Font("宋体", Font.BOLD, 10));
        chatArea.setLayout(null);
        chatArea.setBounds(425,90,155,300);
        this.getContentPane().add(chatArea);



        repaint();
        // 设置背景
        try {
            bgImage = ImageIO.read(new File("src/main/resources/source/background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setVisible(true);

    }

    @Override
    public void initActionListener() {
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                log.debug("棋盘被点击");
                PlayChessRequestsMessage playChessMessage = new PlayChessRequestsMessage();
                mouseX = e.getX();
                mouseY = e.getY();
                // 鼠标落子
                if (chessBoardRect.contains(mouseX, mouseY)) {
                    mapsX = (mouseX - 50) / chessBoardItemWidth;
                    mapsY = (mouseY - 120) / chessBoardItemWidth;
                    if (maps[mapsY][mapsX] == 0) {
                        playChessMessage.setMapX(mapsX);
                        playChessMessage.setMapY(mapsY);
                        playChessMessage.setGameId(gameId);
                        playChessMessage.setPlayerType(playerType);
                    }
                    channel.writeAndFlush(playChessMessage);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        //悔棋
        retractChess.addActionListener(button -> {
            RetractChessRequestsMessage requestsMessage = new RetractChessRequestsMessage();
            requestsMessage.setFrom(this.playerType);
            requestsMessage.setTo(this.playerType == Player.WHITE_CHESS ? Player.BLACK_CHESS : Player.WHITE_CHESS);
            requestsMessage.setGameId(this.getGameId());

            log.debug("to:"+Player.NAMES.get(requestsMessage.getTo()));
            log.debug("from:"+Player.NAMES.get(requestsMessage.getFrom()));

            channel.writeAndFlush(requestsMessage);
        });

        //求和
        sueSpace.addActionListener(button -> {
            SuePeaceRequestsMessage requestsMessage = new SuePeaceRequestsMessage();
            requestsMessage.setFrom(this.playerType);
            requestsMessage.setTo(this.playerType == Player.WHITE_CHESS ? Player.BLACK_CHESS : Player.WHITE_CHESS);
            requestsMessage.setGameId(this.getGameId());

            log.debug("to:"+Player.NAMES.get(requestsMessage.getTo()));
            log.debug("from:"+Player.NAMES.get(requestsMessage.getFrom()));
            channel.writeAndFlush(requestsMessage);
        });

        sendMsg.addActionListener(button ->{
            String text = this.textInput.getText();
            if (text.isBlank()) {
                return;
            }
            Chat chat = new Chat(LocalDateTime.now(),Player.NAMES.get(this.playerType), text);
            ChatRequestsMessage requestsMessage = new ChatRequestsMessage(chat);
            requestsMessage.setGameId(gameId);
            this.textInput.setText("");
            channel.writeAndFlush(requestsMessage);
        });
    }


    @Override
    public void paint(Graphics g1) {
        super.paint(offsetImg.getGraphics());
        Graphics g = offsetImg.getGraphics();
        // 绘制背景
        g.drawImage(bgImage, 45, 115, this);

        g.setColor(Color.black);
        g.setFont(new Font("楷体", Font.BOLD, 30));
        g.drawString("游戏信息：" + message, 100, 75);
        for (int i = 0; i < 15; i++) {
            g.drawLine(60, 130 + i * chessBoardItemWidth, 410, 130 + i * chessBoardItemWidth);
            g.drawLine(60 + i * chessBoardItemWidth, 130, 60 + i * chessBoardItemWidth, 480);
        }
        // 标注点位
        g.fillOval(131, 200, 8, 8);
        g.fillOval(331, 200, 8, 8);
        g.fillOval(131, 400, 8, 8);
        g.fillOval(331, 400, 8, 8);
        g.fillOval(230, 299, 10, 10);

        // 绘制棋子
        for (int j = 0; j < maps.length; j++) {
            for (int i = 0; i < maps[0].length; i++) {
                if (maps[j][i] == 1) {
                    g.setColor(Color.black);
                    g.fillOval(50 + i * chessBoardItemWidth, 120 + j * chessBoardItemWidth, 20, 20);
                }
                if (maps[j][i] == 2) {
                    g.setColor(Color.white);
                    g.fillOval(50 + i * chessBoardItemWidth, 120 + j * chessBoardItemWidth, 20, 20);
                }
            }
        }
        // 双缓冲解决屏幕闪烁
        g1.drawImage(offsetImg, 0, 0, this);
    }
}
