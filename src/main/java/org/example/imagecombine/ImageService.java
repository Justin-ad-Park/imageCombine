package org.example.imagecombine;


import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@Service
public class ImageService {

    public byte[] combineImages(InputStream baseImageInput, InputStream frameImageInput, String outputType, String frameColorHex) throws IOException {
        BufferedImage baseImage = ImageIO.read(baseImageInput);
        BufferedImage frameImage = ImageIO.read(frameImageInput);

        // 프레임 이미지에서 흰색을 지정한 색상으로 치환
        BufferedImage coloredFrame = replaceWhiteWithColor(frameImage, Color.decode(frameColorHex));

        int width = coloredFrame.getWidth();
        int height = coloredFrame.getHeight();

        BufferedImage resizedBase = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedBase.createGraphics();
        g2d.drawImage(baseImage, 0, 0, width, height, null);
        g2d.dispose();

        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();
        g.drawImage(resizedBase, 0, 0, null);
        g.drawImage(coloredFrame, 0, 0, null);
        g.dispose();

        return convertToFormat(combined, outputType);
    }

    private byte[] convertToFormat(BufferedImage image, String outputType) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if ("jpg".equalsIgnoreCase(outputType)) {
            // Remove alpha for JPEG
            BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = rgbImage.createGraphics();
            g2.drawImage(image, 0, 0, Color.WHITE, null);  // white background for transparent areas
            g2.dispose();

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter writer = writers.next();

            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.8f); // 80% quality
            }

            MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(baos);
            writer.setOutput(output);
            writer.write(null, new IIOImage(rgbImage, null, null), param);
            writer.dispose();

        } else {
            ImageIO.write(image, "png", baos); // default to PNG
        }

        return baos.toByteArray();
    }

    private BufferedImage replaceWhiteWithColor(BufferedImage image, Color targetColor) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);

                Color color = new Color(pixel, true);
                if (isWhite(color)) {
                    result.setRGB(x, y, new Color(targetColor.getRed(), targetColor.getGreen(), targetColor.getBlue(), color.getAlpha()).getRGB());
                } else {
                    result.setRGB(x, y, pixel);
                }
            }
        }

        return result;
    }

    private boolean isWhite(Color color) {
        return color.getRed() > 240 && color.getGreen() > 240 && color.getBlue() > 240 && color.getAlpha() > 200;
    }
}