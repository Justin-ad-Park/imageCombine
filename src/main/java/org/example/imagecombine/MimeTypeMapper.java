package org.example.imagecombine;

class MimeTypeMapper {
    public static String map(String type) {
        return "jpg".equalsIgnoreCase(type) ? "image/jpeg" : "image/png";
    }
}