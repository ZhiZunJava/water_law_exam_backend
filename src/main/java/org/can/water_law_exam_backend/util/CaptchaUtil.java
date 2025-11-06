package org.can.water_law_exam_backend.util;

import org.can.water_law_exam_backend.vo.CaptchaVO;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * 验证码生成工具类
 *
 * @author 程安宁
 * @date 2025/11/06
 */
public class CaptchaUtil {

    /**
     * 验证码字符集（去除容易混淆的字符：0OoIl1）
     */
    private static final String CHARACTERS = "23456789ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
    
    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 4;
    
    /**
     * 图片宽度
     */
    private static final int WIDTH = 120;
    
    /**
     * 图片高度
     */
    private static final int HEIGHT = 40;
    
    /**
     * 干扰线数量
     */
    private static final int LINE_COUNT = 5;
    
    /**
     * 噪点数量
     */
    private static final int NOISE_COUNT = 50;

    /**
     * 生成验证码
     *
     * @return 验证码结果（包含验证码文本和图片Base64编码）
     */
    public static CaptchaVO generate() {
        // 生成随机验证码文本
        String code = generateCode();
        
        // 生成验证码图片
        String imageBase64 = generateImage(code);
        
        return new CaptchaVO(code, imageBase64);
    }

    /**
     * 生成随机验证码文本
     *
     * @return 验证码文本
     */
    private static String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        return code.toString();
    }

    /**
     * 生成验证码图片并转换为Base64编码
     *
     * @param code 验证码文本
     * @return Base64编码的图片字符串
     */
    private static String generateImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        Random random = new Random();

        // 设置背景色（浅色）
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制干扰线
        for (int i = 0; i < LINE_COUNT; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.setColor(getRandomColor(150, 200));
            g.drawLine(x1, y1, x2, y2);
        }

        // 绘制噪点
        for (int i = 0; i < NOISE_COUNT; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g.setColor(getRandomColor(100, 200));
            g.fillOval(x, y, 2, 2);
        }

        // 绘制验证码文字
        g.setFont(new Font("Arial", Font.BOLD, 28));
        for (int i = 0; i < code.length(); i++) {
            // 随机颜色（深色）
            g.setColor(getRandomColor(20, 130));
            
            // 随机旋转角度（-15度到15度）
            int degree = random.nextInt(30) - 15;
            
            // 计算字符位置
            int x = 15 + i * 25;
            int y = 25 + random.nextInt(5);
            
            // 保存当前图形状态
            Graphics2D g2d = (Graphics2D) g.create();
            
            // 旋转
            g2d.rotate(Math.toRadians(degree), x, y);
            
            // 绘制字符
            g2d.drawString(String.valueOf(code.charAt(i)), x, y);
            
            // 恢复图形状态
            g2d.dispose();
        }

        g.dispose();

        // 转换为Base64
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }

    /**
     * 生成随机颜色
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机颜色
     */
    private static Color getRandomColor(int min, int max) {
        Random random = new Random();
        int r = random.nextInt(max - min) + min;
        int g = random.nextInt(max - min) + min;
        int b = random.nextInt(max - min) + min;
        return new Color(r, g, b);
    }
}

