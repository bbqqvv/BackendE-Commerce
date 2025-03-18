package org.bbqqvv.backendecommerce.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    // 1000 Series - User Errors
    USER_NOT_FOUND(1001, "User not found with ID: {0}", HttpStatus.NOT_FOUND),
    ACCOUNT_NOT_FOUND(1002, "Account not found", HttpStatus.NOT_FOUND),
    INVALID_USER_CREDENTIALS(1003, "Invalid username or password", HttpStatus.UNAUTHORIZED),
    USER_ACCOUNT_LOCKED(1004, "User account is locked with ID: {0}", HttpStatus.FORBIDDEN),
    USER_UNAUTHORIZED(1005, "User is not authorized", HttpStatus.UNAUTHORIZED),
    USER_ACCESS_DENIED(1006, "Access denied for user with ID: {0}", HttpStatus.FORBIDDEN),
    USER_EXISTED(1007, "User already exists", HttpStatus.BAD_REQUEST),
    PASSWORDS_DO_NOT_MATCH(1008, "Passwords do not match", HttpStatus.BAD_REQUEST),
    INVALID_OLD_PASSWORD(1009, "Old password is incorrect", HttpStatus.BAD_REQUEST),

    // 2000 Series - Product Errors
    PRODUCT_NOT_FOUND(2001, "Product not found with ID: {0}", HttpStatus.NOT_FOUND),
    DUPLICATE_PRODUCT_CODE(2002, "Product code already exists", HttpStatus.BAD_REQUEST),
    INVALID_AMOUNT(2003, "Invalid amount provided", HttpStatus.BAD_REQUEST),
    PRODUCT_OUT_OF_QUANTITY(2004, "Product out of stock", HttpStatus.BAD_REQUEST),
    PRODUCT_PRICE_INVALID(2005, "Product price is invalid", HttpStatus.BAD_REQUEST),

    // 3000 Series - Cart Errors
    CART_NOT_FOUND(3001, "Cart not found with ID: {0}", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND(3002, "Cart item not found with Product ID: {0}", HttpStatus.NOT_FOUND),
    CART_ITEM_ALREADY_EXISTS(3003, "Cart item already exists with Product ID: {0}", HttpStatus.BAD_REQUEST),
    CART_EMPTY(3004, "Cart is empty", HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_OPTION(3005, "Invalid product option", HttpStatus.BAD_REQUEST),
    PRODUCT_VARIANT_NOT_FOUND(3006, "Product variant not found with ID: {0}", HttpStatus.NOT_FOUND),
    OUT_OF_STOCK(3007, "Out of stock", HttpStatus.BAD_REQUEST),

    // 4000 Series - Order Errors
    ORDER_NOT_FOUND(4001, "Order not found", HttpStatus.NOT_FOUND),
    CANNOT_CANCEL_ORDER(4002, "Cannot cancel order", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS(4003, "Invalid order status", HttpStatus.UNAUTHORIZED),
    EMPTY_CART(4004,"Empty cart",HttpStatus.BAD_REQUEST),
    // 5000 Series - Account Errors
    ACCOUNT_DISABLED(5001, "Account is disabled", HttpStatus.FORBIDDEN),

    // 6000 Series - Image Upload Errors
    IMAGE_UPLOAD_FAILED(6001, "Failed to upload image", HttpStatus.BAD_REQUEST),
    IMAGE_FORMAT_INVALID(6002, "Invalid image format", HttpStatus.BAD_REQUEST),

    // 7000 Series - Validation Errors
    INVALID_DOB(7001, "Your age must be at least {0}", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_FUNDS(7002, "Insufficient funds", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(7003, "You do not have permission", HttpStatus.FORBIDDEN),
    REMOVE_FAVOURITE_NOT_FOUND(7004, "Favourite not found for user with ID: {0} and product with ID: {1}", HttpStatus.NOT_FOUND),
    PRODUCT_ALREADY_FAVOURITE(7005, "Product with ID: {0} is already in the favourites list of user with ID: {1}", HttpStatus.BAD_REQUEST),

    // 8000 Series - Address Errors
    ADDRESS_VALIDATION_FAILED(8001, "Address validation failed: {0}", HttpStatus.BAD_REQUEST),
    ADDRESS_PHONE_INVALID(8002, "Invalid phone number format", HttpStatus.BAD_REQUEST),
    ADDRESS_NOT_FOUND(8003, "Address not found", HttpStatus.NOT_FOUND),
    ADDRESS_DEFAULT_CANNOT_DELETE(8004, "Default address cannot be deleted", HttpStatus.BAD_REQUEST),
    SIZE_NOT_FOUND(8005, "Size not found", HttpStatus.NOT_FOUND),
    INVALID_DISCOUNT_CODE(8006, "Invalid discount code", HttpStatus.BAD_REQUEST),

    // 8500 Series - Category Errors
    CATEGORY_NOT_FOUND(8500, "Category not found with ID: {0}", HttpStatus.NOT_FOUND),

    // 8600 Series - Discount Errors
    DISCOUNT_NOT_FOUND(8600, "Discount not found with ID: {0}", HttpStatus.NOT_FOUND),
    DISCOUNT_NOT_APPLICABLE(8601, "Discount not applicable", HttpStatus.FORBIDDEN),
    DISCOUNT_ALREADY_EXPIRED(8602, "Discount has already expired", HttpStatus.BAD_REQUEST),
    DISCOUNT_USAGE_LIMIT_REACHED(8603, "Discount usage limit reached", HttpStatus.BAD_REQUEST),
    INVALID_EXPIRY_DATE(8604, "Invalid expiry date", HttpStatus.BAD_REQUEST),
    DUPLICATE_DISCOUNT_CODE(8605, "Discount code already exists", HttpStatus.BAD_REQUEST),
    INVALID_MIN_ORDER_VALUE(8606, "Minimum order value is invalid", HttpStatus.BAD_REQUEST),
    INVALID_USAGE_LIMIT(8607, "Usage limit cannot be less than times used", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_ACTIVE_DISCOUNT(8608, "Cannot delete an active discount", HttpStatus.BAD_REQUEST),
    INVALID_DISCOUNT_AMOUNT(8610, "Discount amount must be greater than 0", HttpStatus.BAD_REQUEST),
    INVALID_DISCOUNT_TYPE(8611, "Discount type is required", HttpStatus.BAD_REQUEST),
    INVALID_DISCOUNT_DATES(8612, "Start date must be before expiry date", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(8613,"Invalid Request", HttpStatus.BAD_REQUEST),
    DISCOUNT_ALREADY_ADDED(8614,"Discount already added", HttpStatus.BAD_REQUEST),
    INVALID_MAX_DISCOUNT_AMOUNT(8615,"Invalid max discount amount", HttpStatus.BAD_REQUEST),
    INVALID_DISCOUNT_AMOUNT_LIMIT(8616,"Invalid discount amount limit", HttpStatus.BAD_REQUEST),
    DISCOUNT_ALREADY_SAVED(8617,"Discount already saved", HttpStatus.BAD_REQUEST),
    // 9000 Series - Security & Access Errors
    ACCESS_DENIED(9001, "Access denied", HttpStatus.UNAUTHORIZED),

    // 9999 - General Errors
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessageWithParams(Object... params) {
        String formattedMessage = this.message;
        for (int i = 0; i < params.length; i++) {
            formattedMessage = formattedMessage.replace("{" + i + "}", params[i].toString());
        }
        return formattedMessage;
    }
}
