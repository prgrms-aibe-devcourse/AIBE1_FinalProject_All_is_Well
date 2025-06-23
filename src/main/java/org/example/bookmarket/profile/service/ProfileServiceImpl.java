
package org.example.bookmarket.profile.service;

import com.bukgeokbukgeok.chat.dto.ChatSummary;
import com.bukgeokbukgeok.usedbook.dto.UsedBookSummary;
import com.bukgeokbukgeok.trade.dto.PurchaseSummary;
import com.bukgeokbukgeok.wishlist.dto.WishlistItem;
import com.bukgeokbukgeok.profile.dto.ProfileResponse;
import com.bukgeokbukgeok.profile.dto.ProfileUpdateRequest;
import com.bukgeokbukgeok.profile.mapper.ProfileMapper;
import com.bukgeokbukgeok.domain.user.User;
import com.bukgeokbukgeok.domain.user.UserRepository;
import com.bukgeokbukgeok.domain.category.Category;
import com.bukgeokbukgeok.domain.category.CategoryRepository;
import com.bukgeokbukgeok.domain.usercategory.UserCategory;
import com.bukgeokbukgeok.domain.usercategory.UserCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserCategoryRepository userCategoryRepository;

    @Override
    public ProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return ProfileMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void updateMyProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow();

        user.setNickname(request.getNickname());
        user.setProfileImageUrl(request.getProfileImageUrl());

        List<UserCategory> existing = userCategoryRepository.findByUser(user);
        Set<Long> existingIds = existing.stream()
                .map(uc -> uc.getCategory().getId())
                .collect(Collectors.toSet());

        Set<Long> newIds = new HashSet<>(request.getInterestCategoryIds());

        List<UserCategory> toDelete = existing.stream()
                .filter(uc -> !newIds.contains(uc.getCategory().getId()))
                .toList();

        List<Long> toAdd = newIds.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();

        userCategoryRepository.deleteAll(toDelete);

        List<Category> categoriesToAdd = categoryRepository.findAllById(toAdd);
        List<UserCategory> toInsert = categoriesToAdd.stream()
                .map(cat -> UserCategory.builder().user(user).category(cat).build())
                .toList();

        userCategoryRepository.saveAll(toInsert);
    }

    @Override
    public List<ChatSummary> getMyDmList(Long userId) {
        return List.of(); // TODO
    }

    @Override
    public List<UsedBookSummary> getMySellBooks(Long userId) {
        return List.of(); // TODO
    }

    @Override
    public List<PurchaseSummary> getMyPurchases(Long userId) {
        return List.of(); // TODO
    }

    @Override
    public List<WishlistItem> getMyWishlist(Long userId) {
        return List.of(); // TODO
    }
}
