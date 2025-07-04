package org.example.bookmarket.usedbook.controller;

import lombok.RequiredArgsConstructor;
import org.example.bookmarket.usedbook.service.UsedBookPurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/used-books")
@RequiredArgsConstructor
public class UsedBookPurchaseController {

    private final UsedBookPurchaseService purchaseService;

    @PostMapping("/{bookId}/purchase")
    public ResponseEntity<String> purchaseBook(@PathVariable Long bookId) {
        try {
            purchaseService.purchase(bookId);
            return ResponseEntity.ok("책 구매에 성공했습니다. ID: " + bookId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}