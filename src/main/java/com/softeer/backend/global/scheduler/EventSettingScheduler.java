package com.softeer.backend.global.scheduler;

import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.repository.DrawSettingRepository;
import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsSettingRepository;
import com.softeer.backend.fo_domain.fcfs.service.FcfsSettingManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor
public class EventSettingScheduler {

    private final ThreadPoolTaskScheduler taskScheduler;

    private final FcfsSettingManager fcfsSettingManager;
    private final DrawSettingManager drawSettingManager;
    private final FcfsSettingRepository fcfsSettingRepository;
    private final DrawSettingRepository drawSettingRepository;

    private ScheduledFuture<?> scheduledFuture;

    @PostConstruct
    public void init() {
        scheduleTask();

    }

    public void scheduleTask() {
        scheduledFuture = taskScheduler.schedule(this::updateEventSetting, new CronTrigger("0 0 1 * * *"));
    }

    @Transactional(readOnly = true)
    protected void updateEventSetting() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(LocalDateTime.of(drawSettingManager.getStartDate(), drawSettingManager.getStartTime()))
        || now.isAfter(LocalDateTime.of(drawSettingManager.getEndDate(), drawSettingManager.getEndTime()))){

            List<FcfsSetting> fcfsSettings = fcfsSettingRepository.findAll();
            DrawSetting drawSetting = drawSettingRepository.findAll().get(0);

            fcfsSettingManager.setFcfsSettingList(fcfsSettings);
            drawSettingManager.setDrawSetting(drawSetting);
        }
    }

}
