package com.equipment.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

/**
 * @Author: JavaTansanlin
 * @Description: 二维码处理工具类
 * @Date: Created in 10:54 2019/4/10
 * @Modified By:
 */
public class QRCodeUtil {

    public static void main(String[] args) {
        try {
            BufferedImage img=  ImageIO.read(new File("E://WX-201894646571929459575812.jpg"));
            Color color = new Color(255,255,255);
            pressText("赖凯这煞笔" ,"e://111.jpg",img,color ,24 ,400 ,400);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @param pressText 文字
     * @param newImg    带文字的图片
     * @param image     需要添加文字的图片
     * @param color
     * @param fontSize
     * @为图片添加文字
     */
    public static void pressText(String pressText, String newImg, BufferedImage image, Color color, int fontSize, int newWidth, int newHeight) {

        try {
            BufferedImage outImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = outImage.createGraphics();
            outImage = graphics2D.getDeviceConfiguration().createCompatibleImage(newWidth, newHeight);
            graphics2D = outImage.createGraphics();
            graphics2D.setBackground(color);
            graphics2D.clearRect(0,0,newWidth,newHeight);
            int imageW = image.getWidth();
            int imageH = image.getHeight();
            // 设置图片居中显示
            graphics2D.drawImage(image, (newWidth - imageW) / 2,
                    (newHeight - imageH) / 2, null);
            //文字位置
            graphics2D.setColor(new Color(0,0,0));
            graphics2D.setFont(new Font("粗体", Font.BOLD, fontSize));
            graphics2D.drawString(pressText, 140, 375);
            graphics2D.dispose();

            FileOutputStream out = new FileOutputStream(newImg);
            ImageIO.write(outImage, "JPEG", out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
