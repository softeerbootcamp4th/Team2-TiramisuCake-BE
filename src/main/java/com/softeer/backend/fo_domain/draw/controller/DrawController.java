package com.softeer.backend.fo_domain.draw.controller;

import com.softeer.backend.fo_domain.draw.dto.DrawResponseDto;
import com.softeer.backend.fo_domain.draw.service.DrawService;
import com.softeer.backend.global.annotation.AuthInfo;
import com.softeer.backend.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DrawController {
    private final DrawService drawService;

    @GetMapping("/event/draw")
    public ResponseDto<DrawResponseDto> getDrawMainPageInfo(@Parameter(hidden = true) @AuthInfo Integer userId) {
        return drawService.getDrawMainPageInfo(userId);
    }
}
