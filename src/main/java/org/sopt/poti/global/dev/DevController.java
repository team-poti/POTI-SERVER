package org.sopt.poti.global.dev;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.auth.entity.RefreshToken;
import org.sopt.poti.domain.auth.repository.RefreshTokenRepository;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.service.UserService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
@Profile({"local", "dev"})
@Tag(name = "Dev", description = "개발자 테스트용 API")
public class DevController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository; // RefreshTokenRepository 주입

    @Value("${jwt.refresh-token-validity}") // Refresh Token 유효기간 주입
    private long refreshTokenValidity;

    @GetMapping("/login")
    @Operation(summary = "개발자용 토큰 발급 (userId=1)", description = "개발 테스트를 위해 1번 유저의 토큰을 즉시 발급합니다. (로컬/Dev 환경 전용)")
    public ResponseEntity<ApiResponse<DevTokenResponseDto>> devLogin() {
        Long devUserId = 1L;

        User user = userService.getUserById(devUserId);

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // Redis에 Refresh Token 저장
        refreshTokenRepository.save(RefreshToken.builder()
                .userId(user.getId())
                .refreshToken(refreshToken)
                .ttl(refreshTokenValidity / 1000)
                .build());

        DevTokenResponseDto responseDto = DevTokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(SuccessStatus.OK, responseDto));
    }
}
