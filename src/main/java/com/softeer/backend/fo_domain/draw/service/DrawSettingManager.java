package com.softeer.backend.fo_domain.draw.service;

import com.softeer.backend.fo_domain.draw.repository.DrawSettingRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Getter
@Component
@RequiredArgsConstructor
public class DrawSettingManager {
    private final DrawSettingRepository drawSettingRepository;

    private Date startTime;
    private Date endTime;
    private int winnerNum1;
    private int winnerNum2;
    private int winnerNum3;


}
