package com.softeer.backend.bo_domain.admin.controller;

import com.softeer.backend.bo_domain.admin.dto.indicator.EventIndicatorResponseDto;
import com.softeer.backend.bo_domain.admin.service.IndicatorPageService;
import com.softeer.backend.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class IndicatorPageController {

    private final IndicatorPageService indicatorPageService;

    @GetMapping("/indicator")
    public ResponseDto<EventIndicatorResponseDto> getEventIndicator() {
        EventIndicatorResponseDto eventIndicatorResponseDto = indicatorPageService.getEventIndicator();

        return ResponseDto.onSuccess(eventIndicatorResponseDto);
    }
}
