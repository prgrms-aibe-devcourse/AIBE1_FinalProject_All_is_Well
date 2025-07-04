package org.example.bookmarket.usedbook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookmarket.ai.dto.PriceSuggestResponse;
import org.example.bookmarket.ai.service.AiService;
import org.example.bookmarket.book.entity.Book;
import org.example.bookmarket.book.repository.BookRepository;
import org.example.bookmarket.common.handler.exception.CustomException;
import org.example.bookmarket.common.handler.exception.ErrorCode;
import org.example.bookmarket.common.service.S3UploadService;
import org.example.bookmarket.usedbook.dto.UsedBookPostRequest;
import org.example.bookmarket.usedbook.entity.UsedBook;
import org.example.bookmarket.usedbook.entity.UsedBookImage;
import org.example.bookmarket.usedbook.repository.UsedBookRepository;
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
    private final BookRepository bookRepository;
    private final AiService aiService;
    private final S3UploadService s3UploadService;

    @Transactional
    public void registerUsedBook(UsedBookPostRequest request) {
        // 1. ISBN으로 책 정보 조회 또는 신규 생성
        Book book = bookRepository.findByIsbn(request.isbn())
                .orElseGet(() -> {
                    log.info("새로운 책 정보를 등록합니다. ISBN: {}", request.isbn());
                    return bookRepository.save(
                            Book.builder()
                                    .isbn(request.isbn())
                                    .title(request.title())
                                    .author(request.author())
                                    .publisher(request.publisher())
                                    .publicationYear(request.publicationYear())
                                    // TODO: 외부 API를 통해 새 책 가격 정보를 가져오는 로직 추가 필요
                                    .build()
                    );
                });

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
            // TODO: book.getNewPrice() 로 실제 새 책 가격을 가져와야 합니다.
            aiResponse = aiService.suggestPriceFromImage(representativeImageUrl, 30000);
        } catch (IOException e) {
            log.error("AI 이미지 분석 중 오류 발생", e);
            // AI 분석에 실패하더라도 등록은 계속 진행하되, 추천 가격 정보는 비워둡니다.

        }

        // 4. 최종 UsedBook 엔티티 생성
        UsedBook usedBook = UsedBook.builder()
                .seller(null) // TODO: SecurityContext에서 현재 로그인한 사용자 정보 주입 필요
                .book(book)
                .conditionGrade(request.conditionGrade())
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
