package com.softeer.backend.bo_domain.admin.service;

import com.softeer.backend.bo_domain.admin.dto.event.EventPageResponseDto;
import com.softeer.backend.bo_domain.admin.dto.winner.*;
import com.softeer.backend.bo_domain.admin.exception.AdminException;
import com.softeer.backend.fo_domain.draw.domain.Draw;
import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.repository.DrawRepository;
import com.softeer.backend.fo_domain.draw.repository.DrawSettingRepository;
import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.fcfs.domain.Fcfs;
import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsRepository;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsSettingRepository;
import com.softeer.backend.fo_domain.fcfs.service.FcfsSettingManager;
import com.softeer.backend.fo_domain.user.repository.UserRepository;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WinnerPageService {

    private final FcfsRepository fcfsRepository;
    private final DrawRepository drawRepository;
    private final FcfsSettingRepository fcfsSettingRepository;
    private final DrawSettingRepository drawSettingRepository;
    private final FcfsSettingManager fcfsSettingManager;
    private final DrawSettingManager drawSettingManager;

    @Transactional(readOnly = true)
    public WinnerPageResponseDto getWinnerPage() {

        return WinnerPageResponseDto.of(fcfsSettingManager, drawSettingManager);
    }

    @Transactional(readOnly = true)
    public FcfsWinnerListResponseDto getFcfsWinnerList(int round) {
        List<Fcfs> fcfsList = fcfsRepository.findFcfsWithUser(round);

        return FcfsWinnerListResponseDto.of(fcfsList, round);
    }

    @Transactional(readOnly = true)
    public DrawWinnerListResponseDto getDrawWinnerList(int rank) {
        List<Draw> drawList = drawRepository.findDrawWithUser(rank);

        return DrawWinnerListResponseDto.of(drawList, rank);
    }

    @Transactional
    public void updateFcfsWinnerNum(FcfsWinnerUpdateRequestDto fcfsWinnerUpdateRequestDto) {
        List<FcfsSetting> fcfsSettingList = fcfsSettingRepository.findAll();

        fcfsSettingList.forEach((fcfsSetting) -> fcfsSetting.setWinnerNum(fcfsWinnerUpdateRequestDto.getFcfsWinnerNum()));

        fcfsSettingManager.setFcfsWinnerNum(fcfsWinnerUpdateRequestDto.getFcfsWinnerNum());
    }

    @Transactional
    public void updateDrawWinnerNum(DrawWinnerUpdateRequestDto drawWinnerUpdateRequestDto) {
        DrawSetting drawSetting = drawSettingRepository.findAll().get(0);

        drawSetting.setWinnerNum1(drawWinnerUpdateRequestDto.getFirstWinnerNum());
        drawSetting.setWinnerNum2(drawWinnerUpdateRequestDto.getSecondWinnerNum());
        drawSetting.setWinnerNum3(drawWinnerUpdateRequestDto.getThirdWinnerNum());

        drawSettingManager.setDrawWinnerNum(drawWinnerUpdateRequestDto.getFirstWinnerNum(),
                drawWinnerUpdateRequestDto.getSecondWinnerNum(),
                drawWinnerUpdateRequestDto.getThirdWinnerNum());
    }
}
