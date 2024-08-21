package com.softeer.backend.bo_domain.admin.controller;

import com.softeer.backend.bo_domain.admin.dto.winner.*;
import com.softeer.backend.bo_domain.admin.service.WinnerPageService;
import com.softeer.backend.global.common.response.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 어드민 페이지의 당첨 관리 페이지를 처리하는 컨트롤러 클래스
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/winner")
public class WinnerPageController {
    private final WinnerPageService winnerPageService;

    /**
     * 당첨 관리 페이지의 정보를 반환하는 메서드
     */
    @GetMapping
    public ResponseDto<WinnerPageResponseDto> getWinnerPage() {
        WinnerPageResponseDto winnerPageResponseDto = winnerPageService.getWinnerPage();

        return ResponseDto.onSuccess(winnerPageResponseDto);
    }

    /**
     * 특정 라운드의 선착순 이벤트 당첨자를 반환하는 메서드
     */
    @GetMapping("/fcfs/{round}")
    public ResponseDto<FcfsWinnerListResponseDto> getFcfsWinnerList(@PathVariable Integer round) {

        FcfsWinnerListResponseDto fcfsWinnerListResponseDto = winnerPageService.getFcfsWinnerList(round);

        return ResponseDto.onSuccess(fcfsWinnerListResponseDto);
    }

    /**
     * 특정 등수의 추첨 이벤트 당첨자를 반환하는 메서드
     */
    @GetMapping("/draw/{rank}")
    public ResponseDto<DrawWinnerListResponseDto> getDrawWinnerList(@PathVariable Integer rank) {

        DrawWinnerListResponseDto drawWinnerListResponseDto = winnerPageService.getDrawWinnerList(rank);

        return ResponseDto.onSuccess(drawWinnerListResponseDto);
    }

    /**
     * 선착순 당첨자 수를 수정하는 메서드
     */
    @PostMapping("/fcfs")
    public ResponseDto<Void> updateFcfsWinnerNum(@Valid @RequestBody FcfsWinnerUpdateRequestDto fcfsWinnerUpdateRequestDto) {

        winnerPageService.updateFcfsWinnerNum(fcfsWinnerUpdateRequestDto);

        return ResponseDto.onSuccess();
    }

    /**
     * 추첨 당첨자 수를 수정하는 메서드
     */
    @PostMapping("/draw")
    public ResponseDto<Void> updateFcfsWinnerNum(@Valid @RequestBody DrawWinnerUpdateRequestDto drawWinnerUpdateRequestDto) {

        winnerPageService.updateDrawWinnerNum(drawWinnerUpdateRequestDto);

        return ResponseDto.onSuccess();
    }
}
