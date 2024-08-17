package com.softeer.backend.fo_domain.share.controller;

import com.softeer.backend.fo_domain.share.dto.ShareUrlInfoResponseDto;
import com.softeer.backend.fo_domain.share.service.ShareUrlInfoService;
import com.softeer.backend.global.annotation.AuthInfo;
import com.softeer.backend.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShareController {
    private final ShareUrlInfoService shareUrlInfoService;

    @GetMapping("/share-shorten-url")
    public ResponseDto<ShareUrlInfoResponseDto> getShortenShareUrl(@Parameter(hidden = true) @AuthInfo Integer userId) {
        return ResponseDto.onSuccess(shareUrlInfoService.getShortenShareUrl(userId));
    }

    @GetMapping("/share/{shareUrl}")
    public ResponseEntity<Void> redirectWithShareUrl(@PathVariable String shareUrl, HttpServletRequest request, HttpServletResponse response) {
        // session을 이용해 공유 url 저장
        Cookie shareCodeCookie = new Cookie("shareCode", shareUrl);
        shareCodeCookie.setPath("/");
        shareCodeCookie.setHttpOnly(false); // HttpOnly 속성을 비활성화
        shareCodeCookie.setDomain("softeer.site"); // 도메인 설정. 이렇게 해서 softeer.site 포함 하위 모든 도메인에서 해당 쿠키 사용 가능
        response.addCookie(shareCodeCookie);

        // 헤더를 이용해 redirect
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "https://softeer.site");
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // HTTP 302 Found 응답
    }
}
