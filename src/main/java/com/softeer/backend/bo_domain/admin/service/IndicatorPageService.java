package com.softeer.backend.bo_domain.admin.service;

import com.softeer.backend.bo_domain.admin.dto.indicator.EventIndicatorResponseDto;
import com.softeer.backend.bo_domain.eventparticipation.domain.EventParticipation;
import com.softeer.backend.bo_domain.eventparticipation.repository.EventParticipationRepository;
import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.repository.DrawSettingRepository;
import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import kotlinx.serialization.Required;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IndicatorPageService {

    private final EventParticipationRepository eventParticipationRepository;
    private final DrawSettingManager drawSettingManager;

    public EventIndicatorResponseDto getEventIndicator() {


        List<EventParticipation> eventParticipationList = eventParticipationRepository.findAllByEventDateBetween(
                drawSettingManager.getStartDate(), drawSettingManager.getEndDate()
        );

        return EventIndicatorResponseDto.of(eventParticipationList);
    }

}
