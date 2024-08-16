package com.softeer.backend.fo_domain.draw.controller;

import com.softeer.backend.fo_domain.draw.dto.main.DrawMainResponseDto;
import com.softeer.backend.fo_domain.draw.dto.participate.DrawModalResponseDto;
import com.softeer.backend.fo_domain.draw.dto.result.DrawHistoryResponseDto;
import com.softeer.backend.fo_domain.draw.service.DrawService;
import com.softeer.backend.global.annotation.AuthInfo;
import com.softeer.backend.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DrawController {
    private final DrawService drawService;

    @GetMapping("/event/draw")
    public ResponseDto<DrawMainResponseDto> getDrawMainPageInfo(@AuthInfo Integer userId) {
        return ResponseDto.onSuccess(drawService.getDrawMainPageInfo(userId));
    }

    @PostMapping("/event/draw")
    public ResponseEntity<Void> participateDrawEvent(@AuthInfo Integer userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/event/draw-result");
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // HTTP 302 Found 응답
    }

    @GetMapping("/event/draw-result")
    public ResponseDto<DrawModalResponseDto> getDrawResult(@AuthInfo Integer userId) {
        return drawService.participateDrawEvent(userId);
    }

    @GetMapping("/event/draw/history")
    public ResponseDto<DrawHistoryResponseDto> getDrawHistory(@AuthInfo Integer userId) {
        return drawService.getDrawHistory(userId);
    }
}
