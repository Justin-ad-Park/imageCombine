package org.example.imagecombine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class FilenameUtil {
    protected static String generate(String filename, String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String safeName = (filename == null || filename.isBlank()) ? timestamp : filename.trim();
        return safeName + "." + extension.toLowerCase();
    }
}