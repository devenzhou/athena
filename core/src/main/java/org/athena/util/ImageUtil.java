package org.athena.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片工具类
 *
 * @author zhoutaotao
 * @date 2019/5/29
 */
public class ImageUtil {

    /**
     * 将原始图片居中裁剪为正方形
     */
    public static void cutSquareImage(String src, String dest) {
        InputStream secInputSteam = null;
        try {
            secInputSteam = new FileInputStream(src);
            // 获取图片读取器
            ImageReader reader = ImageIO.getImageReadersByFormatName("jpeg").next();
            // 读取原始图片
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(secInputSteam);
            // 将原始图片信息设置到读取器内
            reader.setInput(imageInputStream, true);
            // 获取长宽，取正方形的边长
            int imageIndex = 0;
            int height = reader.getHeight(imageIndex);
            int width = reader.getWidth(imageIndex);
            // 边长
            int side = Math.min(width, height);
            // 绘制一个正方形，使读取器读取正方形指定的范围
            Rectangle rectangle = new Rectangle((width - side) / 2, (height - side) / 2, side, side);
            ImageReadParam param = reader.getDefaultReadParam();
            param.setSourceRegion(rectangle);
            // 输出图片
            BufferedImage bufferedImage = reader.read(imageIndex, param);
            File destFile = IoUtil.createFile(dest);
            ImageIO.write(bufferedImage, "jpeg", destFile);
        } catch (IOException e) {
            throw ExceptionUtil.unchecked(e);
        } finally {
            IoUtil.close(secInputSteam);
        }
    }

}
