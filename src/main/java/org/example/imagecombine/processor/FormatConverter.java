package org.example.imagecombine.processor;


import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

class FormatConverter {
    public byte[] convert(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if ("jpg".equalsIgnoreCase(format)) {
            BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = rgbImage.createGraphics();
            g.drawImage(image, 0, 0, Color.WHITE, null);
            g.dispose();

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter writer = writers.next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.8f);
            }
            writer.setOutput(new MemoryCacheImageOutputStream(baos));
            writer.write(null, new javax.imageio.IIOImage(rgbImage, null, null), param);
            writer.dispose();
        } else {
            ImageIO.write(image, "png", baos);
        }
        return baos.toByteArray();
    }
}