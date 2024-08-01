package com.softeer.backend.fo_domain.share.controller;

import com.softeer.backend.fo_domain.share.dto.ShareUrlResponseDto;
import com.softeer.backend.fo_domain.share.service.ShareInfoService;
import com.softeer.backend.global.annotation.AuthInfo;
import com.softeer.backend.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShareInfoController {
    private final ShareInfoService shareInfoService;

    @GetMapping("/share-shorten-url")
    public ResponseDto<ShareUrlResponseDto> getShortenShareUrl(@AuthInfo Integer userId) {
        return shareInfoService.getShortenShareUrl(userId);
    }
}
