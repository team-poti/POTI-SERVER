package org.sopt.poti.global.dev;

import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.repository.UserRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.sopt.poti.global.security.jwt.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
public class DevController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 개발자 테스트용 자동 로그인 (userId=1)
     * 실제 서비스에서는 사용되지 않으며, 개발 단계에서 토큰 발급 편의를 위해 존재
     * DB에 id=1인 유저가 미리 존재해야 함
     */
    @GetMapping("/login")
    public ResponseEntity<DevTokenResponseDto> devLogin() {
        // 개발자 테스트용 유저 ID (예: 1)
        Long devUserId = 1L;

        // DB에서 유저 조회 (없으면 예외 발생)
        User user = userRepository.findById(devUserId)
                .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));

        // Access Token 및 Refresh Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId()); // Refresh Token은 Redis에 저장하는 로직 필요 (이 컨트롤러에서는 생략)

        // 토큰 응답 DTO 생성
        DevTokenResponseDto responseDto = DevTokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return ResponseEntity.ok(responseDto);
    }
}
