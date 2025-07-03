package org.example.bookmarket.usedbook.controller;

import lombok.RequiredArgsConstructor;
import org.example.bookmarket.book.dto.BookResponse;
import org.example.bookmarket.book.service.BookService;
import org.example.bookmarket.usedbook.dto.UsedBookPostRequest;
import org.example.bookmarket.usedbook.service.UsedBookPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/used-books")
@RequiredArgsConstructor
public class UsedBookPostController {

    private final UsedBookPostService postService;
    private final BookService bookService;

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookResponse> getBookInfo(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.getBookByIsbn(isbn));
    }

    @PostMapping
    public ResponseEntity<String> registerUsedBook(@ModelAttribute UsedBookPostRequest request) {
        postService.registerUsedBook(request);
        return ResponseEntity.ok("중고도서가 등록되었습니다.");
    }
}