package org.example.imagecombine;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Controller
@RequiredArgsConstructor
public class ImageCombineController {

    private final ImageService imageService;

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/combine")
    @ResponseBody
    public ResponseEntity<byte[]> combineImagesDownload(
            @RequestParam("productImage") MultipartFile productImage,
            @RequestParam("frameImage") MultipartFile frameImage,
            @RequestParam(name = "outputType", defaultValue = "png") String outputType,
            @RequestParam(name = "frameColor", defaultValue = "#87CEEB") String frameColorHex,
            @RequestParam(name = "filename", required = false) String filename
    ) throws Exception {

        byte[] productImageBytes = productImage.getBytes();
        byte[] frameImageBytes = frameImage.getBytes();
        byte[] resultImageBytes = imageService.combineImages(
                new ByteArrayInputStream(productImageBytes),
                new ByteArrayInputStream(frameImageBytes),
                outputType,
                frameColorHex
        );

        // 파일명 설정
        String defaultName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String finalName = (filename != null && !filename.isBlank()) ? filename : defaultName;
        String fullName = finalName + "." + outputType.toLowerCase();


        String contentType = "jpg".equalsIgnoreCase(outputType) ? "image/jpeg" : "image/png";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fullName)
                .contentType(MediaType.parseMediaType(contentType))
                .body(resultImageBytes);
    }

    @PostMapping("/combine-view")
    public String combineImagesForView(
            @RequestParam("productImage") MultipartFile productImage,
            @RequestParam("frameImage") MultipartFile frameImage,
            @RequestParam(name = "outputType", defaultValue = "png") String outputType,
            @RequestParam(name = "frameColor", defaultValue = "#87CEEB") String frameColorHex,
            Model model
    ) throws Exception {

        byte[] productImageBytes = productImage.getBytes();
        byte[] frameImageBytes = frameImage.getBytes();
        byte[] resultImageBytes = imageService.combineImages(
                new ByteArrayInputStream(productImageBytes),
                new ByteArrayInputStream(frameImageBytes),
                outputType,
                frameColorHex
        );

        model.addAttribute("productImage", Base64.getEncoder().encodeToString(productImageBytes));
        model.addAttribute("frameImage", Base64.getEncoder().encodeToString(frameImageBytes));
        model.addAttribute("resultImage", Base64.getEncoder().encodeToString(resultImageBytes));
        model.addAttribute("dataPrefix", outputType.equalsIgnoreCase("jpg") ? "image/jpeg" : "image/png");

        return "result.html";
    }
}