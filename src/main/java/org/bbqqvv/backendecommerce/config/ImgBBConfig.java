package org.bbqqvv.backendecommerce.config;

import io.jsonwebtoken.io.IOException;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Configuration
public class ImgBBConfig {

    private final String API_KEY = "e567c666a355b1543f9afbdcf3927ff5";
    public String uploadImage(MultipartFile image) {
        try {
            String url = "https://api.imgbb.com/1/upload";
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("key", API_KEY);  // Thêm API key vào request
            body.add("image", image.getResource()); // Thêm ảnh vào dưới dạng MultipartFile
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA); // Xác định kiểu nội dung là multipart
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null) {
                    Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                    return (String) data.get("url"); // Trả về URL của ảnh được upload
                }
            }
            throw new RuntimeException("Error: Invalid response from ImgBB API");
        } catch (IOException e) {
            throw new RuntimeException("Error while uploading image to ImgBB", e);
        }
    }
}
