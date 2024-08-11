package com.softeer.backend.global.staticresources.util;

import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.repository.DrawRepository;
import com.softeer.backend.fo_domain.draw.repository.DrawSettingRepository;
import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsSettingRepository;
import com.softeer.backend.fo_domain.fcfs.service.FcfsSettingManager;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.exception.GeneralException;
import com.softeer.backend.global.staticresources.constant.StaticText;
import com.softeer.backend.global.staticresources.domain.StaticResources;
import com.softeer.backend.global.staticresources.repository.StaticResourcesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class StaticResourcesUtil {
    private final DateTimeFormatter eventTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("a h", Locale.KOREAN);
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    private final StaticResourcesRepository staticResourcesRepository;
    private final DrawSettingRepository drawSettingRepository;
    private final FcfsSettingRepository fcfsSettingRepository;
    private final DrawRepository drawRepository;

    private final Map<String, String> staticResourcesMap = new HashMap<>();

    @PostConstruct
    public void init() {
        loadInitialData();
    }

    public void loadInitialData() {
        List<StaticResources> staticResourcesList = staticResourcesRepository.findAll();

        staticResourcesMap.putAll(
                staticResourcesList.stream()
                        .collect(Collectors.toMap(
                                StaticResources::getFileName,
                                StaticResources::getFileUrl
                        ))
        );

        DrawSetting drawSetting = drawSettingRepository.findAll().get(0);

        List<FcfsSetting> fcfsSettingList = fcfsSettingRepository.findAll();
        FcfsSetting firstFcfsSetting = fcfsSettingList.get(0);
        FcfsSetting secondFcfsSetting = fcfsSettingList.get(1);

        int totalDrawWinner = drawSetting.getWinnerNum1()
                + drawSetting.getWinnerNum2() + drawSetting.getWinnerNum3();
        int remainDrawCount = totalDrawWinner - (int)drawRepository.count();

        Map<String, String> formattedTexts = Arrays.stream(StaticText.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        enumValue -> {
                            switch (enumValue) {
                                case EVENT_PERIOD:

                                    return enumValue.format(drawSetting.getStartDate().format(eventTimeFormatter),
                                            drawSetting.getEndDate().format(eventTimeFormatter));

                                case FCFS_INFO:

                                    return enumValue.format(getKoreanDayOfWeek(firstFcfsSetting.getStartTime().getDayOfWeek()),
                                            getKoreanDayOfWeek(secondFcfsSetting.getStartTime().getDayOfWeek()),
                                            firstFcfsSetting.getStartTime().format(timeFormatter),
                                            firstFcfsSetting.getWinnerNum());
                                case TOTAL_DRAW_WINNER:
                                    return enumValue.format(decimalFormat.format(totalDrawWinner));
                                case REMAIN_DRAW_COUNT:
                                    return enumValue.format(decimalFormat.format(remainDrawCount));

                                default:
                                    return enumValue.getText();
                            }
                        }
                ));

        staticResourcesMap.putAll(formattedTexts);
    }

    private static String getKoreanDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "월";
            case TUESDAY:
                return "화";
            case WEDNESDAY:
                return "수";
            case THURSDAY:
                return "목";
            case FRIDAY:
                return "금";
            case SATURDAY:
                return "토";
            case SUNDAY:
                return "일";
            default:
                log.error("Korean day of week is not supported");
                throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    public String getData(String resourceKey) {
        return staticResourcesMap.get(resourceKey);
    }
}
