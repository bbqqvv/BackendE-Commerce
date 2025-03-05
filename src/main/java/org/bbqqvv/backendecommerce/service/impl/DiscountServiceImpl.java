package org.bbqqvv.backendecommerce.service.impl;

import org.bbqqvv.backendecommerce.dto.request.DiscountRequest;
import org.bbqqvv.backendecommerce.dto.response.DiscountResponse;
import org.bbqqvv.backendecommerce.entity.Discount;
import org.bbqqvv.backendecommerce.exception.AppException;
import org.bbqqvv.backendecommerce.exception.ErrorCode;
import org.bbqqvv.backendecommerce.mapper.DiscountMapper;
import org.bbqqvv.backendecommerce.repository.DiscountRepository;
import org.bbqqvv.backendecommerce.service.DiscountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;

    public DiscountServiceImpl(DiscountRepository discountRepository, DiscountMapper discountMapper) {
        this.discountRepository = discountRepository;
        this.discountMapper = discountMapper;
    }

    @Override
    @Transactional
    public DiscountResponse createDiscount(DiscountRequest request) {
        Discount discount = Discount.builder()
                .code(request.getCode())
                .discountAmount(request.getDiscountAmount())
                .isPercentage(request.isPercentage())
                .expiryDate(request.getExpiryDate())
                .isActive(request.isActive())
                .build();

        discount = discountRepository.save(discount);
        return discountMapper.toDiscountResponse(discount);
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountResponse getDiscountById(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));
        return discountMapper.toDiscountResponse(discount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> getAllDiscounts() {
        return discountRepository.findAll().stream()
                .map(discountMapper::toDiscountResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DiscountResponse updateDiscount(Long id, DiscountRequest request) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));

        discount.setCode(request.getCode());
        discount.setDiscountAmount(request.getDiscountAmount());
        discount.setPercentage(request.isPercentage());
        discount.setExpiryDate(request.getExpiryDate());
        discount.setActive(request.isActive());

        discount = discountRepository.save(discount);
        return discountMapper.toDiscountResponse(discount);
    }

    @Override
    @Transactional
    public void deleteDiscount(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));
        discountRepository.delete(discount);
    }
}
