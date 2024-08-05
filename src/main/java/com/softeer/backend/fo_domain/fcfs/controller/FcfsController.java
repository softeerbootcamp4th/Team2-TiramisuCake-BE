package com.softeer.backend.fo_domain.fcfs.controller;

import com.softeer.backend.fo_domain.fcfs.domain.Fcfs;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsFailResponse;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsResponse;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsSuccessResponse;
import com.softeer.backend.fo_domain.fcfs.service.FcfsService;
import com.softeer.backend.global.annotation.AuthInfo;
import com.softeer.backend.global.common.code.status.SuccessStatus;
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

    @PostMapping("/fcfs")
    public ResponseDto<FcfsResponse> handleFCFS(@Parameter(hidden = true) @AuthInfo Integer userId) {
        FcfsResponse fcfsResponse = fcfsService.handleFcfsEvent(userId);

        if(fcfsResponse instanceof FcfsSuccessResponse)
            return ResponseDto.onSuccess(SuccessStatus._FCFS_SUCCESS, fcfsResponse);

        return ResponseDto.onSuccess(SuccessStatus._FCFS_FAIL, fcfsResponse);
    }

}
