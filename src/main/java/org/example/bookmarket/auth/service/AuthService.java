package org.example.bookmarket.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.bookmarket.auth.JwtTokenProvider;
import org.example.bookmarket.auth.dto.LoginRequest;
import org.example.bookmarket.auth.dto.LoginResponse;
import org.example.bookmarket.auth.dto.SignUpRequest;
import org.example.bookmarket.common.handler.exception.CustomException;
import org.example.bookmarket.common.handler.exception.ErrorCode;
import org.example.bookmarket.common.service.RedisService;
import org.example.bookmarket.user.entity.Role;
import org.example.bookmarket.user.entity.User;
import org.example.bookmarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 1. 아이디/비밀번호로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        // 2. 실제 인증 (비밀번호 검증)
        Authentication authentication = authenticationManager.authenticate(authToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // 4. Redis에 Refresh Token 저장 (유효 시간과 함께)
        redisService.setData(
                authentication.getName(), // key: user email
                refreshToken,
                Duration.ofMillis(refreshTokenExpirationMs)
        );

        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public void signUp(SignUpRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.findByNickname(request.nickname()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User newUser = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .role(Role.USER)
                .build();
        userRepository.save(newUser);
    }
}