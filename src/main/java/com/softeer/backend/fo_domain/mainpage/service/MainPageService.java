package com.softeer.backend.fo_domain.mainpage.service;

import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.mainpage.dto.MainPageCarResponseDto;
import com.softeer.backend.fo_domain.mainpage.dto.MainPageEventResponseDto;
import com.softeer.backend.global.common.constant.RedisKeyPrefix;
import com.softeer.backend.global.staticresources.util.StaticResourcesUtil;
import com.softeer.backend.global.util.EventLockRedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MainPageService {
    private final DateTimeFormatter eventTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private final EventLockRedisUtil eventLockRedisUtil;
    private final StaticResourcesUtil staticResourcesUtil;
    private final DrawSettingManager drawSettingManager;

    public MainPageEventResponseDto getEventPage(){

        setTotalVisitorsCount();

        MainPageEventResponseDto.EventInfo fcfsInfo = MainPageEventResponseDto.EventInfo.builder()
                .title(staticResourcesUtil.getData("FCFS_TITLE"))
                .content(staticResourcesUtil.getData("FCFS_CONTENT"))
                .rewardImage1(staticResourcesUtil.getData("fcfs_reward_image_1"))
                .rewardImage2(staticResourcesUtil.getData("fcfs_reward_image_2"))
                .build();

        MainPageEventResponseDto.EventInfo drawInfo = MainPageEventResponseDto.EventInfo.builder()
                .title(staticResourcesUtil.getData("DRAW_TITLE"))
                .content(staticResourcesUtil.getData("DRAW_CONTENT"))
                .rewardImage1(staticResourcesUtil.getData("draw_reward_image_1"))
                .rewardImage2(staticResourcesUtil.getData("draw_reward_image_2_3"))
                .build();

        return MainPageEventResponseDto.builder()
                .startDate(drawSettingManager.getStartDate().format(eventTimeFormatter))
                .endDate(drawSettingManager.getEndDate().format(eventTimeFormatter))
                .eventTitle(staticResourcesUtil.getData("EVENT_TITLE"))
                .eventDescription(staticResourcesUtil.getData("EVENT_DESCRIPTION"))
                .fcfsInfo(staticResourcesUtil.getData("FCFS_INFO"))
                .totalDrawWinner(staticResourcesUtil.getData("TOTAL_DRAW_WINNER"))
                .remainDrawCount(staticResourcesUtil.getData("REMAIN_DRAW_COUNT"))
                .eventInfoList(Arrays.asList(fcfsInfo, drawInfo))
                .build();

    }

    // 이벤트 기간이면 redis에 사이트 방문자 수 +1 하기
    private void setTotalVisitorsCount(){

        LocalDate now = LocalDate.now();

        if (!now.isBefore(drawSettingManager.getStartDate()) && !now.isAfter(drawSettingManager.getEndDate())) {
            eventLockRedisUtil.incrementData(RedisKeyPrefix.TOTAL_VISITORS_COUNT_PREFIX.getPrefix());
        }

    }

    public MainPageCarResponseDto getCarPage(){

        MainPageCarResponseDto.CarInfo carInfo1 = MainPageCarResponseDto.CarInfo.builder()
                .id(1)
                .title(staticResourcesUtil.getData("MAIN_TITLE"))
                .subTitle(staticResourcesUtil.getData("MAIN_SUBTITLE"))
                .imgUrl(staticResourcesUtil.getData("ioniq_video"))
                .backgroundImgUrl(staticResourcesUtil.getData("main_background_image"))
                .build();

        MainPageCarResponseDto.CarDetailInfo carDetailInfo2_1 = MainPageCarResponseDto.CarDetailInfo.builder()
                .id(1)
                .title(staticResourcesUtil.getData("INTERIOR_OPENNESS_TITLE"))
                .subTitle(staticResourcesUtil.getData("INTERIOR_OPENNESS_SUBTITLE"))
                .content(staticResourcesUtil.getData("INTERIOR_OPENNESS_CONTENT"))
                .imgUrl(staticResourcesUtil.getData("interior_openness_image"))
                .build();

        MainPageCarResponseDto.CarDetailInfo carDetailInfo2_2 = MainPageCarResponseDto.CarDetailInfo.builder()
                .id(2)
                .title(staticResourcesUtil.getData("INTERIOR_WELLNESS_TITLE"))
                .subTitle(staticResourcesUtil.getData("INTERIOR_WELLNESS_SUBTITLE"))
                .content(staticResourcesUtil.getData("INTERIOR_WELLNESS_CONTENT"))
                .imgUrl(staticResourcesUtil.getData("interior_wellness_image"))
                .build();

        MainPageCarResponseDto.CarInfo carInfo2 = MainPageCarResponseDto.CarInfo.builder()
                .id(2)
                .title(staticResourcesUtil.getData("INTERIOR_TITLE"))
                .subTitle(staticResourcesUtil.getData("INTERIOR_SUBTITLE"))
                .imgTitle(staticResourcesUtil.getData("INTERIOR_IMAGE_TITLE"))
                .imgContent(staticResourcesUtil.getData("INTERIOR_IMAGE_CONTENT"))
                .imgUrl(staticResourcesUtil.getData("interior_thumbnail_image"))
                .backgroundImgUrl(staticResourcesUtil.getData("interior_background_image"))
                .carDetailInfoList(Arrays.asList(carDetailInfo2_1, carDetailInfo2_2))
                .build();

        MainPageCarResponseDto.CarDetailInfo carDetailInfo3_1 = MainPageCarResponseDto.CarDetailInfo.builder()
                .id(1)
                .title(staticResourcesUtil.getData("PERFORMANCE_BRAKING_TITLE"))
                .subTitle(staticResourcesUtil.getData("PERFORMANCE_BRAKING_SUBTITLE"))
                .content(staticResourcesUtil.getData("PERFORMANCE_BRAKING_CONTENT"))
                .imgUrl(staticResourcesUtil.getData("performance_braking_image"))
                .build();

        MainPageCarResponseDto.CarDetailInfo carDetailInfo3_2 = MainPageCarResponseDto.CarDetailInfo.builder()
                .id(2)
                .title(staticResourcesUtil.getData("PERFORMANCE_DRIVING_TITLE"))
                .subTitle(staticResourcesUtil.getData("PERFORMANCE_DRIVING_SUBTITLE"))
                .content(staticResourcesUtil.getData("PERFORMANCE_DRIVING_CONTENT"))
                .imgUrl(staticResourcesUtil.getData("performance_driving_image"))
                .build();

        MainPageCarResponseDto.CarInfo carInfo3 = MainPageCarResponseDto.CarInfo.builder()
                .id(3)
                .title(staticResourcesUtil.getData("PERFORMANCE_TITLE"))
                .subTitle(staticResourcesUtil.getData("PERFORMANCE_SUBTITLE"))
                .imgTitle(staticResourcesUtil.getData("PERFORMANCE_IMAGE_TITLE"))
                .imgContent(staticResourcesUtil.getData("PERFORMANCE_IMAGE_CONTENT"))
                .imgUrl(staticResourcesUtil.getData("performance_thumbnail_image"))
                .backgroundImgUrl(staticResourcesUtil.getData("performance_background_image"))
                .carDetailInfoList(Arrays.asList(carDetailInfo3_1, carDetailInfo3_2))
                .build();

        MainPageCarResponseDto.CarDetailInfo carDetailInfo4_1 = MainPageCarResponseDto.CarDetailInfo.builder()
                .id(1)
                .title(staticResourcesUtil.getData("CHARGING_FAST_TITLE"))
                .subTitle(staticResourcesUtil.getData("CHARGING_FAST_SUBTITLE"))
                .content(staticResourcesUtil.getData("CHARGING_FAST_CONTENT"))
                .imgUrl(staticResourcesUtil.getData("charging_fast_image"))
                .build();

        MainPageCarResponseDto.CarDetailInfo carDetailInfo4_2 = MainPageCarResponseDto.CarDetailInfo.builder()
                .id(2)
                .title(staticResourcesUtil.getData("CHARGING_V2L_TITLE"))
                .subTitle(staticResourcesUtil.getData("CHARGING_V2L_SUBTITLE"))
                .content(staticResourcesUtil.getData("CHARGING_V2L_CONTENT"))
                .imgUrl(staticResourcesUtil.getData("charging_v2l_image"))
                .build();

        MainPageCarResponseDto.CarInfo carInfo4 = MainPageCarResponseDto.CarInfo.builder()
                .id(4)
                .title(staticResourcesUtil.getData("CHARGING_TITLE"))
                .subTitle(staticResourcesUtil.getData("CHARGING_SUBTITLE"))
                .imgTitle(staticResourcesUtil.getData("CHARGING_IMAGE_TITLE"))
                .imgContent(staticResourcesUtil.getData("CHARGING_IMAGE_CONTENT"))
                .imgUrl(staticResourcesUtil.getData("charging_thumbnail_image"))
                .backgroundImgUrl(staticResourcesUtil.getData("charging_background_image"))
                .carDetailInfoList(Arrays.asList(carDetailInfo4_1, carDetailInfo4_2))
                .build();

        MainPageCarResponseDto.CarDetailInfo carDetailInfo5_1 = MainPageCarResponseDto.CarDetailInfo.builder()
                .id(1)
                .title(staticResourcesUtil.getData("SAFE_DRIVING_TITLE"))
                .subTitle(staticResourcesUtil.getData("SAFE_DRIVING_SUBTITLE"))
                .content(staticResourcesUtil.getData("SAFE_DRIVING_CONTENT"))
                .imgUrl(staticResourcesUtil.getData("safe_driving_image"))
                .build();

        MainPageCarResponseDto.CarDetailInfo carDetailInfo5_2 = MainPageCarResponseDto.CarDetailInfo.builder()
                .id(2)
                .title(staticResourcesUtil.getData("SAFE_ADVANCED_TITLE"))
                .subTitle(staticResourcesUtil.getData("SAFE_ADVANCED_SUBTITLE"))
                .content(staticResourcesUtil.getData("SAFE_ADVANCED_CONTENT"))
                .imgUrl(staticResourcesUtil.getData("safe_advanced_image"))
                .build();

        MainPageCarResponseDto.CarInfo carInfo5 = MainPageCarResponseDto.CarInfo.builder()
                .id(5)
                .title(staticResourcesUtil.getData("SAFE_TITLE"))
                .subTitle(staticResourcesUtil.getData("SAFE_SUBTITLE"))
                .imgTitle(staticResourcesUtil.getData("SAFE_IMAGE_TITLE"))
                .imgContent(staticResourcesUtil.getData("SAFE_IMAGE_CONTENT"))
                .imgUrl(staticResourcesUtil.getData("safe_thumbnail_image"))
                .backgroundImgUrl(staticResourcesUtil.getData("safe_background_image"))
                .carDetailInfoList(Arrays.asList(carDetailInfo5_1, carDetailInfo5_2))
                .build();

        return MainPageCarResponseDto.builder()
                .carInfoList(Arrays.asList(carInfo1, carInfo2, carInfo3, carInfo4, carInfo5))
                .build();
    }
}
