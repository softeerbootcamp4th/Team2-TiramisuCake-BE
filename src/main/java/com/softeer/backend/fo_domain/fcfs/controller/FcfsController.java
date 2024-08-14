package com.softeer.backend.fo_domain.fcfs.controller;

import com.softeer.backend.fo_domain.fcfs.dto.FcfsPageResponseDto;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsResponseDto;
import com.softeer.backend.fo_domain.fcfs.service.FcfsService;
import com.softeer.backend.global.annotation.AuthInfo;
import com.softeer.backend.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/fcfs")
@Tag(name = "Fcfs Controller", description = "선착순 API")
public class FcfsController {
    private final FcfsService fcfsService;

    @GetMapping
    @ResponseBody
    public ResponseDto<FcfsPageResponseDto> getFcfsPage(@Parameter(hidden = true) HttpServletRequest request) {

        int round = (Integer) request.getAttribute("round");

        FcfsPageResponseDto fcfsPageResponseDto = fcfsService.getFcfsPage(round);

        return ResponseDto.onSuccess(fcfsPageResponseDto);
    }

    @GetMapping("/tutorial")
    @ResponseBody
    public ResponseDto<FcfsPageResponseDto> getFcfsTutorialPage() {

        FcfsPageResponseDto fcfsPageResponseDto = fcfsService.getFcfsTutorialPage();

        return ResponseDto.onSuccess(fcfsPageResponseDto);
    }

    @PostMapping
    public String handleFcfs(@Parameter(hidden = true) HttpServletRequest request,
                             @Parameter(hidden = true) @AuthInfo Integer userId,
                             @RequestParam(value = "answer") String answer,
                             @Parameter(hidden = true) RedirectAttributes redirectAttributes) {

        int round = (Integer) request.getAttribute("round");

//        boolean isFcfsWinner = fcfsService.handleFcfsEvent(userId, round, answer);
//
//        // 리다이렉트 시 쿼리 파라미터를 추가하여 정보 전달
//        redirectAttributes.addAttribute("fcfsWin", isFcfsWinner);

        // GET 요청으로 리다이렉트
        return "redirect:/fcfs/result";
    }

    @GetMapping("/result")
    @ResponseBody
    public ResponseDto<FcfsResponseDto> getFcfsResult(@RequestParam("fcfsWin") Boolean fcfsWin){
        FcfsResponseDto fcfsResponseDto = fcfsService.getFcfsResult(fcfsWin);

        return ResponseDto.onSuccess(fcfsResponseDto);
    }

}
