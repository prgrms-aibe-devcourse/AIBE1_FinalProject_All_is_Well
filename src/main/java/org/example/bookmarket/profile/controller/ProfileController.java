package org.example.bookmarket.profile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

//    private final ProfileService profileService;

//    @GetMapping("/me")
//    public ResponseEntity<ProfileResponse> getMyProfile(@RequestParam Long userId) {
//        return ResponseEntity.ok(profileService.getMyProfile(userId));
//    }
//
//    @PatchMapping("/me")
//    public ResponseEntity<Void> updateMyProfile(@RequestParam Long userId,
//                                                @RequestBody ProfileUpdateRequest request) {
//        profileService.updateMyProfile(userId, request);
//        return ResponseEntity.noContent().build();
//    }

//    @GetMapping("/me/dms")
//    public ResponseEntity<List<ChatSummary>> getMyDmList(@RequestParam Long userId) {
//        return ResponseEntity.ok(profileService.getMyDmList(userId));
//    }
//
//    @GetMapping("/me/sell-books")
//    public ResponseEntity<List<UsedBookSummary>> getMySellBooks(@RequestParam Long userId) {
//        return ResponseEntity.ok(profileService.getMySellBooks(userId));
//    }
//
//    @GetMapping("/me/purchases")
//    public ResponseEntity<List<PurchaseSummary>> getMyPurchases(@RequestParam Long userId) {
//        return ResponseEntity.ok(profileService.getMyPurchases(userId));
//    }
//
//    @GetMapping("/me/wishlist")
//    public ResponseEntity<List<WishlistItem>> getMyWishlist(@RequestParam Long userId) {
//        return ResponseEntity.ok(profileService.getMyWishlist(userId));
//    }
}