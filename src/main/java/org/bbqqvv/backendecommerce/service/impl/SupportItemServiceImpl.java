package org.bbqqvv.backendecommerce.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bbqqvv.backendecommerce.dto.request.SupportItemRequest;
import org.bbqqvv.backendecommerce.dto.response.SupportItemResponse;
import org.bbqqvv.backendecommerce.entity.SupportItem;
import org.bbqqvv.backendecommerce.exception.AppException;
import org.bbqqvv.backendecommerce.exception.ErrorCode;
import org.bbqqvv.backendecommerce.mapper.SupportItemMapper;
import org.bbqqvv.backendecommerce.repository.SupportItemRepository;
import org.bbqqvv.backendecommerce.service.SupportItemsService;
import org.bbqqvv.backendecommerce.service.img.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class SupportItemServiceImpl implements SupportItemsService {

    SupportItemRepository supportItemRepository;
    SupportItemMapper supportItemMapper;
    FileStorageService fileStorageService;

    public SupportItemServiceImpl(SupportItemRepository supportItemRepository,
                                  SupportItemMapper supportItemMapper,
                                  FileStorageService fileStorageService) {
        this.supportItemRepository = supportItemRepository;
        this.supportItemMapper = supportItemMapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public List<SupportItemResponse> getAllSupportItems() {
        log.info("Fetching all support items...");
        return supportItemRepository.findAll().stream()
                .map(supportItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SupportItemResponse getSupportItemById(Long id) {
        log.info("Fetching support item with id: {}", id);
        return supportItemRepository.findById(id)
                .map(supportItemMapper::toResponse)
                .orElseThrow(() -> new AppException(ErrorCode.ResourceNotFoundException));
    }

    @Override
    public SupportItemResponse createSupportItem(SupportItemRequest request) {
        log.info("Creating support item: {}", request.getTitle());

        String imageUrl = (request.getImg() != null && !request.getImg().isEmpty())
                ? fileStorageService.storeImage(request.getImg())
                : null;

        SupportItem supportItem = SupportItem.builder()
                .title(request.getTitle())
                .img(imageUrl)
                .hours(request.getHours())
                .contact(request.getContact())
                .link(request.getLink())
                .bgColor(request.getBgColor())
                .build();

        return supportItemMapper.toResponse(supportItemRepository.save(supportItem));
    }

    @Override
    public SupportItemResponse updateSupportItem(Long id, SupportItemRequest request) {
        log.info("Updating support item with id: {}", id);
        SupportItem existingSupportItem = supportItemRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ResourceNotFoundException));

        String imageUrl = (request.getImg() != null && !request.getImg().isEmpty())
                ? fileStorageService.storeImage(request.getImg())
                : existingSupportItem.getImg();

        SupportItem updatedSupportItem = SupportItem.builder()
                .id(existingSupportItem.getId())  // Giữ nguyên ID
                .title(request.getTitle())
                .img(imageUrl)
                .hours(request.getHours())
                .contact(request.getContact())
                .link(request.getLink())
                .bgColor(request.getBgColor())
                .build();

        return supportItemMapper.toResponse(supportItemRepository.save(updatedSupportItem));
    }

    @Override
    public void deleteSupportItem(Long id) {
        log.info("Deleting support item with id: {}", id);
        SupportItem supportItem = supportItemRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ResourceNotFoundException));

        supportItemRepository.delete(supportItem);
    }
}
