package org.example.bookmarket.book.service;

import org.example.bookmarket.book.dto.BookResponse;

import org.example.bookmarket.book.entity.Book;

public interface BookService {
    BookResponse getBookByIsbn(String isbn);

    /**
     * ISBN으로 책을 조회하고, 존재하지 않으면 외부 API를 통해 정보를 가져와 저장합니다.
     */

    Book getOrCreateByIsbn(String isbn);
}
