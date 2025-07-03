package org.example.bookmarket.usedbook.service;

import org.example.bookmarket.usedbook.dto.UsedBookResponse;
import org.example.bookmarket.usedbook.dto.UsedBookSummary;

import java.util.List;

public interface UsedBookQueryService {
    UsedBookResponse getUsedBookById(Long id);
    List<UsedBookSummary> getAllUsedBooks();
}