package org.example.imagecombine.processor;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
class BasicImageProcessor implements ImageProcessor {
    private final ImageCombiner imageCombiner = new ImageCombiner();
    private final FormatConverter formatConverter = new FormatConverter();

    @Override
    public byte[] process(byte[] productImage, byte[] frameImage, String colorHex, String format) throws IOException {
        BufferedImage base = ImageIO.read(new ByteArrayInputStream(productImage));
        BufferedImage frame = ImageIO.read(new ByteArrayInputStream(frameImage));
        BufferedImage colored = FrameColorizer.replaceWhiteWithColor(frame, Color.decode(colorHex));
        BufferedImage combined = imageCombiner.combine(base, colored);
        return formatConverter.convert(combined, format);
    }
}