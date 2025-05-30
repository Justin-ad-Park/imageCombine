package org.example.imagecombine.processor;


import java.awt.*;
import java.awt.image.BufferedImage;

class ImageCombiner {
    public BufferedImage combine(BufferedImage baseImage, BufferedImage frameImage) {
        int width = frameImage.getWidth();
        int height = frameImage.getHeight();
        BufferedImage resizedBase = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedBase.createGraphics();
        g2d.drawImage(baseImage, 0, 0, width, height, null);
        g2d.dispose();

        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();
        g.drawImage(resizedBase, 0, 0, null);
        g.drawImage(frameImage, 0, 0, null);
        g.dispose();

        return combined;
    }
}