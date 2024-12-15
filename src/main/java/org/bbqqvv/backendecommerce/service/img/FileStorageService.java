package org.bbqqvv.backendecommerce.service.img;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bbqqvv.backendecommerce.config.ImgBBConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FileStorageService {

    private final ImgBBConfig imgBBConfig;

    public FileStorageService(ImgBBConfig imgBBConfig) {
        this.imgBBConfig = imgBBConfig;
    }

    // Upload ảnh chính
    public String storeMainImage(MultipartFile mainImage) {
        return imgBBConfig.uploadImage(mainImage);
    }

    // Upload danh sách ảnh phụ
    public List<String> storeSecondaryImages(List<MultipartFile> secondaryImages) {
        return secondaryImages.stream()
                .map(imgBBConfig::uploadImage)
                .toList();
    }

    // Upload danh sách ảnh mô tả
    public List<String> storeDescriptionImages(List<MultipartFile> descriptionImages) {
        return descriptionImages.stream()
                .map(imgBBConfig::uploadImage)
                .toList();
    }
}

