package org.example.bookmarket.usedbook.service;

import org.example.bookmarket.usedbook.dto.UsedBookResponse;
import org.example.bookmarket.usedbook.dto.UsedBookSummary;

import java.util.List;

public interface UsedBookQueryService {
    UsedBookResponse getUsedBookById(Long id);
    List<UsedBookSummary> getAllUsedBooks();

    /**
     * [신규 기능 추가] 키워드 리스트로 중고책을 검색하는 메소드
     */
    List<UsedBookResponse> getUsedBooksByKeywords(List<String> keywords);

    /**
     * 최신 등록된 중고책을 조회합니다.
     *
     * @param limit 최대 조회 건수
     * @return 최신 중고책 목록
     */
    List<UsedBookResponse> getLatestUsedBooks(int limit);

    /**
     * 특수 계정 사용자가 등록한 중고책을 조회합니다.
     * @param limit 최대 조회 건수
     * @return 특수 계정 중고책 목록
     */
    List<UsedBookResponse> getSpecialUserBooks(int limit);
}