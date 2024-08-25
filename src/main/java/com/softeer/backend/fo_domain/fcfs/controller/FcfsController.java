package com.softeer.backend.fo_domain.fcfs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsHistoryResponseDto;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsPageResponseDto;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsRequestDto;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsResultResponseDto;
import com.softeer.backend.fo_domain.fcfs.service.FcfsService;
import com.softeer.backend.global.annotation.AuthInfo;
import com.softeer.backend.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 선착순 이벤트 요청을 처리하는 컨트롤러 클래스
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/fcfs")
public class FcfsController {
    private final FcfsService fcfsService;

    /**
     * 선착순 페이지 정보를 반환하는 메서드
     */
    @GetMapping
    public ResponseDto<FcfsPageResponseDto> getFcfsPage(@Parameter(hidden = true) HttpServletRequest request) {

        int round = (Integer) request.getAttribute("round");

        FcfsPageResponseDto fcfsPageResponseDto = fcfsService.getFcfsPage(round);

        return ResponseDto.onSuccess(fcfsPageResponseDto);
    }

    /**
     * 선착순 튜토리얼 페이지 정보를 반환하는 메서드
     */
    @GetMapping("/tutorial")
    public ResponseDto<FcfsPageResponseDto> getFcfsTutorialPage() {

        FcfsPageResponseDto fcfsPageResponseDto = fcfsService.getFcfsTutorialPage();

        return ResponseDto.onSuccess(fcfsPageResponseDto);
    }

    @PostMapping("/insert")
    public ResponseDto<Void> insertFcfsCode(@RequestParam("num") Integer num, @RequestParam("round") Integer round){
        fcfsService.insertFcfsCode(num, round);

        return ResponseDto.onSuccess();
    }

    /**
     * 선착순 등록을 처리하는 메서드
     */
    @PostMapping
    public ResponseDto<FcfsResultResponseDto> handleFcfs(@Parameter(hidden = true) HttpServletRequest request,
                                     @Parameter(hidden = true) @AuthInfo Integer userId,
                                     @RequestBody FcfsRequestDto fcfsRequestDto) throws JsonProcessingException {

        int round = (Integer) request.getAttribute("round");

        FcfsResultResponseDto fcfsResultResponseDto = fcfsService.handleFcfs(userId, round, fcfsRequestDto);

        return ResponseDto.onSuccess(fcfsResultResponseDto);
    }

    @GetMapping("/history")
    public ResponseDto<FcfsHistoryResponseDto> getFcfsHistory(@Parameter(hidden = true) @AuthInfo Integer userId){

        FcfsHistoryResponseDto fcfsHistoryResponseDto = fcfsService.getFcfsHistory(userId);

        return ResponseDto.onSuccess(fcfsHistoryResponseDto);
    }


}
