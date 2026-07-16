package com.travel.ai.controller;

import com.travel.ai.common.Result;
import com.travel.ai.entity.Scenic;
import com.travel.ai.service.ScenicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private ScenicService scenicService;

    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/image")
    public Result<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的图片");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        String newFilename = UUID.randomUUID().toString() + extension;
        
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uploadPath = UPLOAD_DIR + dateDir + "/";
        
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        File dest = new File(uploadPath + newFilename);
        try {
            file.transferTo(dest);
            String fileUrl = "/api/upload/image/" + dateDir + "/" + newFilename;
            
            Map<String, Object> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", newFilename);
            return Result.success(result);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("图片上传失败");
        }
    }

    @PostMapping("/scenic/{scenicId}")
    public Result<Map<String, Object>> uploadScenicImage(@PathVariable Long scenicId, 
                                                         @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的图片");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        String newFilename = UUID.randomUUID().toString() + extension;
        
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uploadPath = UPLOAD_DIR + dateDir + "/";
        
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        File dest = new File(uploadPath + newFilename);
        try {
            file.transferTo(dest);
            String fileUrl = "/api/upload/image/" + dateDir + "/" + newFilename;
            
            Scenic scenic = scenicService.getById(scenicId);
            if (scenic != null) {
                String currentUrls = scenic.getImgUrls();
                if (currentUrls == null || currentUrls.isEmpty()) {
                    scenic.setImgUrls(fileUrl);
                } else {
                    scenic.setImgUrls(currentUrls + "," + fileUrl);
                }
                scenicService.updateById(scenic);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", newFilename);
            return Result.success(result);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("图片上传失败");
        }
    }

    @GetMapping("/image/{dateDir}/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String dateDir, @PathVariable String filename) {
        try {
            String filePath = UPLOAD_DIR + dateDir + "/" + filename;
            File file = new File(filePath);
            
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(file);
            
            String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            MediaType mediaType;
            switch (extension) {
                case "jpg":
                case "jpeg":
                    mediaType = MediaType.IMAGE_JPEG;
                    break;
                case "png":
                    mediaType = MediaType.IMAGE_PNG;
                    break;
                case "gif":
                    mediaType = MediaType.IMAGE_GIF;
                    break;
                case "webp":
                    mediaType = MediaType.parseMediaType("image/webp");
                    break;
                default:
                    mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }
            
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                    .replace("+", "%20");
            
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "inline; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}