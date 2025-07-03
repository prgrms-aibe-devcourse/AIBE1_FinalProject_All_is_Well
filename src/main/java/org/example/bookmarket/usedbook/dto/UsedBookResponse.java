package org.example.bookmarket.usedbook.dto;

public record UsedBookResponse(
        Long id,
        Long bookId,
        String isbn,
        String title,
        String author,
        String publisher,
        Integer publicationYear,
        String conditionGrade,
        boolean hasWriting,
        boolean hasStains,
        boolean hasTears,
        boolean hasWaterDamage,
        boolean likeNew,
        String detailedCondition,
        Integer sellingPrice,
        String status,
        Long categoryId,
        Long sellerId
) {}