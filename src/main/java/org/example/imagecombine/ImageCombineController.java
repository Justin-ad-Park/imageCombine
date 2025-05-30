package org.example.imagecombine;

import lombok.RequiredArgsConstructor;
import org.example.imagecombine.processor.ImageProcessor;
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
import java.util.Base64;

@Controller
@RequiredArgsConstructor
public class ImageCombineController {
    private final ImageProcessor imageProcessor;

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
            @RequestParam(name = "frameColor", defaultValue = "#FFFFFF") String frameColorHex,
            @RequestParam(name = "filename", required = false) String filename
    ) throws Exception {

        String finalFilename = FilenameUtil.generate(filename, outputType);
        byte[] result = imageProcessor.process(productImage.getBytes(), frameImage.getBytes(), frameColorHex, outputType);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + finalFilename)
                .contentType(MediaType.parseMediaType(MimeTypeMapper.map(outputType)))
                .body(result);
    }

    @PostMapping("/combine-view")
    public String combineImagesForView(
            @RequestParam("productImage") MultipartFile productImage,
            @RequestParam("frameImage") MultipartFile frameImage,
            @RequestParam(name = "outputType", defaultValue = "png") String outputType,
            @RequestParam(name = "frameColor", defaultValue = "#FFFFFF") String frameColorHex,
            Model model
    ) throws Exception {
        byte[] product = productImage.getBytes();
        byte[] frame = frameImage.getBytes();
        byte[] result = imageProcessor.process(product, frame, frameColorHex, outputType);

        model.addAttribute("productImage", Base64.getEncoder().encodeToString(product));
        model.addAttribute("frameImage", Base64.getEncoder().encodeToString(frame));
        model.addAttribute("resultImage", Base64.getEncoder().encodeToString(result));
        model.addAttribute("dataPrefix", MimeTypeMapper.map(outputType));
        return "result.html";
    }

}