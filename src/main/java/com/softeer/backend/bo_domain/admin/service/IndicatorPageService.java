package com.softeer.backend.bo_domain.admin.service;

import com.softeer.backend.bo_domain.admin.dto.indicator.EventIndicatorResponseDto;
import com.softeer.backend.bo_domain.eventparticipation.domain.EventParticipation;
import com.softeer.backend.bo_domain.eventparticipation.repository.EventParticipationRepository;
import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.repository.DrawSettingRepository;
import kotlinx.serialization.Required;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IndicatorPageService {

    private final EventParticipationRepository eventParticipationRepository;
    private final DrawSettingRepository drawSettingRepository;

    public EventIndicatorResponseDto getEventIndicator(){

        DrawSetting drawSetting = drawSettingRepository.findAll().get(0);

        List<EventParticipation> eventParticipationList = eventParticipationRepository.findAllByEventDateBetween(
                drawSetting.getStartDate(), drawSetting.getEndDate()
        );

        return EventIndicatorResponseDto.of(eventParticipationList);
    }

}
