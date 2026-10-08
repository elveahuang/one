package cc.wdev.platform.commons.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author elvea
 */
public abstract class ImageUtils {

    // 新增文字添加方法
    public static BufferedImage addTextToImage(BufferedImage sourceImage, String text,
                                               int x, int y, Font font, Color color) {
        if (sourceImage == null || text == null || text.isEmpty()) {
            return sourceImage;
        }

        Graphics2D graphics = sourceImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 设置字体和颜色
        graphics.setFont(font != null ? font : new Font("微软雅黑", Font.PLAIN, 24));
        graphics.setColor(color != null ? color : Color.WHITE);

        // 绘制文字
        graphics.drawString(text, x, y);
        graphics.dispose();

        return sourceImage;
    }

    // 新增Base64版本
    public static String addTextToBase64Image(String base64Image, String text,
                                              int x, int y, Font font, Color color) throws Exception {
        byte[] imageData = Base64Utils.decodeFromString(base64Image);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));

        BufferedImage modifiedImage = addTextToImage(image, text, x, y, font, color);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(modifiedImage, "png", baos);
        return Base64Utils.encodeToString(baos.toByteArray());
    }

}
