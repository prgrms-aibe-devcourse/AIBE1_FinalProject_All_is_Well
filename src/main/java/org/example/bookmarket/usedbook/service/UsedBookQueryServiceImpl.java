package org.example.bookmarket.usedbook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookmarket.ai.service.AiService;
import org.example.bookmarket.usedbook.dto.UsedBookResponse;
import org.example.bookmarket.usedbook.dto.UsedBookSummary;
import org.example.bookmarket.usedbook.entity.UsedBook;
import org.example.bookmarket.usedbook.repository.UsedBookRepository;
import org.example.bookmarket.specialaccount.service.SpecialAccountService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsedBookQueryServiceImpl implements UsedBookQueryService {

    private final UsedBookRepository usedBookRepository;
    private final AiService aiService;
    private final SpecialAccountService specialAccountService;

    @Override
    @Transactional(readOnly = true)
    public UsedBookResponse getUsedBookById(Long id) {
        UsedBook ub = usedBookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Used book not found"));
        return toResponseWithAi(ub);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsedBookSummary> getAllUsedBooks() {
        return usedBookRepository.findAll().stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsedBookResponse> getLatestUsedBooks(int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"));
        // 특수 계정 판매글도 함께 노출하기 위해 별도의 제외 처리 없이 전체 조회합니다.
        List<UsedBook> books = usedBookRepository.findAll(pageRequest).getContent();
        return books.stream()
        .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsedBookResponse> getUsedBooksByKeywords(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return Collections.emptyList();
        }
        return usedBookRepository.findByKeywords(keywords).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsedBookResponse> getSpecialUserBooks(int limit) {
        List<String> special = specialAccountService.getActiveNicknames();
        if (special.isEmpty()) {
            return Collections.emptyList();
        }
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"));
        return usedBookRepository.findBySellerNicknames(special, pageRequest).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private UsedBookResponse toResponse(UsedBook ub) {
        return new UsedBookResponse(
                ub.getId(),
                ub.getBook().getId(),
                ub.getBook().getIsbn(),
                ub.getBook().getTitle(),
                ub.getBook().getAuthor(),
                ub.getBook().getPublisher(),
                ub.getBook().getPublicationYear(),
                ub.getConditionGrade(),
                ub.getDetailedCondition(),
                ub.getSellingPrice(),
                ub.getStatus(),
                ub.getCategory().getId(),
                ub.getSeller() != null ? ub.getSeller().getId() : null,
                ub.getSeller() != null ? ub.getSeller().getNickname() : null,
                ub.getSeller() != null ? ub.getSeller().getProfileImageUrl() : null,
                ub.getBook().getCoverImageUrl(),
                null,
                null
        );
    }

    /**
     *  AI 호출을 비동기 병렬 처리 방식으로 변경하여 성능 개선
     */
    private UsedBookResponse toResponseWithAi(UsedBook ub) {
        String bookInfo = String.format(
                "제목: %s, 저자: %s, 설명: %s",
                ub.getBook().getTitle(),
                ub.getBook().getAuthor(),
                ub.getBook().getDescription()
        );

        // 1. 두 개의 AI 작업을 비동기적으로 동시에 호출합니다.
        CompletableFuture<String> summaryFuture = aiService.summarizeBookAsync(bookInfo);
        CompletableFuture<String> reviewFuture = aiService.reviewWithPersonaAsync(bookInfo);

        // 2. 두 작업이 모두 완료될 때까지 기다립니다.
        CompletableFuture.allOf(summaryFuture, reviewFuture).join();

        String summary = "";
        String personaReview = "";

        try {
            // 3. 완료된 결과를 가져옵니다.
            summary = summaryFuture.get();
            personaReview = reviewFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("AI 비동기 작업 결과 조회 중 오류 발생. 책 ID: {}", ub.getId(), e);
            // 예외가 발생하더라도 스레드 인터럽트 상태를 유지해주는 것이 좋습니다.
            Thread.currentThread().interrupt();
        }

        return new UsedBookResponse(
                ub.getId(),
                ub.getBook().getId(),
                ub.getBook().getIsbn(),
                ub.getBook().getTitle(),
                ub.getBook().getAuthor(),
                ub.getBook().getPublisher(),
                ub.getBook().getPublicationYear(),
                ub.getConditionGrade(),
                ub.getDetailedCondition(),
                ub.getSellingPrice(),
                ub.getStatus(),
                ub.getCategory().getId(),
                ub.getSeller() != null ? ub.getSeller().getId() : null,
                ub.getSeller() != null ? ub.getSeller().getNickname() : null,
                ub.getSeller() != null ? ub.getSeller().getProfileImageUrl() : null,
                ub.getBook().getCoverImageUrl(),
                summary,
                personaReview
        );
    }

    private UsedBookSummary toSummary(UsedBook ub) {
        String thumbnailUrl = ub.getImages().isEmpty() ? null : ub.getImages().get(0).getImageUrl();

        return new UsedBookSummary(
                ub.getId(),
                ub.getBook().getTitle(),
                ub.getSellingPrice(),
                ub.getStatus(),
                thumbnailUrl,
                ub.getUpdatedAt()
        );
    }
}