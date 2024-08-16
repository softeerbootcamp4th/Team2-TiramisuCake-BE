package com.softeer.backend.fo_domain.mainpage.controller;

import com.softeer.backend.fo_domain.mainpage.dto.MainPageCarResponseDto;
import com.softeer.backend.fo_domain.mainpage.dto.MainPageEventResponseDto;
import com.softeer.backend.fo_domain.mainpage.service.MainPageService;
import com.softeer.backend.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
public class MainPageController {

    private final MainPageService mainPageService;

    @GetMapping("/event")
    public ResponseDto<MainPageEventResponseDto> getEventPage(){
        MainPageEventResponseDto mainPageEventResponseDto = mainPageService.getEventPage();

        return ResponseDto.onSuccess(mainPageEventResponseDto);
    }

    @GetMapping("/car")
    public ResponseDto<MainPageCarResponseDto> getCarPage(){

        MainPageCarResponseDto mainPageCarResponseDto = mainPageService.getCarPage();

        return ResponseDto.onSuccess(mainPageCarResponseDto);
    }

}
