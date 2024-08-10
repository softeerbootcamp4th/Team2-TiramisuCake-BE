package com.softeer.backend.bo_domain.admin.controller;

import com.softeer.backend.bo_domain.admin.dto.main.MainPageResponseDto;
import com.softeer.backend.bo_domain.admin.service.MainPageService;
import com.softeer.backend.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class MainPageController {
    private final MainPageService mainPageService;

    @GetMapping("/main")
    public ResponseDto<MainPageResponseDto> getMainPage() {
        MainPageResponseDto mainPageResponseDto = mainPageService.getMainPage();

        return ResponseDto.onSuccess(mainPageResponseDto);
    }
}
