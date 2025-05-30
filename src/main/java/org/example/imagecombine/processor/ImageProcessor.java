package org.example.imagecombine.processor;

import java.io.IOException;

public interface ImageProcessor {
    byte[] process(byte[] productImage, byte[] frameImage, String colorHex, String format) throws IOException;
}