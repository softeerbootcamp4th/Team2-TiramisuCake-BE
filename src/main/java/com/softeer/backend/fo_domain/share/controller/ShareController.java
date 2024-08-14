package com.softeer.backend.fo_domain.share.controller;

import com.softeer.backend.fo_domain.share.dto.ShareUrlInfoResponseDto;
import com.softeer.backend.fo_domain.share.service.ShareUrlInfoService;
import com.softeer.backend.global.annotation.AuthInfo;
import com.softeer.backend.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShareController {
    private final ShareUrlInfoService shareUrlInfoService;

    @GetMapping("/share-shorten-url")
    public ResponseDto<ShareUrlInfoResponseDto> getShortenShareUrl(@Parameter(hidden = true) @AuthInfo Integer userId) {
        return shareUrlInfoService.getShortenShareUrl(userId);
    }
}
