package org.example.bookmarket.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookmarket.auth.dto.LoginRequest;
import org.example.bookmarket.auth.dto.LoginResponse;
import org.example.bookmarket.auth.dto.SignUpRequest;
import org.example.bookmarket.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<LoginResponse> apiLogin(@Valid @RequestBody LoginRequest request) {
        LoginResponse tokenInfo = authService.login(request);
        return ResponseEntity.ok(tokenInfo);
    }

    // [페이지 반환] 로그인 페이지를 보여줍니다.
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // [페이지 반환] 회원가입 페이지를 보여줍니다.
    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    // [기능 처리] 회원가입을 처리합니다.
    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignUpRequest request) {
        authService.signUp(request);
        return "redirect:/auth/login";
    }
}