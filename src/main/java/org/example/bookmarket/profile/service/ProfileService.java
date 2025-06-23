
package org.example.bookmarket.profile.service;

import com.bukgeokbukgeok.profile.dto.ProfileResponse;
import com.bukgeokbukgeok.profile.dto.ProfileUpdateRequest;
import com.bukgeokbukgeok.chat.dto.ChatSummary;
import com.bukgeokbukgeok.usedbook.dto.UsedBookSummary;
import com.bukgeokbukgeok.trade.dto.PurchaseSummary;
import com.bukgeokbukgeok.wishlist.dto.WishlistItem;

import java.util.List;

public interface ProfileService {

    ProfileResponse getMyProfile(Long userId);

    void updateMyProfile(Long userId, ProfileUpdateRequest request);

    List<ChatSummary> getMyDmList(Long userId);

    List<UsedBookSummary> getMySellBooks(Long userId);

    List<PurchaseSummary> getMyPurchases(Long userId);

    List<WishlistItem> getMyWishlist(Long userId);
}
