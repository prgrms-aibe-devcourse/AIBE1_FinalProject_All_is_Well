package org.example.bookmarket.usedbook.service;

import lombok.RequiredArgsConstructor;
import org.example.bookmarket.usedbook.dto.UsedBookResponse;
import org.example.bookmarket.usedbook.dto.UsedBookSummary;
import org.example.bookmarket.usedbook.entity.UsedBook;
import org.example.bookmarket.usedbook.repository.UsedBookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsedBookQueryServiceImpl implements UsedBookQueryService {

    private final UsedBookRepository usedBookRepository;

    @Override
    @Transactional(readOnly = true)
    public UsedBookResponse getUsedBookById(Long id) {
        UsedBook ub = usedBookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Used book not found"));
        return toResponse(ub);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsedBookSummary> getAllUsedBooks() {
        return usedBookRepository.findAll().stream()
                .map(this::toSummary)
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
                ub.isHasWriting(),
                ub.isHasStains(),
                ub.isHasTears(),
                ub.isHasWaterDamage(),
                ub.isLikeNew(),
                ub.getDetailedCondition(),
                ub.getSellingPrice(),
                ub.getStatus(),
                ub.getCategory().getId(),
                ub.getSeller() != null ? ub.getSeller().getId() : null
        );
    }

    private UsedBookSummary toSummary(UsedBook ub) {
        return new UsedBookSummary(
                ub.getId(),
                ub.getBook().getTitle(),
                ub.getSellingPrice(),
                ub.getStatus(),
                null,
                ub.getUpdatedAt()
        );
    }
}