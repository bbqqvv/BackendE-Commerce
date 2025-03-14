package org.bbqqvv.backendecommerce.service.impl;

import org.bbqqvv.backendecommerce.config.jwt.SecurityUtils;
import org.bbqqvv.backendecommerce.dto.request.OrderItemRequest;
import org.bbqqvv.backendecommerce.dto.request.OrderRequest;
import org.bbqqvv.backendecommerce.dto.response.OrderResponse;
import org.bbqqvv.backendecommerce.entity.*;
import org.bbqqvv.backendecommerce.exception.AppException;
import org.bbqqvv.backendecommerce.exception.ErrorCode;
import org.bbqqvv.backendecommerce.mapper.OrderMapper;
import org.bbqqvv.backendecommerce.repository.*;
import org.bbqqvv.backendecommerce.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final SizeProductVariantRepository sizeProductVariantRepository;
    private final DiscountRepository discountRepository;
    private final OrderMapper orderMapper;
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = BigDecimal.valueOf(500000);
    private static final int EXPECTED_DELIVERY_DAYS = 5;

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                            UserRepository userRepository, ProductRepository productRepository, CartRepository cartRepository,
                            AddressRepository addressRepository, SizeProductVariantRepository sizeProductVariantRepository,
                            DiscountRepository discountRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.sizeProductVariantRepository = sizeProductVariantRepository;
        this.discountRepository = discountRepository;
        this.orderMapper = orderMapper;
    }

    private User getAuthenticatedUser() {
        String username = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderResponse createOrder(OrderRequest orderRequest) {
        User user = getAuthenticatedUser();
        Address address = findAddressById(orderRequest.getAddressId());

        // Lấy giỏ hàng của người dùng
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        if (cart.getCartItems().isEmpty()) {
            throw new AppException(ErrorCode.EMPTY_CART);
        }

        // Tạo map sản phẩm để tối ưu hiệu suất
        Map<Long, Product> productMap = cart.getCartItems().stream()
                .map(CartItem::getProduct)
                .distinct()
                .collect(Collectors.toMap(Product::getId, product -> product));

        // 1️⃣ Tính tổng giá trị đơn hàng trước giảm giá
        BigDecimal orderTotal = cart.getCartItems().stream()
                .map(cartItem -> {
                    Product product = productMap.get(cartItem.getProduct().getId());
                    SizeProductVariant sizeProductVariant = findSizeProduct(product, cartItem.getSizeName());
                    return sizeProductVariant.getSizeProduct().getPriceAfterDiscount()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

// 2️⃣ Áp dụng giảm giá
        Discount discount = applyDiscount(orderRequest.getDiscountCode(), orderTotal);
        BigDecimal discountAmount = (discount != null) ? calculateDiscountAmount(discount, orderTotal) : BigDecimal.ZERO;

// 3️⃣ Tổng sau khi giảm giá (không được âm)
        BigDecimal totalAfterDiscount = orderTotal.subtract(discountAmount).max(BigDecimal.ZERO);

// 4️⃣ Tính phí ship sau khi giảm giá
        BigDecimal shippingFee = calculateShippingFee(address, totalAfterDiscount);

// 5️⃣ Tổng tiền cuối cùng
        BigDecimal finalTotalAmount = totalAfterDiscount.add(shippingFee);


        // Tạo đơn hàng
        Order order = buildOrder(orderRequest, user, address, shippingFee, discount, discountAmount, finalTotalAmount);
        orderRepository.save(order);

        // Lưu danh sách sản phẩm trong đơn hàng
        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    Product product = findProductById(cartItem.getProduct().getId());
                    SizeProductVariant sizeProductVariant = findSizeProduct(product, cartItem.getSizeName());

                    // Giảm số lượng tồn kho
                    updateStock(sizeProductVariant, cartItem.getQuantity());

                    return OrderItem.builder()
                            .order(order)
                            .product(product)
                            .sizeName(cartItem.getSizeName())
                            .quantity(cartItem.getQuantity())
                            .price(sizeProductVariant.getSizeProduct().getPriceAfterDiscount())
                            .subtotal(sizeProductVariant.getSizeProduct().getPriceAfterDiscount()
                                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                            .color(cartItem.getColor())
                            .build();
                }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        orderItemRepository.saveAll(orderItems);

        // Xóa giỏ hàng sau khi tạo đơn hàng
        cartRepository.deleteByUserId(user.getId());

        return orderMapper.toCartResponse(order);
    }


    private Discount applyDiscount(String discountCode, BigDecimal totalAmount) {
        if (discountCode == null || discountCode.isBlank()) {
            System.out.println("Không có mã giảm giá");
            return null;
        }

        // Lấy discount từ DB
        Discount discount = discountRepository.findByCode(discountCode).orElse(null);

        if (discount == null || !discount.isActive()) {
            System.out.println("Mã giảm giá không hợp lệ hoặc chưa được kích hoạt: " + discountCode);
            return null;
        }

        // Kiểm tra điều kiện áp dụng mã giảm giá
        if (totalAmount.compareTo(discount.getMinOrderValue()) < 0) {
            System.out.println("Tổng giá trị đơn hàng (" + totalAmount + ") không đủ để áp dụng mã giảm giá " + discountCode);
            return null;
        }

        System.out.println("Áp dụng mã giảm giá: " + discountCode + ", Giá trị giảm: " + discount.getDiscountAmount());
        return discount;
    }


    private BigDecimal calculateDiscountAmount(Discount discount, BigDecimal orderTotal) {
        if (discount.getDiscountType() == DiscountType.PERCENTAGE) {
            return orderTotal.multiply(discount.getDiscountAmount()).divide(BigDecimal.valueOf(100));
        } else if (discount.getDiscountType() == DiscountType.MONEY) {
            return discount.getDiscountAmount();
        }
        return BigDecimal.ZERO; // Nếu loại giảm giá không hợp lệ
    }


    private static final Map<String, BigDecimal> SHIPPING_FEES = Map.of(
            "hồ chí minh", BigDecimal.valueOf(50000),
            "hà nội", BigDecimal.valueOf(50000),
            "đà nẵng", BigDecimal.valueOf(20000)
    );

    private BigDecimal calculateShippingFee(Address address, BigDecimal orderTotal) {
        if (orderTotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0) {
            return BigDecimal.ZERO;
        }

        String province = address.getProvince().trim().toLowerCase();
        return SHIPPING_FEES.getOrDefault(province, BigDecimal.valueOf(50000));
    }

    private Order buildOrder(OrderRequest orderRequest, User user, Address address,
                             BigDecimal shippingFee, Discount discount, BigDecimal discountAmount, BigDecimal totalAmount) {
        return Order.builder()
                .user(user)
                .fullAddress(address.getFullAddress())
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .expectedDeliveryDate(LocalDate.now().plusDays(EXPECTED_DELIVERY_DAYS))
                .paymentMethod(orderRequest.getPaymentMethod())
                .shippingFee(shippingFee)
                .notes(address.getNote())
                .discount(discount)
                .discountCode(discount.getCode())
                .discountAmount(discountAmount)
                .totalAmount(totalAmount) // ✅ Đảm bảo totalAmount được set đúng
                .build();
    }



    private OrderItem buildOrderItem(Order order, Product product, SizeProductVariant sizeProductVariant, SizeProduct sizeProduct, OrderItemRequest itemRequest) {
        validateStock(sizeProduct, itemRequest.getQuantity());
        updateStock(sizeProductVariant, itemRequest.getQuantity());

        return OrderItem.builder()
                .order(order)
                .product(product)
                .sizeName(sizeProduct.getSizeName())
                .quantity(itemRequest.getQuantity())
                .price(sizeProduct.getPrice())
                .subtotal(sizeProduct.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()))) // ✅ Lưu tổng tiền
                .color(itemRequest.getColor())
                .build();

    }

    private void validateStock(SizeProduct sizeProduct, int quantity) {
        if (sizeProduct.getStockQuantity() < quantity) {
            throw new AppException(ErrorCode.OUT_OF_STOCK);
        }
    }

    private Address findAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND)); // ✅ Ném lỗi nếu không tìm thấy
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private SizeProductVariant findSizeProduct(Product product, String sizeName) {
        System.out.println("Finding SizeProductVariant for product: " + product.getId() + " with size: " + sizeName);

        return sizeProductVariantRepository.findByProductIdAndSizeName(product.getId(), sizeName)
                .orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_FOUND));
    }

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = findOrderById(orderId);
        User user = getAuthenticatedUser();

        // Chỉ cho phép admin hoặc chủ sở hữu đơn hàng truy cập
        if (!user.getId().equals(order.getUser().getId()) && !user.isAdmin()) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return orderMapper.toCartResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser() {
        User user = getAuthenticatedUser();
        return orderRepository.findByUserId(user.getId()).stream()
                .map(orderMapper::toCartResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toCartResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse updateOrder(Long orderId, OrderRequest orderRequest) {
        Order order = findOrderById(orderId);
        updateOrderDetails(order, orderRequest);
        return orderMapper.toCartResponse(order);
    }

    private void updateOrderDetails(Order order, OrderRequest orderRequest) {
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = findOrderById(orderId);

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }

        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        return orderMapper.toCartResponse(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = findOrderById(orderId);
        User user = getAuthenticatedUser();
        if (!user.getId().equals(order.getUser().getId()) && !user.isAdmin()) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED) {
            throw new AppException(ErrorCode.CANNOT_CANCEL_ORDER);
        }


        // 🔄 Hoàn lại số lượng sản phẩm
        List<SizeProductVariant> updatedVariants = new ArrayList<>();
        order.getOrderItems().forEach(item -> {
            SizeProductVariant sizeProductVariant = findSizeProduct(item.getProduct(), item.getSizeName());
            sizeProductVariant.setStock(sizeProductVariant.getStock() + item.getQuantity());
            updatedVariants.add(sizeProductVariant);
        });
        sizeProductVariantRepository.saveAll(updatedVariants);


        order.setStatus(OrderStatus.CANCELED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }


    private void updateStock(SizeProductVariant sizeProductVariant, int quantity) {
        if (sizeProductVariant.getStock() < quantity) {
            throw new AppException(ErrorCode.OUT_OF_STOCK);
        }
        sizeProductVariant.setStock(sizeProductVariant.getStock() - quantity);
        sizeProductVariantRepository.save(sizeProductVariant);
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = findOrderById(orderId);
        orderItemRepository.deleteAllByOrderId(orderId);
        orderRepository.delete(order);
    }
}
