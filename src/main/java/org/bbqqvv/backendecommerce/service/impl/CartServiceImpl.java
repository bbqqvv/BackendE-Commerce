package org.bbqqvv.backendecommerce.service.impl;

import org.bbqqvv.backendecommerce.config.jwt.SecurityUtils;
import org.bbqqvv.backendecommerce.dto.request.CartRequest;
import org.bbqqvv.backendecommerce.dto.response.CartResponse;
import org.bbqqvv.backendecommerce.entity.*;
import org.bbqqvv.backendecommerce.exception.AppException;
import org.bbqqvv.backendecommerce.exception.ErrorCode;
import org.bbqqvv.backendecommerce.mapper.CartMapper;
import org.bbqqvv.backendecommerce.repository.*;
import org.bbqqvv.backendecommerce.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final SizeProductRepository sizeProductRepository;
    private final SizeCategoryRepository sizeCategoryRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           SizeProductRepository sizeProductRepository,
                           SizeCategoryRepository sizeCategoryRepository,
                           UserRepository userRepository,
                           CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.sizeProductRepository = sizeProductRepository;
        this.sizeCategoryRepository = sizeCategoryRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    @Transactional
    public CartResponse addOrUpdateProductInCart(CartRequest cartRequest) {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(() ->
                cartRepository.save(Cart.builder()
                        .user(user)
                        .totalPrice(BigDecimal.ZERO)
                        .cartItems(new ArrayList<>())
                        .build())
        );

        Map<String, CartItem> cartItemMap = cart.getCartItems().stream()
                .collect(Collectors.toMap(
                        item -> generateKey(item.getProduct().getId(), item.getSizeName(), item.getColor()),
                        item -> item
                ));

        cartRequest.getItems().forEach(itemRequest -> {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            validateSizeOption(product, itemRequest.getSizeName());

            SizeProduct sizeProduct = sizeProductRepository.findByProductIdAndSizeName(
                            product.getId(), itemRequest.getSizeName())
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_PRODUCT_OPTION));

            ProductVariant productVariant = sizeProduct.getProductVariantSizes().stream()
                    .map(SizeProductVariant::getProductVariant)
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));

            String key = generateKey(itemRequest.getProductId(), itemRequest.getSizeName(), itemRequest.getColor());
            int newTotalQuantity = cartItemMap.getOrDefault(key, new CartItem()).getQuantity() + itemRequest.getQuantity();

            if (newTotalQuantity > sizeProduct.getStockQuantity()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            BigDecimal price = sizeProduct.getPriceAfterDiscount() != null ? sizeProduct.getPriceAfterDiscount() : BigDecimal.ZERO;
            boolean isInStock = sizeProduct.getStockQuantity() >= itemRequest.getQuantity();

            cartItemMap.computeIfAbsent(key, k -> {
                CartItem newCartItem = CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .productVariant(productVariant)
                        .quantity(0) // Sẽ cập nhật ngay sau đây
                        .sizeName(itemRequest.getSizeName())
                        .color(itemRequest.getColor())
                        .price(price)
                        .subtotal(BigDecimal.ZERO)
                        .inStock(isInStock)
                        .stock(sizeProduct.getStockQuantity())
                        .build();
                cart.getCartItems().add(newCartItem);
                return newCartItem;
            }).setQuantity(newTotalQuantity);


            cartItemMap.get(key).setSubtotal(price.multiply(BigDecimal.valueOf(newTotalQuantity)));
        });

        updateCartTotal(cart);
        return cartMapper.toCartResponse(cart);
    }


    @Override
    @Transactional
    public CartResponse removeProductFromCart(Long productId, String sizeName, String color) {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductIdAndSizeNameAndColor(cart.getId(), productId, sizeName, color)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        updateCartTotal(cart);

        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse getCartByUserId() {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        cart.getCartItems().forEach(cartItem -> sizeProductRepository.findByProductIdAndSizeName(
                        cartItem.getProduct().getId(), cartItem.getSizeName())
                .ifPresent(sizeProduct -> {
                    cartItem.setStock(sizeProduct.getStockQuantity()); // ✅ Cập nhật stock
                    cartItem.setInStock(sizeProduct.getStockQuantity() >= cartItem.getQuantity());
                })
        );

        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse increaseProductQuantity(CartRequest cartRequest) {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        for (var itemRequest : cartRequest.getItems()) {
            CartItem cartItem = cartItemRepository.findByCartIdAndProductIdAndSizeNameAndColor(
                            cart.getId(), itemRequest.getProductId(), itemRequest.getSizeName(), itemRequest.getColor())
                    .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

            SizeProduct sizeProduct = sizeProductRepository.findByProductIdAndSizeName(
                            cartItem.getProduct().getId(), cartItem.getSizeName())
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_PRODUCT_OPTION));

            // Kiểm tra tồn kho
            if (cartItem.getQuantity() + 1 > sizeProduct.getStockQuantity()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItem.setSubtotal(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            cartItemRepository.save(cartItem);
        }

        updateCartTotal(cart);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse decreaseProductQuantity(CartRequest cartRequest) {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        for (var itemRequest : cartRequest.getItems()) {
            CartItem cartItem = cartItemRepository.findByCartIdAndProductIdAndSizeNameAndColor(
                            cart.getId(), itemRequest.getProductId(), itemRequest.getSizeName(), itemRequest.getColor())
                    .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

            // Nếu số lượng còn 1 thì xóa luôn sản phẩm khỏi giỏ hàng
            if (cartItem.getQuantity() == 1) {
                cart.getCartItems().remove(cartItem);
                cartItemRepository.delete(cartItem);
            } else {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                cartItem.setSubtotal(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                cartItemRepository.save(cartItem);
            }
        }

        updateCartTotal(cart);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public void clearCart() {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        cartItemRepository.deleteAllByCartId(cart.getId());
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    @Override
    public BigDecimal getTotalCartAmount(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        return cart.getTotalPrice();
    }

    private void updateCartTotal(Cart cart) {
        BigDecimal newTotal = cart.getCartItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (!newTotal.equals(cart.getTotalPrice())) {
            cart.setTotalPrice(newTotal);
            cartRepository.save(cart);
        }
    }

    private User getAuthenticatedUser() {
        String username = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateSizeOption(Product product, String sizeName) {
        List<String> validSizes = sizeCategoryRepository.findSizeNamesByCategoryId(product.getCategory().getId());
        System.out.println("📌 Danh sách size hợp lệ: " + validSizes);
        System.out.println("📌 Kiểm tra size: " + sizeName);

        if (!validSizes.contains(sizeName)) {
            System.out.println("❌ Size không hợp lệ: " + sizeName);
            throw new AppException(ErrorCode.INVALID_PRODUCT_OPTION);
        }

        System.out.println("✅ Size hợp lệ: " + sizeName);
    }
    private String generateKey(Long productId, String sizeName, String color) {
        return productId + "-" + sizeName + "-" + color;
    }
}
