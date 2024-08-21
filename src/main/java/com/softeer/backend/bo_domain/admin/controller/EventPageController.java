package com.softeer.backend.bo_domain.admin.controller;

import com.softeer.backend.bo_domain.admin.dto.event.DrawEventTimeRequestDto;
import com.softeer.backend.bo_domain.admin.dto.event.EventPageResponseDto;
import com.softeer.backend.bo_domain.admin.dto.event.FcfsEventTimeRequestDto;
import com.softeer.backend.bo_domain.admin.service.EventPageService;
import com.softeer.backend.global.common.response.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 어드민 페이지의 이벤트 관리 컨트롤러 클래스
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/event")
public class EventPageController {
    private final EventPageService eventPageService;

    /**
     * 이벤트 페이지의 동적 정보 조회하는 메서드
     */
    @GetMapping
    public ResponseDto<EventPageResponseDto> getEventPage() {
        EventPageResponseDto eventPageResponseDto = eventPageService.getEventPage();

        return ResponseDto.onSuccess(eventPageResponseDto);
    }

    /**
     * 선착순 이벤트의 시간 관련 속성을 업데이트하는 메서드
     */
    @PostMapping("/fcfs")
    public ResponseDto<Void> updateFcfsEventTime(@Valid @RequestBody FcfsEventTimeRequestDto fcfsEventTimeRequestDto) {
        eventPageService.updateFcfsEventTime(fcfsEventTimeRequestDto);

        return ResponseDto.onSuccess();
    }

    /**
     * 추첨 이벤트의 시간 관련 속성을 업데이트하는 메서드
     */
    @PostMapping("/draw")
    public ResponseDto<Void> updateDrawEventTime(@Valid @RequestBody DrawEventTimeRequestDto drawEventTimeRequestDto) {
        eventPageService.updateDrawEventTime(drawEventTimeRequestDto);

        return ResponseDto.onSuccess();
    }

}
