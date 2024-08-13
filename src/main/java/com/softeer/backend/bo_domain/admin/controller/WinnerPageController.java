package com.softeer.backend.bo_domain.admin.controller;

import com.softeer.backend.bo_domain.admin.dto.event.EventPageResponseDto;
import com.softeer.backend.bo_domain.admin.dto.winner.*;
import com.softeer.backend.bo_domain.admin.service.WinnerPageService;
import com.softeer.backend.global.common.response.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/winner")
public class WinnerPageController {
    private final WinnerPageService winnerPageService;

    @GetMapping
    public ResponseDto<WinnerPageResponseDto> getWinnerPage(){
        WinnerPageResponseDto winnerPageResponseDto = winnerPageService.getWinnerPage();

        return ResponseDto.onSuccess(winnerPageResponseDto);
    }

    @GetMapping("/fcfs/{round}")
    public ResponseDto<FcfsWinnerListResponseDto> getFcfsWinnerList(@PathVariable Integer round) {

        FcfsWinnerListResponseDto fcfsWinnerListResponseDto = winnerPageService.getFcfsWinnerList(round);

        return ResponseDto.onSuccess(fcfsWinnerListResponseDto);
    }

    @GetMapping("/draw/{rank}")
    public ResponseDto<DrawWinnerListResponseDto> getDrawWinnerList(@PathVariable Integer rank) {

        DrawWinnerListResponseDto drawWinnerListResponseDto = winnerPageService.getDrawWinnerList(rank);

        return ResponseDto.onSuccess(drawWinnerListResponseDto);
    }

    @PostMapping("/fcfs")
    public ResponseDto<Void> updateFcfsWinnerNum(@Valid @RequestBody FcfsWinnerUpdateRequestDto fcfsWinnerUpdateRequestDto) {

        winnerPageService.updateFcfsWinnerNum(fcfsWinnerUpdateRequestDto);

        return ResponseDto.onSuccess();
    }

    @PostMapping("/draw")
    public ResponseDto<Void> updateFcfsWinnerNum(@Valid @RequestBody DrawWinnerUpdateRequestDto drawWinnerUpdateRequestDto) {

        winnerPageService.updateDrawWinnerNum(drawWinnerUpdateRequestDto);

        return ResponseDto.onSuccess();
    }


}
