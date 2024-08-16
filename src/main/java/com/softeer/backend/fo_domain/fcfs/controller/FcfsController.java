package com.softeer.backend.fo_domain.fcfs.controller;

import com.softeer.backend.fo_domain.fcfs.dto.FcfsPageResponseDto;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsRequestDto;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsResult;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsResultResponseDto;
import com.softeer.backend.fo_domain.fcfs.service.FcfsService;
import com.softeer.backend.global.annotation.AuthInfo;
import com.softeer.backend.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/fcfs")
@Tag(name = "Fcfs Controller", description = "선착순 API")
public class FcfsController {
    private final FcfsService fcfsService;

    @GetMapping
    public ResponseDto<FcfsPageResponseDto> getFcfsPage(@Parameter(hidden = true) HttpServletRequest request) {

        int round = (Integer) request.getAttribute("round");

        FcfsPageResponseDto fcfsPageResponseDto = fcfsService.getFcfsPage(round);

        return ResponseDto.onSuccess(fcfsPageResponseDto);
    }

    @GetMapping("/tutorial")
    public ResponseDto<FcfsPageResponseDto> getFcfsTutorialPage() {

        FcfsPageResponseDto fcfsPageResponseDto = fcfsService.getFcfsTutorialPage();

        return ResponseDto.onSuccess(fcfsPageResponseDto);
    }

    @PostMapping
    public ResponseEntity<Void> handleFcfs(@Parameter(hidden = true) HttpServletRequest request,
                                     @Parameter(hidden = true) @AuthInfo Integer userId,
                                     @RequestBody FcfsRequestDto fcfsRequestDto) {

        int round = (Integer) request.getAttribute("round");

        String fcfsCode = fcfsService.handleFcfsEvent(userId, round, fcfsRequestDto);

        log.info("fcfsCode in handleFcfs : {}", fcfsCode);

        HttpHeaders headers = new HttpHeaders();
        String redirectUrl = "/fcfs/result";

        if(fcfsCode != null){
            request.getSession().setAttribute("fcfsCode", fcfsCode);
            redirectUrl += "?fcfsWin=true";
        }
        else{
            redirectUrl += "?fcfsWin=false";
        }

        headers.setLocation(URI.create(redirectUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/result")
    public ResponseDto<FcfsResultResponseDto> getFcfsResult(@Parameter(hidden = true) HttpServletRequest request,
                                                            @RequestParam("fcfsWin") Boolean fcfsWin){

        String fcfsCode = (String) request.getSession().getAttribute("fcfsCode");
        log.info("fcfsCode in getFcfsResult : {}", fcfsCode);
        request.getSession().removeAttribute("fcfsCode");

        FcfsResultResponseDto fcfsResultResponseDto = fcfsService.getFcfsResult(fcfsWin, fcfsCode);

        return ResponseDto.onSuccess(fcfsResultResponseDto);
    }

}
