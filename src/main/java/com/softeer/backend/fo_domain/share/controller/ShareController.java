package com.softeer.backend.fo_domain.share.controller;

import com.softeer.backend.fo_domain.share.dto.ShareUrlResponseDto;
import com.softeer.backend.fo_domain.share.service.ShareService;
import com.softeer.backend.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShareController {
    private final ShareService shareService;

    @GetMapping("/share-shorten-url")
    public ResponseDto<ShareUrlResponseDto> getShortenShareUrl(@RequestHeader("Authorization") String jwtToken) {
        return shareService.getShortenShareUrl(jwtToken);
    }
}
