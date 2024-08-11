package com.softeer.backend.bo_domain.admin.service;

import com.softeer.backend.bo_domain.admin.dto.main.MainPageResponseDto;
import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.repository.DrawSettingRepository;
import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMainPageService {

    private final FcfsSettingRepository fcfsSettingRepository;
    private final DrawSettingRepository drawSettingRepository;

    @Transactional(readOnly = true)
    public MainPageResponseDto getMainPage() {
        List<FcfsSetting> fcfsSettingList = fcfsSettingRepository.findAll(Sort.by(Sort.Order.asc("round")));
        List<DrawSetting> drawSetting = drawSettingRepository.findAll();

        return MainPageResponseDto.of(fcfsSettingList, drawSetting);
    }
}
