package com.softeer.backend.bo_domain.admin.controller;

import com.softeer.backend.bo_domain.admin.dto.event.DrawEventTimeRequestDto;
import com.softeer.backend.bo_domain.admin.dto.event.EventPageResponseDto;
import com.softeer.backend.bo_domain.admin.dto.event.FcfsEventTimeRequestDto;
import com.softeer.backend.bo_domain.admin.service.EventPageService;
import com.softeer.backend.global.common.response.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/event")
public class EventPageController {

    private final EventPageService eventPageService;

    @GetMapping
    public ResponseDto<EventPageResponseDto> getEventPage(){
        EventPageResponseDto eventPageResponseDto = eventPageService.getEventPage();

        return ResponseDto.onSuccess(eventPageResponseDto);
    }

    @PostMapping("/fcfs")
    public ResponseDto<Void> updateFcfsEventTime(@Valid @RequestBody FcfsEventTimeRequestDto fcfsEventTimeRequestDto) {
        eventPageService.updateFcfsEventTime(fcfsEventTimeRequestDto);

        return ResponseDto.onSuccess();
    }

    @PostMapping("/draw")
    public ResponseDto<Void> updateDrawEventTime(@Valid @RequestBody DrawEventTimeRequestDto drawEventTimeRequestDto) {
        eventPageService.updateDrawEventTime(drawEventTimeRequestDto);

        return ResponseDto.onSuccess();
    }

}
