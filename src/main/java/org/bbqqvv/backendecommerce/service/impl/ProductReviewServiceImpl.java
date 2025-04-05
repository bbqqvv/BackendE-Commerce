package org.bbqqvv.backendecommerce.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bbqqvv.backendecommerce.config.jwt.SecurityUtils;
import org.bbqqvv.backendecommerce.dto.PageResponse;
import org.bbqqvv.backendecommerce.dto.request.ProductReviewRequest;
import org.bbqqvv.backendecommerce.dto.response.ProductReviewResponse;
import org.bbqqvv.backendecommerce.entity.*;
import org.bbqqvv.backendecommerce.exception.AppException;
import org.bbqqvv.backendecommerce.exception.ErrorCode;
import org.bbqqvv.backendecommerce.mapper.ProductReviewMapper;
import org.bbqqvv.backendecommerce.repository.OrderRepository;
import org.bbqqvv.backendecommerce.repository.ProductRepository;
import org.bbqqvv.backendecommerce.repository.ProductReviewRepository;
import org.bbqqvv.backendecommerce.repository.UserRepository;
import org.bbqqvv.backendecommerce.service.ProductReviewService;
import org.bbqqvv.backendecommerce.service.img.FileStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.bbqqvv.backendecommerce.util.PagingUtil.toPageResponse;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductReviewServiceImpl implements ProductReviewService {
    ProductReviewRepository productReviewRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    ProductReviewMapper productReviewMapper;
    OrderRepository orderRepository;
    FileStorageService fileStorageService; // Thêm vào đây
    public ProductReviewServiceImpl(ProductReviewRepository productReviewRepository, ProductRepository productRepository, ProductReviewMapper productReviewMapper, UserRepository userRepository, OrderRepository orderRepository, FileStorageService fileStorageService) {
        this.productReviewRepository = productReviewRepository;
        this.productRepository = productRepository;
        this.productReviewMapper = productReviewMapper;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.fileStorageService = fileStorageService;
    }

    private User getAuthenticatedUser() {
        return SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findByUsername)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
    }

    @Override
    @Transactional
    public ProductReviewResponse addOrUpdateReview(ProductReviewRequest reviewRequest) {
        User user = getAuthenticatedUser();
        Product product = productRepository.findById(reviewRequest.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // 🔥 Kiểm tra nếu user đã mua hàng và đơn hàng đã hoàn thành
        boolean hasCompletedOrder = orderRepository.existsByUserIdAndOrderItems_Product_IdAndStatus(
                user.getId(), product.getId(), OrderStatus.DELIVERED);

        if (!hasCompletedOrder) {
            throw new AppException(ErrorCode.ORDER_NOT_COMPLETED);
        }

        ProductReview review = productReviewRepository.findByProductIdAndUserId(product.getId(), user.getId())
                .orElse(new ProductReview());

        review.setUser(user);
        review.setProduct(product);
        review.setRating(reviewRequest.getRating());
        review.setReviewText(reviewRequest.getReviewText());

        // Upload ảnh nếu có
        if (reviewRequest.getImageFiles() != null) {
            List<String> imageUrls = fileStorageService.storeImages(reviewRequest.getImageFiles());

            List<ProductReviewImage> reviewImages = imageUrls.stream()
                    .map(url -> ProductReviewImage.builder()
                            .productReview(review)
                            .imageUrl(url)
                            .build())
                    .toList();

            review.getImages().clear(); // Xóa ảnh cũ nếu cập nhật review
            review.getImages().addAll(reviewImages);
        }

        return productReviewMapper.toResponse(productReviewRepository.save(review));
    }
    @Override
    public PageResponse<ProductReviewResponse> getReviewsByProduct(Long productId, Pageable pageable) {
        productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        Page<ProductReview> reviewPage = productReviewRepository.findByProductId(productId, pageable);
        return toPageResponse(reviewPage, productReviewMapper::toResponse);
    }

    @Override
    public PageResponse<ProductReviewResponse> getReviewsByUser(Pageable pageable) {
        User user = getAuthenticatedUser();
        Page<ProductReview> reviewPage = productReviewRepository.findByUserId(user.getId(), pageable);
        return toPageResponse(reviewPage, productReviewMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        User user = getAuthenticatedUser();
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        // Chỉ cho phép xóa review của chính user đó
        if (!review.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không thể xóa review của người khác.");
        }

        productReviewRepository.delete(review);
    }
}
