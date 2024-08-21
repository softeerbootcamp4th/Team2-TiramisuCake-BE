package com.softeer.backend.bo_domain.admin.controller;

import com.softeer.backend.bo_domain.admin.dto.indicator.EventIndicatorResponseDto;
import com.softeer.backend.bo_domain.admin.service.IndicatorPageService;
import com.softeer.backend.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 어드민 페이지의 이벤트 지표를 처리하는 컨트롤러 클래스
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class IndicatorPageController {

    private final IndicatorPageService indicatorPageService;

    /**
     * 이벤트 지표 데이터를 조회하는 메서드
     */
    @GetMapping("/indicator")
    public ResponseDto<EventIndicatorResponseDto> getEventIndicator() {
        EventIndicatorResponseDto eventIndicatorResponseDto = indicatorPageService.getEventIndicator();

        return ResponseDto.onSuccess(eventIndicatorResponseDto);
    }
}
