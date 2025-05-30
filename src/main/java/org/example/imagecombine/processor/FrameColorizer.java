package org.example.imagecombine.processor;


import java.awt.*;
import java.awt.image.BufferedImage;

class FrameColorizer {
    public static BufferedImage replaceWhiteWithColor(BufferedImage image, Color color) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixel = new Color(image.getRGB(x, y), true);
                if (isWhite(pixel)) {
                    result.setRGB(x, y, new Color(color.getRed(), color.getGreen(), color.getBlue(), pixel.getAlpha()).getRGB());
                } else {
                    result.setRGB(x, y, pixel.getRGB());
                }
            }
        }
        return result;
    }

    private static boolean isWhite(Color c) {
        return c.getRed() > 240 && c.getGreen() > 240 && c.getBlue() > 240 && c.getAlpha() > 200;
    }
}