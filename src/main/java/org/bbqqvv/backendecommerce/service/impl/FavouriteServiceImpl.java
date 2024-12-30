package org.bbqqvv.backendecommerce.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bbqqvv.backendecommerce.config.jwt.SecurityUtils;
import org.bbqqvv.backendecommerce.dto.response.FavouriteResponse;
import org.bbqqvv.backendecommerce.entity.Favourite;
import org.bbqqvv.backendecommerce.entity.User;
import org.bbqqvv.backendecommerce.exception.AppException;
import org.bbqqvv.backendecommerce.exception.ErrorCode;
import org.bbqqvv.backendecommerce.mapper.FavouriteMapper;
import org.bbqqvv.backendecommerce.repository.FavouriteRepository;
import org.bbqqvv.backendecommerce.repository.ProductRepository;
import org.bbqqvv.backendecommerce.repository.UserRepository;
import org.bbqqvv.backendecommerce.service.FavouriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FavouriteServiceImpl implements FavouriteService {

    FavouriteRepository favouriteRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    FavouriteMapper favouriteMapper;

    public FavouriteServiceImpl(FavouriteRepository favouriteRepository, ProductRepository productRepository,
                                UserRepository userRepository, FavouriteMapper favouriteMapper) {
        this.favouriteRepository = favouriteRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.favouriteMapper = favouriteMapper;
    }

    @Override
    @Transactional
    public FavouriteResponse addFavourite(Long productId) {
        String username = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        User user = userRepository.findByUsername(username);

        var product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (favouriteRepository.existsByProductIdAndUserId(productId, user.getId())) {
            throw new AppException(ErrorCode.PRODUCT_ALREADY_FAVOURITE);
        }

        Favourite favourite = new Favourite();
        favourite.setUser(user);
        favourite.setProduct(product);

        Favourite savedFavourite = favouriteRepository.save(favourite);

        return favouriteMapper.toFavouriteResponse(savedFavourite);
    }

    @Override
    @Transactional
    public FavouriteResponse removeFavourite(Long productId) {
        String username = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        User user = userRepository.findByUsername(username);

        Favourite existingFavourite = favouriteRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() -> new AppException(ErrorCode.REMOVE_FAVOURITE_NOT_FOUND));

        favouriteRepository.delete(existingFavourite);

        return FavouriteResponse.builder()
                .id(existingFavourite.getId())
                .userId(existingFavourite.getUser().getId())
                .nameProduct(existingFavourite.getProduct().getName())
                .imageUrl(existingFavourite.getProduct().getMainImage().getImageUrl())
                .build();
    }

    @Override
    public List<FavouriteResponse> getUserFavourites() {
        String username = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        User user = userRepository.findByUsername(username);

        List<Favourite> favourites = favouriteRepository.findByUserId(user.getId());

        return favourites.stream()
                .map(favouriteMapper::toFavouriteResponse)
                .collect(Collectors.toList());
    }
}
