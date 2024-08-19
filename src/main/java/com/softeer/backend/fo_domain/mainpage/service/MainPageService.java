package com.softeer.backend.fo_domain.mainpage.service;

import com.softeer.backend.fo_domain.draw.repository.DrawRepository;
import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsSettingDto;
import com.softeer.backend.fo_domain.fcfs.service.FcfsSettingManager;
import com.softeer.backend.fo_domain.mainpage.dto.MainPageCarResponseDto;
import com.softeer.backend.fo_domain.mainpage.dto.MainPageEventInfoResponseDto;
import com.softeer.backend.fo_domain.mainpage.dto.MainPageEventStaticResponseDto;
import com.softeer.backend.global.common.constant.RedisKeyPrefix;
import com.softeer.backend.global.staticresources.constant.S3FileName;
import com.softeer.backend.global.staticresources.constant.StaticTextName;
import com.softeer.backend.global.staticresources.util.StaticResourceUtil;
import com.softeer.backend.global.util.EventLockRedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainPageService {
    private final DateTimeFormatter eventTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private final DateTimeFormatter fcfsTimeFormatter = DateTimeFormatter.ofPattern("a h", Locale.KOREAN);
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    private final EventLockRedisUtil eventLockRedisUtil;
    private final FcfsSettingManager fcfsSettingManager;
    private final DrawSettingManager drawSettingManager;
    private final DrawRepository drawRepository;
    private final StaticResourceUtil staticResourceUtil;

    @Transactional(readOnly = true)
    @Cacheable(value = "staticResources", key = "'event'")
    public MainPageEventStaticResponseDto getEventPageStatic(){

        Map<String, String> textContentMap = staticResourceUtil.getTextContentMap();
        Map<String, String> s3ContentMap = staticResourceUtil.getS3ContentMap();

        MainPageEventStaticResponseDto.EventInfo fcfsInfo = MainPageEventStaticResponseDto.EventInfo.builder()
                .title(textContentMap.get(StaticTextName.FCFS_TITLE.name()))
                .content(textContentMap.get(StaticTextName.FCFS_CONTENT.name()))
                .rewardImage1(s3ContentMap.get(S3FileName.FCFS_REWARD_IMAGE_1.name()))
                .rewardImage2(s3ContentMap.get(S3FileName.FCFS_REWARD_IMAGE_2.name()))
                .build();

        MainPageEventStaticResponseDto.EventInfo drawInfo = MainPageEventStaticResponseDto.EventInfo.builder()
                .title(textContentMap.get(StaticTextName.DRAW_TITLE.name()))
                .content(textContentMap.get(StaticTextName.DRAW_CONTENT.name()))
                .rewardImage1(s3ContentMap.get(S3FileName.DRAW_REWARD_IMAGE_1.name()))
                .rewardImage2(s3ContentMap.get(S3FileName.DRAW_REWARD_IMAGE_2_3.name()))
                .build();

        return MainPageEventStaticResponseDto.builder()
                .eventTitle(textContentMap.get(StaticTextName.EVENT_TITLE.name()))
                .eventDescription(textContentMap.get(StaticTextName.EVENT_DESCRIPTION.name()))
                .eventInfoList(Arrays.asList(fcfsInfo, drawInfo))
                .build();

    }

    @Transactional(readOnly = true)
    public MainPageEventInfoResponseDto getEventPageInfo(){

        setTotalVisitorsCount();

        Map<String, String> textContentMap = staticResourceUtil.getTextContentMap();

        FcfsSettingDto firstFcfsSetting = fcfsSettingManager.getFcfsSettingByRound(1);
        FcfsSettingDto secondFcfsSetting = fcfsSettingManager.getFcfsSettingByRound(2);

        int totalDrawWinner = drawSettingManager.getWinnerNum1()
                + drawSettingManager.getWinnerNum2() + drawSettingManager.getWinnerNum3();

        int remainDrawCount = totalDrawWinner - (int)drawRepository.count();

        return MainPageEventInfoResponseDto.builder()
                .startDate(drawSettingManager.getStartDate().format(eventTimeFormatter))
                .endDate(drawSettingManager.getEndDate().format(eventTimeFormatter))
                .fcfsInfo(staticResourceUtil.format(textContentMap.get(StaticTextName.FCFS_INFO.name()),
                        staticResourceUtil.getKoreanDayOfWeek(firstFcfsSetting.getStartTime().getDayOfWeek()),
                        staticResourceUtil.getKoreanDayOfWeek(secondFcfsSetting.getStartTime().getDayOfWeek()),
                        firstFcfsSetting.getStartTime().format(fcfsTimeFormatter),
                        firstFcfsSetting.getWinnerNum()))
                .totalDrawWinner(staticResourceUtil.format(
                        textContentMap.get(StaticTextName.TOTAL_DRAW_WINNER.name()), decimalFormat.format(totalDrawWinner)))
                .remainDrawCount(staticResourceUtil.format(
                        textContentMap.get(StaticTextName.REMAIN_DRAW_COUNT.name()), decimalFormat.format(remainDrawCount)))
                .fcfsStartTime(fcfsSettingManager.getNextFcfsTime(LocalDateTime.now()))
                .build();
    }

    // 이벤트 기간이면 redis에 사이트 방문자 수 +1 하기
    public void setTotalVisitorsCount(){

        LocalDate now = LocalDate.now();

        if (!now.isBefore(drawSettingManager.getStartDate()) && !now.isAfter(drawSettingManager.getEndDate())) {
            eventLockRedisUtil.incrementData(RedisKeyPrefix.TOTAL_VISITORS_COUNT_PREFIX.getPrefix());
        }

    }

    @Transactional(readOnly = true)
    @Cacheable(value = "staticResources", key = "'car'")
    public MainPageCarResponseDto getCarPage() {
        Map<String, String> textContentMap = staticResourceUtil.getTextContentMap();
        Map<String, String> s3ContentMap = staticResourceUtil.getS3ContentMap();

        List<MainPageCarResponseDto.CarInfo> carInfoList = List.of(
                createCarInfo(1,
                        StaticTextName.MAIN_TITLE,
                        StaticTextName.MAIN_SUBTITLE,
                        S3FileName.IONIQ_VIDEO,
                        S3FileName.MAIN_BACKGROUND_IMAGE,
                        textContentMap,
                        s3ContentMap),
                createCarInfoWithDetails(2,
                        StaticTextName.INTERIOR_TITLE,
                        StaticTextName.INTERIOR_SUBTITLE,
                        StaticTextName.INTERIOR_IMAGE_TITLE,
                        StaticTextName.INTERIOR_IMAGE_CONTENT,
                        S3FileName.INTERIOR_THUMBNAIL_IMAGE,
                        S3FileName.INTERIOR_BACKGROUND_IMAGE,
                        List.of(
                                createCarDetailInfo(1,
                                        StaticTextName.INTERIOR_OPENNESS_TITLE,
                                        StaticTextName.INTERIOR_OPENNESS_SUBTITLE,
                                        StaticTextName.INTERIOR_OPENNESS_CONTENT,
                                        S3FileName.INTERIOR_OPENNESS_IMAGE,
                                        textContentMap,
                                        s3ContentMap),
                                createCarDetailInfo(2,
                                        StaticTextName.INTERIOR_WELLNESS_TITLE,
                                        StaticTextName.INTERIOR_WELLNESS_SUBTITLE,
                                        StaticTextName.INTERIOR_WELLNESS_CONTENT,
                                        S3FileName.INTERIOR_WELLNESS_IMAGE,
                                        textContentMap,
                                        s3ContentMap)
                        ),
                        textContentMap,
                        s3ContentMap
                ),
                createCarInfoWithDetails(3,
                        StaticTextName.PERFORMANCE_TITLE,
                        StaticTextName.PERFORMANCE_SUBTITLE,
                        StaticTextName.PERFORMANCE_IMAGE_TITLE,
                        StaticTextName.PERFORMANCE_IMAGE_CONTENT,
                        S3FileName.PERFORMANCE_THUMBNAIL_IMAGE,
                        S3FileName.PERFORMANCE_BACKGROUND_IMAGE,
                        List.of(
                                createCarDetailInfo(1,
                                        StaticTextName.PERFORMANCE_BRAKING_TITLE,
                                        StaticTextName.PERFORMANCE_BRAKING_SUBTITLE,
                                        StaticTextName.PERFORMANCE_BRAKING_CONTENT,
                                        S3FileName.PERFORMANCE_BRAKING_IMAGE,
                                        textContentMap,
                                        s3ContentMap),
                                createCarDetailInfo(2,
                                        StaticTextName.PERFORMANCE_DRIVING_TITLE,
                                        StaticTextName.PERFORMANCE_DRIVING_SUBTITLE,
                                        StaticTextName.PERFORMANCE_DRIVING_CONTENT,
                                        S3FileName.PERFORMANCE_DRIVING_IMAGE,
                                        textContentMap,
                                        s3ContentMap)
                        ),
                        textContentMap,
                        s3ContentMap
                ),
                createCarInfoWithDetails(4, StaticTextName.CHARGING_TITLE,
                        StaticTextName.CHARGING_SUBTITLE,
                        StaticTextName.CHARGING_IMAGE_TITLE,
                        StaticTextName.CHARGING_IMAGE_CONTENT,
                        S3FileName.CHARGING_THUMBNAIL_IMAGE,
                        S3FileName.CHARGING_BACKGROUND_IMAGE,
                        List.of(
                                createCarDetailInfo(1,
                                        StaticTextName.CHARGING_FAST_TITLE,
                                        StaticTextName.CHARGING_FAST_SUBTITLE,
                                        StaticTextName.CHARGING_FAST_CONTENT,
                                        S3FileName.CHARGING_FAST_IMAGE,
                                        textContentMap,
                                        s3ContentMap),
                                createCarDetailInfo(2,
                                        StaticTextName.CHARGING_V2L_TITLE,
                                        StaticTextName.CHARGING_V2L_SUBTITLE,
                                        StaticTextName.CHARGING_V2L_CONTENT,
                                        S3FileName.CHARGING_V2L_IMAGE,
                                        textContentMap,
                                        s3ContentMap)
                        ),
                        textContentMap,
                        s3ContentMap
                ),
                createCarInfoWithDetails(5,
                        StaticTextName.SAFE_TITLE,
                        StaticTextName.SAFE_SUBTITLE,
                        StaticTextName.SAFE_IMAGE_TITLE,
                        StaticTextName.SAFE_IMAGE_CONTENT,
                        S3FileName.SAFE_THUMBNAIL_IMAGE,
                        S3FileName.SAFE_BACKGROUND_IMAGE,
                        List.of(
                                createCarDetailInfo(1,
                                        StaticTextName.SAFE_DRIVING_TITLE,
                                        StaticTextName.SAFE_DRIVING_SUBTITLE,
                                        StaticTextName.SAFE_DRIVING_CONTENT,
                                        S3FileName.SAFE_DRIVING_IMAGE,
                                        textContentMap,
                                        s3ContentMap),
                                createCarDetailInfo(2,
                                        StaticTextName.SAFE_ADVANCED_TITLE,
                                        StaticTextName.SAFE_ADVANCED_SUBTITLE,
                                        StaticTextName.SAFE_ADVANCED_CONTENT,
                                        S3FileName.SAFE_ADVANCED_IMAGE,
                                        textContentMap,
                                        s3ContentMap)
                        ),
                        textContentMap,
                        s3ContentMap
                )
        );

        return MainPageCarResponseDto.builder()
                .carInfoList(carInfoList)
                .build();
    }

    private MainPageCarResponseDto.CarInfo createCarInfo(int id,
                                                         StaticTextName titleKey,
                                                         StaticTextName subTitleKey,
                                                         S3FileName imgUrlKey,
                                                         S3FileName backgroundImgUrlKey,
                                                         Map<String, String> textContentMap,
                                                         Map<String, String> s3ContentMap) {
        return MainPageCarResponseDto.CarInfo.builder()
                .id(id)
                .title(textContentMap.get(titleKey.name()))
                .subTitle(textContentMap.get(subTitleKey.name()))
                .imgUrl(s3ContentMap.get(imgUrlKey.name()))
                .backgroundImgUrl(s3ContentMap.get(backgroundImgUrlKey.name()))
                .build();
    }

    private MainPageCarResponseDto.CarDetailInfo createCarDetailInfo(int id,
                                                                     StaticTextName titleKey,
                                                                     StaticTextName subTitleKey,
                                                                     StaticTextName contentKey,
                                                                     S3FileName imgUrlKey,
                                                                     Map<String, String> textContentMap,
                                                                     Map<String, String> s3ContentMap) {
        return MainPageCarResponseDto.CarDetailInfo.builder()
                .id(id)
                .title(textContentMap.get(titleKey.name()))
                .subTitle(textContentMap.get(subTitleKey.name()))
                .content(textContentMap.get(contentKey.name()))
                .imgUrl(s3ContentMap.get(imgUrlKey.name()))
                .build();
    }

    private MainPageCarResponseDto.CarInfo createCarInfoWithDetails(int id,
                                                                    StaticTextName titleKey,
                                                                    StaticTextName subTitleKey,
                                                                    StaticTextName imgTitleKey,
                                                                    StaticTextName imgContentKey,
                                                                    S3FileName imgUrlKey,
                                                                    S3FileName backgroundImgUrlKey,
                                                                    List<MainPageCarResponseDto.CarDetailInfo> carDetailInfoList,
                                                                    Map<String, String> textContentMap,
                                                                    Map<String, String> s3ContentMap) {
        return MainPageCarResponseDto.CarInfo.builder()
                .id(id)
                .title(textContentMap.get(titleKey.name()))
                .subTitle(textContentMap.get(subTitleKey.name()))
                .imgTitle(textContentMap.get(imgTitleKey.name()))
                .imgContent(textContentMap.get(imgContentKey.name()))
                .imgUrl(s3ContentMap.get(imgUrlKey.name()))
                .backgroundImgUrl(s3ContentMap.get(backgroundImgUrlKey.name()))
                .carDetailInfoList(carDetailInfoList)
                .build();
    }
}
