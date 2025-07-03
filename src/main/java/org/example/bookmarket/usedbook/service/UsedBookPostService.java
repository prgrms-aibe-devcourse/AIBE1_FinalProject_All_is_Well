package org.example.bookmarket.usedbook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookmarket.ai.dto.PriceSuggestResponse;
import org.example.bookmarket.ai.service.AiService;
import org.example.bookmarket.book.entity.Book;
import org.example.bookmarket.book.service.BookService;
import org.example.bookmarket.book.repository.BookRepository;
import org.example.bookmarket.category.entity.Category;
import org.example.bookmarket.category.repository.CategoryRepository;
import org.example.bookmarket.common.handler.exception.CustomException;
import org.example.bookmarket.common.handler.exception.ErrorCode;
import org.example.bookmarket.user.entity.SocialType;
import org.example.bookmarket.common.service.S3UploadService;
import org.example.bookmarket.usedbook.dto.UsedBookPostRequest;
import org.example.bookmarket.usedbook.entity.UsedBook;
import org.example.bookmarket.usedbook.entity.UsedBookImage;
import org.example.bookmarket.usedbook.repository.UsedBookRepository;
import org.example.bookmarket.user.entity.User;
import org.example.bookmarket.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsedBookPostService {

    private final UsedBookRepository usedBookRepository;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final AiService aiService;
    private final S3UploadService s3UploadService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;


    @Transactional
    public void registerUsedBook(UsedBookPostRequest request) {
        Book book = bookService.getOrCreateByIsbn(request.isbn());

        // 입력된 새 책 가격이 있다면 책 정보에 반영합니다.
        if (request.newPrice() != null) {
            if (book.getNewPrice() == null || !book.getNewPrice().equals(request.newPrice())) {
                book.setNewPrice(request.newPrice());
                bookRepository.save(book);
            }
        }

        // 2. 이미지들을 S3에 업로드하고 URL 리스트를 받아옵니다.
        List<String> imageUrls = new ArrayList<>();
        if (request.images() != null && !request.images().isEmpty()) {
            for (MultipartFile imageFile : request.images()) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    // S3UploadService에서 예외가 발생하면 GlobalExceptionHandler가 처리합니다.
                    String imageUrl = s3UploadService.upload(imageFile, "used-book-images");
                    imageUrls.add(imageUrl);
                }
            }
        }

        // AI 분석을 위해 이미지가 최소 1장 이상 필요합니다.
        if (imageUrls.isEmpty()) {
            throw new CustomException(ErrorCode.USED_BOOK_IMAGE_REQUIRED);
        }

        // 3. AI 서비스 호출
        String representativeImageUrl = imageUrls.get(0); // 첫 번째 이미지를 대표 이미지로 사용
        PriceSuggestResponse aiResponse = null;
        try {
            int basePrice = book.getNewPrice() != null ? book.getNewPrice() :
                    (request.newPrice() != null ? request.newPrice() : 30000);
            aiResponse = aiService.suggestPriceFromImage(representativeImageUrl, basePrice);
        } catch (IOException e) {
            log.error("AI 이미지 분석 중 오류 발생", e);
            // AI 분석에 실패하더라도 등록은 계속 진행하되, 추천 가격 정보는 비워둡니다.

        }

        // 판매자 조회 - 현재 로그인한 사용자
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.LOGIN_REQUIRED);
        }

        Object principal = authentication.getPrincipal();
        User seller = null;
        if (principal instanceof User user) {
            seller = user;
        } else if (principal instanceof OAuth2User oauth2User) {
            String socialId = oauth2User.getAttribute("id").toString();
            seller = userRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, socialId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        }

        if (seller == null) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // 4. 최종 UsedBook 엔티티 생성
        UsedBook usedBook = UsedBook.builder()
                .seller(seller)
                .book(book)
                .category(category)
                .conditionGrade(request.conditionGrade())
                .hasWriting(request.hasWriting())
                .hasStains(request.hasStains())
                .hasTears(request.hasTears())
                .hasWaterDamage(request.hasWaterDamage())
                .likeNew(request.likeNew())
                .detailedCondition(request.detailedCondition())
                .sellingPrice(request.sellingPrice())
                // AI 분석 결과를 저장합니다.
                .aiSuggestedMinPrice(aiResponse != null ? aiResponse.getSuggestedMinPrice() : null)
                .aiSuggestedMaxPrice(aiResponse != null ? aiResponse.getSuggestedMaxPrice() : null)
                .aiDetectedDefects(aiResponse != null ? aiResponse.getDetectedDefects() : null)
                .status("판매중")
                .build();

        // 이미지 URL들을 UsedBookImage 엔티티로 변환하여 연결
        List<UsedBookImage> usedBookImages = imageUrls.stream()
                .map(url -> UsedBookImage.builder().imageUrl(url).usedBook(usedBook).build())
                .toList();
        usedBook.setImages(usedBookImages);

        // 5. 데이터베이스에 저장
        usedBookRepository.save(usedBook);
        log.info("새로운 중고책이 등록되었습니다. ID: {}", usedBook.getId());
    }
}