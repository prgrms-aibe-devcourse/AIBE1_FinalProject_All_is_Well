package org.example.bookmarket.common.controller;

import lombok.RequiredArgsConstructor;
import org.example.bookmarket.recommendation.service.RecommendationService;
import org.example.bookmarket.banner.service.BannerService;
import org.example.bookmarket.usedbook.dto.UsedBookResponse;
import org.example.bookmarket.usedbook.service.UsedBookQueryService;
import org.example.bookmarket.user.entity.User;
import org.example.bookmarket.user.entity.SocialType;
import org.example.bookmarket.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserRepository userRepository;
    private final UsedBookQueryService usedBookQueryService;
    private final RecommendationService recommendationService;
    private final BannerService bannerService;

    @GetMapping("/")
    public String root(Model model, Authentication authentication) {

        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                currentUser = (User) principal;
            } else if (principal instanceof OAuth2User) {
                Object idAttr = ((OAuth2User) principal).getAttribute("id");
                if (idAttr != null) {
                    currentUser = userRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, idAttr.toString()).orElse(null);
                }
            }
        }

        // 배너 데이터
        model.addAttribute("banners", bannerService.getBanners());

        // 1. AI 맞춤 추천 도서 데이터
        Long userId = (currentUser != null) ? currentUser.getId() : null;
        List<UsedBookResponse> recommendedBooks = recommendationService.getPersonalizedRecommendations(userId, 4);
        model.addAttribute("recommendedBooks", recommendedBooks);

        // 특수 계정 판매글
        List<UsedBookResponse> specialBooks = usedBookQueryService.getSpecialUserBooks(4);
        model.addAttribute("specialUserBooks", specialBooks);

        // 2. 방금 올라온 따끈한 책 데이터 (그대로 유지)
        List<UsedBookResponse> newlyAddedBooks = usedBookQueryService.getLatestUsedBooks(4);
        model.addAttribute("newlyAddedBooks", newlyAddedBooks);

        // 3. 지금 많이 찾는 책 (태그) 데이터
        List<String> popularTags = List.of("클린코드", "네트워크", "JPA", "운영체제", "HTTP");
        model.addAttribute("popularTags", popularTags);

        if (currentUser != null) {
            model.addAttribute("nickname", currentUser.getNickname());
            model.addAttribute("profileImageUrl", currentUser.getProfileImageUrl());
        }

        return "home";
    }

    /**
     * 간단한 프로모션 페이지를 보여줍니다.
     */
    @GetMapping("/promo")
    public String promoPage() {
        return "promo";
    }
}