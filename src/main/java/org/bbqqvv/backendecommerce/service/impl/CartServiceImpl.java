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
        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart newCart = Cart.builder()
                    .user(user)
                    .totalPrice(BigDecimal.ZERO)
                    .cartItems(new ArrayList<>())
                    .build();
            return cartRepository.save(newCart);
        });

        Map<String, CartItem> cartItemMap = cart.getCartItems().stream()
                .collect(Collectors.toMap(
                        item -> generateKey(item.getProduct().getId(), item.getSizeName(), item.getColor()),
                        item -> item
                ));

        for (var itemRequest : cartRequest.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            validateSizeOption(product, itemRequest.getSizeName());

            SizeProduct sizeProduct = sizeProductRepository.findByProductVariantSizes_ProductVariant_Product_IdAndSizeName(
                            product.getId(), itemRequest.getSizeName())
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_PRODUCT_OPTION));

            ProductVariant productVariant = sizeProduct.getProductVariantSizes()
                    .stream()
                    .map(SizeProductVariant::getProductVariant)
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));

            // Kiểm tra tổng số lượng trong giỏ hàng sau khi thêm
            String key = generateKey(itemRequest.getProductId(), itemRequest.getSizeName(), itemRequest.getColor());
            int existingQuantity = cartItemMap.getOrDefault(key, new CartItem()).getQuantity();
            int newTotalQuantity = existingQuantity + itemRequest.getQuantity();

            if (newTotalQuantity > sizeProduct.getStockQuantity()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            BigDecimal price = sizeProduct.getPrice() != null ? sizeProduct.getPrice() : BigDecimal.ZERO;
            boolean isInStock = sizeProduct.getStockQuantity() >= itemRequest.getQuantity();

            cartItemMap.computeIfAbsent(key, k -> {
                CartItem newCartItem = CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .productVariant(productVariant)
                        .quantity(0) // Sẽ cập nhật lại ngay sau đây
                        .sizeName(itemRequest.getSizeName())
                        .color(itemRequest.getColor())
                        .price(price)
                        .subtotal(BigDecimal.ZERO) // Cập nhật sau
                        .inStock(isInStock)
                        .build();
                cart.getCartItems().add(newCartItem);
                return newCartItem;
            }).setQuantity(existingQuantity + itemRequest.getQuantity());

            cartItemMap.get(key).setSubtotal(price.multiply(BigDecimal.valueOf(cartItemMap.get(key).getQuantity())));
        }

        updateCartTotal(cart);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeProductFromCart(Long productId, String sizeName, String color) {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductIdAndSizeNameAndColor(
                        cart.getId(), productId, sizeName, color)
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

        cart.getCartItems().forEach(cartItem -> {
            sizeProductRepository.findByProductVariantSizes_ProductVariant_Product_IdAndSizeName(
                    cartItem.getProduct().getId(), cartItem.getSizeName()
            ).ifPresent(sizeProduct -> {
                int stockQuantity = sizeProduct.getProductVariantSizes().stream()
                        .filter(variant -> variant.getProductVariant().equals(cartItem.getProductVariant()))
                        .map(SizeProductVariant::getStock)
                        .findFirst()
                        .orElse(0);

                cartItem.setInStock(stockQuantity >= cartItem.getQuantity());
            });
        });

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
        return userRepository.findByUsername(username);
    }

    private void validateSizeOption(Product product, String sizeName) {
        List<String> validSizes = sizeCategoryRepository.findSizesByCategory(product.getCategory().getId());
        if (!validSizes.contains(sizeName)) {
            throw new AppException(ErrorCode.INVALID_PRODUCT_OPTION);
        }
    }

    private String generateKey(Long productId, String sizeName, String color) {
        return productId + "-" + sizeName + "-" + color;
    }
}
