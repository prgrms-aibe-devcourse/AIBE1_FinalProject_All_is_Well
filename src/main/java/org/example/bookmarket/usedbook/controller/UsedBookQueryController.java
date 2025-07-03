package org.example.bookmarket.usedbook.controller;

import lombok.RequiredArgsConstructor;
import org.example.bookmarket.usedbook.dto.UsedBookResponse;
import org.example.bookmarket.usedbook.dto.UsedBookSummary;
import org.example.bookmarket.usedbook.service.UsedBookQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/used-books")
@RequiredArgsConstructor
public class UsedBookQueryController {

    private final UsedBookQueryService usedBookQueryService;

    @GetMapping("/{id}")
    public ResponseEntity<UsedBookResponse> getUsedBook(@PathVariable Long id) {
        return ResponseEntity.ok(usedBookQueryService.getUsedBookById(id));
    }

    @GetMapping
    public ResponseEntity<List<UsedBookSummary>> getAllUsedBooks() {
        return ResponseEntity.ok(usedBookQueryService.getAllUsedBooks());
    }
}