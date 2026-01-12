package org.sopt.poti.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.user.dto.request.NicknameDuplicateRequest;
import org.sopt.poti.domain.user.dto.response.NicknameDuplicateResponse;
import org.sopt.poti.domain.user.service.UserNicknameService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserNicknameController {

    private final UserNicknameService userNicknameService;

    @PostMapping("/nickname/duplicate")
    public ResponseEntity<ApiResponse<NicknameDuplicateResponse>> checkNicknameDuplicate(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody NicknameDuplicateRequest request
    ) {
        NicknameDuplicateResponse data = userNicknameService.checkDuplicate(request);

        return ResponseEntity
                .status(SuccessStatus.OK.getHttpStatus())
                .body(ApiResponse.success(SuccessStatus.OK, data));
    }
}