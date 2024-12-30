package org.bbqqvv.backendecommerce.service.impl;

import org.bbqqvv.backendecommerce.dto.request.CartRequest;
import org.bbqqvv.backendecommerce.dto.response.CartItemResponse;
import org.bbqqvv.backendecommerce.dto.response.CartResponse;
import org.bbqqvv.backendecommerce.entity.Cart;
import org.bbqqvv.backendecommerce.entity.CartItem;
import org.bbqqvv.backendecommerce.entity.User;
import org.bbqqvv.backendecommerce.exception.AppException;
import org.bbqqvv.backendecommerce.exception.ErrorCode;
import org.bbqqvv.backendecommerce.mapper.CartItemMapper;
import org.bbqqvv.backendecommerce.mapper.CartMapper;
import org.bbqqvv.backendecommerce.repository.CartItemRepository;
import org.bbqqvv.backendecommerce.repository.CartRepository;
import org.bbqqvv.backendecommerce.repository.ProductRepository;
import org.bbqqvv.backendecommerce.repository.UserRepository;
import org.bbqqvv.backendecommerce.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           CartMapper cartMapper,
                           CartItemMapper cartItemMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
    }

    @Override
    @Transactional
    public CartResponse addProductToCart(CartRequest cartRequest) {
        Cart cart = cartRepository.findByUserId(cartRequest.getUserId())
                .orElseGet(() -> createNewCart(cartRequest.getUserId()));

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (var itemRequest : cartRequest.getItems()) {
            var product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), itemRequest.getProductId());

            if (existingCartItem.isPresent()) {
                // Cập nhật số lượng nếu sản phẩm đã có trong giỏ
                CartItem cartItem = existingCartItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + itemRequest.getQuantity());
                cartItemRepository.save(cartItem);

                // Cập nhật tổng giá trị
                totalPrice = totalPrice.add(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            } else {
                // Thêm sản phẩm mới vào giỏ
                CartItem newCartItem = CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .price(product.getPrice())
                        .quantity(itemRequest.getQuantity())
                        .build();
                cartItemRepository.save(newCartItem);

                // Cập nhật tổng giá trị
                totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            }
        }

        // Cập nhật lại tổng tiền của giỏ hàng sau khi thay đổi
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);  // Cập nhật lại Cart

        return cartMapper.toCartResponse(cart);
    }


    @Override
    @Transactional
    public CartResponse updateCart(CartRequest cartRequest) {
        Cart cart = cartRepository.findByUserId(cartRequest.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (var itemRequest : cartRequest.getItems()) {
            CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), itemRequest.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

            cartItem.setQuantity(itemRequest.getQuantity());
            totalPrice = totalPrice.add(cartItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        // Cập nhật tổng tiền của giỏ hàng
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);

        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeProductFromCart(Long productId) {
        CartItem cartItem = cartItemRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);  // Xóa sản phẩm khỏi giỏ hàng

        // Tính lại totalPrice của giỏ hàng
        BigDecimal totalPrice = cart.getCartItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);

        return cartMapper.toCartResponse(cart);
    }
    @Override
    @Transactional
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại"));

        List<CartItemResponse> cartItemResponses = cart.getCartItems().stream()
                .map(cartItemMapper::toCartItemResponse)
                .collect(Collectors.toList());

        BigDecimal totalPrice = cartItemResponses.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .cartItems(cartItemResponses)
                .totalPrice(totalPrice)
                .build();
    }
    @Override
    public List<CartResponse> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        return carts.stream()
                .map(cartMapper::toCartResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại"));
        cartItemRepository.deleteAllByCartId(cart.getId());
    }

    private Cart createNewCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = Cart.builder()
                .user(user)
                .totalPrice(BigDecimal.ZERO)
                .build();

        return cartRepository.save(cart);
    }
}
