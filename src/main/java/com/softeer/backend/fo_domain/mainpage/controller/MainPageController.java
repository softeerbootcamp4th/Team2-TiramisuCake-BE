package com.softeer.backend.fo_domain.mainpage.controller;

import com.softeer.backend.fo_domain.mainpage.dto.MainPageCarResponseDto;
import com.softeer.backend.fo_domain.mainpage.dto.MainPageEventInfoResponseDto;
import com.softeer.backend.fo_domain.mainpage.dto.MainPageEventStaticResponseDto;
import com.softeer.backend.fo_domain.mainpage.service.MainPageService;
import com.softeer.backend.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
public class MainPageController {

    private final MainPageService mainPageService;

    @GetMapping("/event/static")
    public ResponseEntity<ResponseDto<MainPageEventStaticResponseDto>> getEventPageStatic(){
        MainPageEventStaticResponseDto mainPageEventStaticResponseDto= mainPageService.getEventPageStatic();

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic()) // 1일 동안 public 캐싱
                .body(ResponseDto.onSuccess(mainPageEventStaticResponseDto));
    }

    @GetMapping("/event/info")
    public ResponseDto<MainPageEventInfoResponseDto> getEventPageInfo(){

        MainPageEventInfoResponseDto mainPageEventInfoResponseDto = mainPageService.getEventPageInfo();

        return ResponseDto.onSuccess(mainPageEventInfoResponseDto);
    }

    @GetMapping("/car")
    public ResponseEntity<ResponseDto<MainPageCarResponseDto>> getCarPage(){

        MainPageCarResponseDto mainPageCarResponseDto = mainPageService.getCarPage();

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic()) // 1일 동안 public 캐싱
                .body(ResponseDto.onSuccess(mainPageCarResponseDto));
    }

}
