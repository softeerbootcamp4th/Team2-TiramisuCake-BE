package com.softeer.backend.fo_domain.draw.controller;

import com.softeer.backend.fo_domain.draw.dto.main.DrawMainResponseDto;
import com.softeer.backend.fo_domain.draw.dto.participate.DrawModalResponseDto;
import com.softeer.backend.fo_domain.draw.dto.history.DrawHistoryResponseDto;
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

/**
 * 추첨 이벤트 컨트롤러 클래스
 */
@RestController
@RequiredArgsConstructor
public class DrawController {
    private final DrawService drawService;

    /**
     * 추첨 이벤트 페이지 접속 관련 정보 반환하는 메서드
     */
    @GetMapping("/event/draw")
    public ResponseDto<DrawMainResponseDto> getDrawMainPageInfo(@AuthInfo Integer userId) {
        return ResponseDto.onSuccess(drawService.getDrawMainPageInfo(userId));
    }

    /**
     * 추첨 이벤트 참여 메서드
     */
    @PostMapping("/event/draw")
    public ResponseEntity<Void> participateDrawEvent() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/event/draw-result");
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // HTTP 302 Found 응답
    }

    /**
     * 추첨 이벤트 결과 반환하는 메서드
     */
    @GetMapping("/event/draw-result")
    public ResponseDto<DrawModalResponseDto> getDrawResult(@AuthInfo Integer userId) {
        return ResponseDto.onSuccess(drawService.participateDrawEvent(userId));
    }

    /**
     * 추첨 이벤트 당첨 내역 반환하는 메서드
     */
    @GetMapping("/event/draw/history")
    public ResponseDto<DrawHistoryResponseDto> getDrawHistory(@AuthInfo Integer userId) {
        return ResponseDto.onSuccess(drawService.getDrawHistory(userId));
    }
}
