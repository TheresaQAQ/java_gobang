package com.zhouq.gui;

import com.zhouq.core.entity.Player;
import com.zhouq.gui.basic.BasicPage;
import com.zhouq.nio.message.requests.PlayChessRequestsMessage;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
    private String message = "黑方先行";
    private final String whiteMessage = "无限制";
    private final String blackMessage = "无限制";// 界面上方信息，下方时间信息

    private int gameTime = 0;// 游戏时间限制（秒）
    private int blackTime = 0, whiteTime = 0;// 黑白方剩余时间
    public GamePage(String title, Integer width, Integer height){
        super(title, width, height);
        addContent();
    }
    public GamePage(String title){
        this(title,600,600);
    }

    @Override
    public void initContent() {
        offsetImg = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    }

    @Override
    public void initActionListener() {
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
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
//                        if (isBlack) {
//                            playChessMessage.setPlayerType(Player.BLACK_CHESS);
//                            //maps[mapsY][mapsX] = 1;
//                            //isBlack = false;
//                            message = "白色落子";
//                        } else {
//                            playChessMessage.setPlayerType(Player.WHITE_CHESS);
//                            //maps[mapsY][mapsX] = 2;
//                            //isBlack = true;
//                            message = "黑色落子";
//                        }
                        //checkGame();
                    }
                    channel.writeAndFlush(playChessMessage);
                }
                //repaint();
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
    }

    @Override
    public void addContent() {
        // 获取屏幕宽高
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        // 棋盘位置矩形
        chessBoardRect = new Rectangle(50, 120, 370, 370);

        setLocation((screenWidth - width) / 2, (screenHeight - height) / 2);
        repaint();
        // 设置背景
        try {
            bgImage = ImageIO.read(new File("D:\\code\\java\\java_gobang\\src\\main\\java\\com\\others\\img\\background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setVisible(true);
    }

    @Override
    public void paint(Graphics g1) {
        super.paint(offsetImg.getGraphics());
        Graphics g = offsetImg.getGraphics();
        // 绘制背景
        g.drawImage(bgImage, 45, 115, this);
//        // 绘制上方标题
//        g.setColor(Color.black);
//        g.setFont(new Font("楷体", Font.BOLD, 30));
//        g.drawString("游戏信息：" + message, 100, 75);
        // 绘制下方
        g.setColor(Color.gray);
        g.fillRect(50, 530, 200, 50);
        g.fillRect(300, 530, 200, 50);
        g.setColor(Color.black);
        g.setFont(new Font("宋体", Font.BOLD, 20));
        g.drawString("黑方时间：" + blackMessage, 60, 560);
        g.drawString("白方时间：" + whiteMessage, 310, 560);
        // 绘制棋盘线条
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
