package org.bbqqvv.backendecommerce.service.img;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bbqqvv.backendecommerce.config.ImgBBConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FileStorageService {

    ImgBBConfig imgBBConfig;

    public FileStorageService(ImgBBConfig imgBBConfig) {
        this.imgBBConfig = imgBBConfig;
    }

    /**
     * Upload một ảnh duy nhất.
     *
     * @param image MultipartFile đại diện cho ảnh cần upload
     * @return URL của ảnh đã upload
     */
    public String storeImage(MultipartFile image) {
        log.info("Uploading single image...");
        return imgBBConfig.uploadImage(image);
    }

    /**
     * Upload danh sách ảnh.
     *
     * @param images Danh sách MultipartFile đại diện cho các ảnh cần upload
     * @return Danh sách URL của các ảnh đã upload
     */
    public List<String> storeImages(List<MultipartFile> images) {
        log.info("Uploading multiple images...");
        return images.stream()
                .map(imgBBConfig::uploadImage)
                .collect(Collectors.toList());
    }

}
