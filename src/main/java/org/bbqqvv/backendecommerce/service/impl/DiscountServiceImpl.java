package org.bbqqvv.backendecommerce.service.impl;

import org.bbqqvv.backendecommerce.config.jwt.SecurityUtils;
import org.bbqqvv.backendecommerce.dto.PageResponse;
import org.bbqqvv.backendecommerce.dto.request.DiscountPreviewRequest;
import org.bbqqvv.backendecommerce.dto.request.DiscountRequest;
import org.bbqqvv.backendecommerce.dto.response.DiscountPreviewResponse;
import org.bbqqvv.backendecommerce.dto.response.DiscountResponse;
import org.bbqqvv.backendecommerce.entity.*;
import org.bbqqvv.backendecommerce.exception.AppException;
import org.bbqqvv.backendecommerce.exception.ErrorCode;
import org.bbqqvv.backendecommerce.mapper.DiscountMapper;
import org.bbqqvv.backendecommerce.mapper.DiscountPreviewMapper;
import org.bbqqvv.backendecommerce.repository.*;
import org.bbqqvv.backendecommerce.service.CartService;
import org.bbqqvv.backendecommerce.service.DiscountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.bbqqvv.backendecommerce.util.PagingUtil.toPageResponse;

@Service
@Transactional
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;
    private final DiscountPreviewMapper discountPreviewMapper;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final DiscountProductRepository discountProductRepository;
    private final DiscountUserRepository discountUserRepository;
private final CartService cartService;
    public DiscountServiceImpl(DiscountRepository discountRepository,
                               DiscountMapper discountMapper, DiscountPreviewMapper discountPreviewMapper,
                               ProductRepository productRepository,
                               UserRepository userRepository,
                               DiscountProductRepository discountProductRepository,
                               DiscountUserRepository discountUserRepository, CartService cartService) {
        this.discountRepository = discountRepository;
        this.discountMapper = discountMapper;
        this.discountPreviewMapper = discountPreviewMapper;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.discountProductRepository = discountProductRepository;
        this.discountUserRepository = discountUserRepository;
        this.cartService = cartService;
    }

    @Override
    public DiscountResponse createDiscount(DiscountRequest request) {
        validateDiscountRequest(request);

        if (discountRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.DUPLICATE_DISCOUNT_CODE);
        }

        // Dùng Builder để tạo Discount
        Discount discount = Discount.builder()
                .code(request.getCode())
                .discountAmount(request.getDiscountAmount())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .discountType(request.getDiscountType())
                .minOrderValue(request.getMinOrderValue())
                .usageLimit(request.getUsageLimit())
                .startDate(request.getStartDate())
                .expiryDate(request.getExpiryDate())
                .timesUsed(0)
                .active(request.isActive())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<DiscountProduct> discountProducts = getDiscountProducts(discount, request.getApplicableProducts());
        List<DiscountUser> discountUsers = getDiscountUsers(discount, request.getApplicableUsers());

        discount.setApplicableProducts(discountProducts);
        discount.setApplicableUsers(discountUsers);

        discount = discountRepository.saveAndFlush(discount);


        return discountMapper.toDiscountResponse(discountRepository.save(discount));
    }

    /**
     * Lấy danh sách sản phẩm từ database
     */
    private List<DiscountProduct> getDiscountProducts(Discount discount, List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) return List.of();

        // Lấy danh sách sản phẩm từ DB
        List<Product> products = productRepository.findAllById(productIds);

        // Kiểm tra xem có sản phẩm nào không tồn tại
        Set<Long> foundProductIds = products.stream().map(Product::getId).collect(Collectors.toSet());
        List<Long> missingProductIds = productIds.stream()
                .filter(id -> !foundProductIds.contains(id))
                .toList();

        if (!missingProductIds.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        return products.stream()
                .map(product -> new DiscountProduct(discount, product))
                .collect(Collectors.toList());
    }



    /**
     * Lấy danh sách người dùng từ database
     */
    private List<DiscountUser> getDiscountUsers(Discount discount, List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return List.of();

        // Lấy danh sách người dùng từ DB
        List<User> users = userRepository.findAllById(userIds);

        // Kiểm tra xem có user nào không tồn tại
        Set<Long> foundUserIds = users.stream().map(User::getId).collect(Collectors.toSet());
        List<Long> missingUserIds = userIds.stream()
                .filter(id -> !foundUserIds.contains(id))
                .toList();

        if (!missingUserIds.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        return users.stream()
                .map(user -> new DiscountUser(discount, user))
                .collect(Collectors.toList());
    }


    private void validateDiscountRequest(DiscountRequest request) {
        String code = Optional.ofNullable(request.getCode()).orElse("").trim();
        BigDecimal discountAmount = Optional.ofNullable(request.getDiscountAmount()).orElse(BigDecimal.ZERO);
        BigDecimal maxDiscountAmount = Optional.ofNullable(request.getMaxDiscountAmount()).orElse(BigDecimal.ZERO);
        BigDecimal minOrderValue = Optional.ofNullable(request.getMinOrderValue()).orElse(BigDecimal.ZERO);
        Integer usageLimit = Optional.ofNullable(request.getUsageLimit()).orElse(0);
        LocalDateTime startDate = request.getStartDate();
        LocalDateTime expiryDate = request.getExpiryDate();

        if (code.isEmpty()) throw new AppException(ErrorCode.INVALID_DISCOUNT_CODE);
        if (discountAmount.compareTo(BigDecimal.ZERO) <= 0) throw new AppException(ErrorCode.INVALID_DISCOUNT_AMOUNT);
        if (maxDiscountAmount.compareTo(BigDecimal.ZERO) < 0) throw new AppException(ErrorCode.INVALID_MAX_DISCOUNT_AMOUNT);
        if (discountAmount.compareTo(maxDiscountAmount) > 0) throw new AppException(ErrorCode.INVALID_DISCOUNT_AMOUNT_LIMIT);
        if (request.getDiscountType() == null) throw new AppException(ErrorCode.INVALID_DISCOUNT_TYPE);
        if (minOrderValue.compareTo(BigDecimal.ZERO) < 0) throw new AppException(ErrorCode.INVALID_MIN_ORDER_VALUE);
        if (usageLimit < 1) throw new AppException(ErrorCode.INVALID_USAGE_LIMIT);
        if (startDate == null || expiryDate == null || startDate.isAfter(expiryDate)) {
            throw new AppException(ErrorCode.INVALID_DISCOUNT_DATES);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public DiscountResponse getDiscountById(Long id) {
        return discountMapper.toDiscountResponse(findDiscountById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DiscountResponse> getAllDiscounts(Pageable pageable) {
        Page<Discount> discountPage = discountRepository.findAll(pageable);
        return toPageResponse(discountPage, discountMapper::toDiscountResponse);
    }


    @Override
    public DiscountResponse updateDiscount(Long id, DiscountRequest request) {
        Discount discount = findDiscountById(id);
        validateDiscountRequest(request);

        if (discount.isExpired()) {
            throw new AppException(ErrorCode.DISCOUNT_ALREADY_EXPIRED);
        }

        // Cập nhật thông tin discount
        discount = discount.toBuilder()
                .code(request.getCode())
                .discountAmount(request.getDiscountAmount())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .discountType(request.getDiscountType())
                .minOrderValue(request.getMinOrderValue())
                .usageLimit(request.getUsageLimit())
                .startDate(request.getStartDate())
                .expiryDate(request.getExpiryDate())
                .active(request.isActive())
                .updatedAt(LocalDateTime.now())
                .build();

        discount = discountRepository.save(discount);

        updateDiscountProducts(discount, request.getApplicableProducts());
        updateDiscountUsers(discount, request.getApplicableUsers());

        return discountMapper.toDiscountResponse(discount);
    }

    @Override
    public void deleteDiscount(Long id) {
        Discount discount = findDiscountById(id);

        if (discount.isActive()) {
            throw new AppException(ErrorCode.CANNOT_DELETE_ACTIVE_DISCOUNT);
        }

        // ✅ Xóa quan hệ trong bảng trung gian trước khi xóa discount
        discountProductRepository.deleteByDiscountId(id);
        discountUserRepository.deleteByDiscountId(id);

        discountRepository.delete(discount);
    }

    @Override
    public void clearUsersAndProducts(Long id) {
        Discount discount = findDiscountById(id);

        // Xóa tất cả sản phẩm khỏi mã giảm giá
        discountProductRepository.deleteByDiscountId(id);

        // Xóa tất cả người dùng khỏi mã giảm giá
        discountUserRepository.deleteByDiscountId(id);
    }


    @Override
    public void removeProductsFromDiscount(Long id, List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) return;

        // Chuyển danh sách thành Set để tìm kiếm nhanh hơn
        Set<Long> productIdSet = new HashSet<>(productIds);

        // Lấy danh sách sản phẩm đang được áp dụng cho discount
        Set<Long> existingProductIds = discountProductRepository.findByDiscountId(id)
                .stream()
                .map(dp -> dp.getProduct().getId())
                .collect(Collectors.toSet());

        // Xác định các sản phẩm hợp lệ cần xóa
        productIdSet.retainAll(existingProductIds); // Chỉ giữ lại các ID tồn tại

        if (productIdSet.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        // Xóa sản phẩm khỏi discount
        discountProductRepository.deleteByDiscountIdAndProductIds(id, productIdSet);
    }

    @Override
    public void removeUsersFromDiscount(Long id, List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return;

        // Chuyển danh sách thành Set để tìm kiếm nhanh hơn
        Set<Long> userIdSet = new HashSet<>(userIds);

        // Lấy danh sách người dùng đang được áp dụng cho discount
        Set<Long> existingUserIds = discountUserRepository.findByDiscountId(id)
                .stream()
                .map(du -> du.getUser().getId())
                .collect(Collectors.toSet());

        // Xác định các user hợp lệ cần xóa
        userIdSet.retainAll(existingUserIds);

        if (userIdSet.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        // Xóa người dùng khỏi discount
        discountUserRepository.deleteByDiscountIdAndUserIds(id, userIdSet);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DiscountResponse> getCurrentUserDiscount(Pageable pageable) {
        User currentUser = getAuthenticatedUser();
        Page<Discount> discountPage = discountUserRepository.findDiscountsByUserId(currentUser.getId(), pageable);
        return toPageResponse(discountPage, discountMapper::toDiscountResponse);
    }


    private User getAuthenticatedUser() {
        String username = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }




    @Override
    public DiscountPreviewResponse previewDiscount(DiscountPreviewRequest discountPreviewRequest) {
        if (discountPreviewRequest == null || discountPreviewRequest.getDiscountCode() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        // 🔥 1️⃣ Tìm mã giảm giá
        Discount discount = discountRepository.findByCode(discountPreviewRequest.getDiscountCode())
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));

        // 🔥 2️⃣ Lấy tổng giá trị giỏ hàng
        BigDecimal originalTotalAmount = cartService.getTotalCartAmount(discountPreviewRequest.getCartId());
        if (originalTotalAmount == null) {
            originalTotalAmount = BigDecimal.ZERO;
        }

        // 🔥 3️⃣ Kiểm tra điều kiện áp dụng
        boolean valid = validateDiscount(discount, originalTotalAmount);

        // 🔥 4️⃣ Tính toán số tiền giảm giá
        BigDecimal discountAmount = valid ? calculateDiscountAmount(discount, originalTotalAmount) : BigDecimal.ZERO;
        BigDecimal finalAmount = originalTotalAmount.subtract(discountAmount);

        // 🔥 5️⃣ Tạo phản hồi
        return DiscountPreviewResponse.builder()
                .discountCode(discount.getCode())
                .discountType(discount.getDiscountType())
                .originalTotalAmount(originalTotalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .valid(valid)
                .message(valid ? "Discount applied successfully" : "Discount not applicable")
                .build();
    }


    @Override
    public void saveDiscount(String discountCode) {
        // Lấy thông tin người dùng hiện tại
        User currentUser = getAuthenticatedUser();

        // Kiểm tra mã giảm giá có tồn tại không
        Discount discount = discountRepository.findByCode(discountCode)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));

        // Kiểm tra xem user đã lưu mã giảm giá này chưa
        boolean alreadySaved = discountUserRepository.existsByUserIdAndDiscountCode(currentUser.getId(), discountCode);
        if (alreadySaved) {
            throw new AppException(ErrorCode.DISCOUNT_ALREADY_SAVED);
        }

        // Lưu mã giảm giá vào danh sách của user
        DiscountUser discountUser = new DiscountUser(discount, currentUser);
        discountUserRepository.save(discountUser);

        // Tăng số lượt sử dụng của mã giảm giá
        discount.setTimesUsed(discount.getTimesUsed() + 1);
        discountRepository.save(discount);
    }


    private BigDecimal calculateDiscountAmount(Discount discount, BigDecimal originalTotalAmount) {
        if (discount == null || originalTotalAmount == null || originalTotalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Nếu tổng tiền chưa đạt mức tối thiểu để áp dụng mã giảm giá
        if (originalTotalAmount.compareTo(discount.getMinOrderValue()) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount;

        // 🔥 Nếu mã giảm giá là phần trăm (PERCENTAGE)
        if (discount.getDiscountType() == DiscountType.PERCENTAGE) {
            discountAmount = originalTotalAmount
                    .multiply(discount.getDiscountAmount().divide(BigDecimal.valueOf(100))); // Chia 100 để tính %

            // Áp dụng giới hạn tối đa (nếu có)
            if (discount.getMaxDiscountAmount() != null) {
                discountAmount = discountAmount.min(discount.getMaxDiscountAmount());
            }
        }
        // 🔥 Nếu mã giảm giá là số tiền cố định (MONEY)
        else {
            discountAmount = discount.getDiscountAmount();
        }

        // Đảm bảo số tiền giảm không vượt quá tổng giá trị giỏ hàng
        return discountAmount.min(originalTotalAmount);
    }

    private boolean validateDiscount(Discount discount, BigDecimal originalTotalAmount) {
        // Kiểm tra ngày hết hạn
        if (discount.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Kiểm tra giá trị đơn hàng tối thiểu
        if (originalTotalAmount.compareTo(discount.getMinOrderValue()) < 0) {
            return false;
        }

        return true;
    }

    private BigDecimal applyDiscount(Discount discount, BigDecimal originalTotalAmount) {
        BigDecimal discountAmount;

        if (discount.getDiscountType() == DiscountType.PERCENTAGE) {
            discountAmount = originalTotalAmount.multiply(discount.getDiscountAmount().divide(BigDecimal.valueOf(100)));
        } else {
            discountAmount = discount.getDiscountAmount();
        }

        // Giới hạn mức giảm tối đa
        if (discount.getMaxDiscountAmount() != null) {
            discountAmount = discountAmount.min(discount.getMaxDiscountAmount());
        }

        return originalTotalAmount.subtract(discountAmount);
    }

    // 🔹 Tìm discount theo ID (dùng chung)
    private Discount findDiscountById(Long id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));
    }

    // 🔹 Thêm danh sách sản phẩm vào bảng trung gian
    private void addProductsToDiscount(Discount discount, List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) return;

        List<Product> products = productRepository.findAllById(productIds);

        List<DiscountProduct> discountProducts = products.stream()
                .map(product -> {
                    DiscountProduct discountProduct = new DiscountProduct();
                    discountProduct.setDiscount(discount); // Gán Discount đã được quản lý
                    discountProduct.setProduct(product);
                    return discountProduct;
                })
                .collect(Collectors.toList());

        // ✅ Đảm bảo các entity được quản lý bởi Hibernate trước khi lưu
        discountProductRepository.saveAll(discountProducts);
    }


    private void updateDiscountProducts(Discount discount, List<Long> productIds) {
        if (productIds == null) productIds = List.of(); // Đảm bảo không null

        // Lấy danh sách sản phẩm hiện tại từ DB
        Set<Long> existingProductIds = discountProductRepository.findByDiscountId(discount.getId())
                .stream()
                .map(dp -> dp.getProduct().getId())
                .collect(Collectors.toSet()); // Chuyển thành Set để tìm kiếm nhanh hơn

        Set<Long> newProductIds = new HashSet<>(productIds); // Chuyển danh sách mới thành Set

        // Xác định sản phẩm cần xóa
        Set<Long> productsToRemove = new HashSet<>(existingProductIds);
        productsToRemove.removeAll(newProductIds); // Giữ lại những sản phẩm không có trong danh sách mới

        // Xác định sản phẩm cần thêm mới
        Set<Long> productsToAdd = new HashSet<>(newProductIds);
        productsToAdd.removeAll(existingProductIds); // Giữ lại những sản phẩm mới chưa có trong danh sách cũ

        // Xóa sản phẩm không còn trong danh sách mới
        if (!productsToRemove.isEmpty()) {
            discountProductRepository.deleteByDiscountIdAndProductIds(discount.getId(), productsToRemove);
        }
        // Thêm sản phẩm mới
        addProductsToDiscount(discount, new ArrayList<>(productsToAdd));
    }
    // 🔹 Cập nhật danh sách người dùng trong bảng trung gian
    private void updateDiscountUsers(Discount discount, List<Long> userIds) {
        if (userIds == null) userIds = List.of(); // Đảm bảo không null

        // Lấy danh sách người dùng hiện tại từ DB
        Set<Long> existingUserIds = discountUserRepository.findByDiscountId(discount.getId())
                .stream()
                .map(du -> du.getUser().getId())
                .collect(Collectors.toSet()); // Chuyển thành Set để tìm kiếm nhanh hơn

        Set<Long> newUserIds = new HashSet<>(userIds); // Chuyển danh sách mới thành Set

        // Xác định user cần xóa
        Set<Long> usersToRemove = new HashSet<>(existingUserIds);
        usersToRemove.removeAll(newUserIds); // Giữ lại những user không có trong danh sách mới

        // Xác định user cần thêm mới
        Set<Long> usersToAdd = new HashSet<>(newUserIds);
        usersToAdd.removeAll(existingUserIds); // Giữ lại những user mới chưa có trong danh sách cũ

        // Xóa user không còn trong danh sách mới
        if (!usersToRemove.isEmpty()) {
            discountUserRepository.deleteByDiscountIdAndUserIds(discount.getId(), usersToRemove);
        }

        // Thêm user mới
        addUsersToDiscount(discount, new ArrayList<>(usersToAdd));
    }
    private void addUsersToDiscount(Discount discount, List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return;
        List<User> users = userRepository.findAllById(userIds);
        List<DiscountUser> discountUsers = users.stream()
                .map(user -> {
                    DiscountUser discountUser = new DiscountUser();
                    discountUser.setDiscount(discount);
                    discountUser.setUser(user);
                    return discountUser;
                })
                .collect(Collectors.toList());
        discountUserRepository.saveAll(discountUsers);
    }
}
