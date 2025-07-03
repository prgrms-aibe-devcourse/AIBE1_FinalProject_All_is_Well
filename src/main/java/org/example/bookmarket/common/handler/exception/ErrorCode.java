package org.example.bookmarket.common.handler.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // == User & Auth Errors == //
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."),
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),

    // == JWT Errors == //
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "토큰을 찾을 수 없습니다."),

    // == Common Errors == //
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 오류가 발생했습니다."),

    // === Profile Errors ===
    PROFILE_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "프로필의 사용자 정보를 찾을 수 없습니다."),
    INVALID_PROFILE_IMAGE(HttpStatus.BAD_REQUEST, "잘못된 프로필 이미지입니다."),
    NICKNAME_DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),

    // === Category Errors ===
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
    CATEGORY_DUPLICATED(HttpStatus.CONFLICT, "이미 존재하는 카테고리입니다."),

    // === UsedBook Errors ===
    USED_BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 중고도서를 찾을 수 없습니다."),
    USED_BOOK_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "중고도서 등록 시 이미지는 최소 1장 이상 필요합니다."),
    INVALID_BOOK_CONDITION(HttpStatus.BAD_REQUEST, "잘못된 도서 상태 정보입니다."),

    // == Book Errors == //
    INVALID_ISBN(HttpStatus.BAD_REQUEST, "올바른 ISBN 형식이 아닙니다."),
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 도서를 찾을 수 없습니다."),

    // == 외부 시스템 연동 오류 ==
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 이미지 업로드에 실패했습니다."),
    AI_ANALYSIS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI 이미지 분석에 실패했습니다."),

    // === External API Errors ===

    EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "외부도서 api 호출에 실패했습니다.");

    private final HttpStatus statusCode;
    private final String message;
}