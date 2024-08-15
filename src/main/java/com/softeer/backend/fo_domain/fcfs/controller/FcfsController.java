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

        String fcfsCode = fcfsService.handleFcfsEvent(userId, round, answer);

        if(fcfsCode != null){
            request.getSession().setAttribute("fcfsCode", fcfsCode);

            redirectAttributes.addAttribute("fcfsWin", true);
        }
        else{
            redirectAttributes.addAttribute("fcfsWin", false);
        }

        return "redirect:/fcfs/result";
    }

    @GetMapping("/result")
    @ResponseBody
    public ResponseDto<FcfsResponseDto> getFcfsResult(@Parameter(hidden = true) HttpServletRequest request,
                                                      @RequestParam("fcfsWin") Boolean fcfsWin){

        String fcfsCode = (String) request.getSession().getAttribute("fcfsCode");

        FcfsResponseDto fcfsResponseDto = fcfsService.getFcfsResult(fcfsWin, fcfsCode);

        return ResponseDto.onSuccess(fcfsResponseDto);
    }

}
