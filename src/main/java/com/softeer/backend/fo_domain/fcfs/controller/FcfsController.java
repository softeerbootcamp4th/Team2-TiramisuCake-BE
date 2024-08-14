package com.softeer.backend.fo_domain.fcfs.controller;

import com.softeer.backend.fo_domain.fcfs.dto.FcfsResponseDto;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsSuccessResponseDto;
import com.softeer.backend.fo_domain.fcfs.service.FcfsService;
import com.softeer.backend.global.annotation.AuthInfo;
import com.softeer.backend.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Fcfs Controller", description = "선착순 API")
public class FcfsController {
    private final FcfsService fcfsService;

//    @PostMapping("/fcfs")
//    public ResponseDto<FcfsResponseDto> handleFCFS(@Parameter(hidden = true) @AuthInfo Integer userId) {
//        FcfsResponseDto fcfsResponse = fcfsService.handleFcfsEvent(userId);
//
//        if (fcfsResponse instanceof FcfsSuccessResponseDto)
//            return ResponseDto.onSuccess(fcfsResponse);
//
//        return ResponseDto.onSuccess(fcfsResponse);
//    }

}
