package com.softeer.backend.fo_domain.mainpage.service;

import com.softeer.backend.fo_domain.draw.repository.DrawRepository;
import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsSettingDto;
import com.softeer.backend.fo_domain.fcfs.service.FcfsSettingManager;
import com.softeer.backend.fo_domain.fcfs.service.QuizManager;
import com.softeer.backend.fo_domain.mainpage.dto.MainPageCarResponseDto;
import com.softeer.backend.fo_domain.mainpage.dto.MainPageEventInfoResponseDto;
import com.softeer.backend.fo_domain.mainpage.dto.MainPageEventStaticResponseDto;
import com.softeer.backend.global.common.constant.RedisKeyPrefix;
import com.softeer.backend.global.staticresources.constant.S3FileName;
import com.softeer.backend.global.staticresources.constant.StaticTextName;
import com.softeer.backend.global.staticresources.util.StaticResourceUtil;
import com.softeer.backend.global.util.EventLockRedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MainPageServiceTest {
    @InjectMocks
    private MainPageService mainPageService;

    @Mock
    private EventLockRedisUtil eventLockRedisUtil;

    @Mock
    private FcfsSettingManager fcfsSettingManager;

    @Mock
    private DrawSettingManager drawSettingManager;

    @Mock
    private QuizManager quizManager;

    @Mock
    private DrawRepository drawRepository;

    @Mock
    private StaticResourceUtil staticResourceUtil;

    @Test
    @DisplayName("메인 페이지의 정적 정보를 올바르게 반환한다.")
    void testGetEventPageStatic() {
        // Given
        Map<String, String> textContentMap = new HashMap<>();
        textContentMap.put(StaticTextName.FCFS_TITLE.name(), "FCFS 제목");
        textContentMap.put(StaticTextName.FCFS_CONTENT.name(), "FCFS 내용");
        textContentMap.put(StaticTextName.DRAW_TITLE.name(), "추첨 제목");
        textContentMap.put(StaticTextName.DRAW_CONTENT.name(), "추첨 내용");
        textContentMap.put(StaticTextName.EVENT_TITLE.name(), "이벤트 제목");
        textContentMap.put(StaticTextName.EVENT_DESCRIPTION.name(), "이벤트 설명");

        Map<String, String> s3ContentMap = new HashMap<>();
        s3ContentMap.put(S3FileName.FCFS_REWARD_IMAGE_1.name(), "fcfs_reward_image_1.jpg");
        s3ContentMap.put(S3FileName.FCFS_REWARD_IMAGE_2.name(), "fcfs_reward_image_2.jpg");
        s3ContentMap.put(S3FileName.DRAW_REWARD_IMAGE_1.name(), "draw_reward_image_1.jpg");
        s3ContentMap.put(S3FileName.DRAW_REWARD_IMAGE_2.name(), "draw_reward_image_2.jpg");
        s3ContentMap.put(S3FileName.DRAW_REWARD_IMAGE_3.name(), "draw_reward_image_3.jpg");

        when(staticResourceUtil.getTextContentMap()).thenReturn(textContentMap);
        when(staticResourceUtil.getS3ContentMap()).thenReturn(s3ContentMap);

        // When
        MainPageEventStaticResponseDto response = mainPageService.getEventPageStatic();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEventTitle()).isEqualTo("이벤트 제목");
        assertThat(response.getEventDescription()).isEqualTo("이벤트 설명");

        List<MainPageEventStaticResponseDto.EventInfo> eventInfoList = response.getEventInfoList();
        assertThat(eventInfoList).hasSize(2);

        MainPageEventStaticResponseDto.EventInfo fcfsInfo = eventInfoList.get(0);
        assertThat(fcfsInfo.getTitle()).isEqualTo("FCFS 제목");
        assertThat(fcfsInfo.getContent()).isEqualTo("FCFS 내용");
        assertThat(fcfsInfo.getRewardImage1()).isEqualTo("fcfs_reward_image_1.jpg");
        assertThat(fcfsInfo.getRewardImage2()).isEqualTo("fcfs_reward_image_2.jpg");

        MainPageEventStaticResponseDto.EventInfo drawInfo = eventInfoList.get(1);
        assertThat(drawInfo.getTitle()).isEqualTo("추첨 제목");
        assertThat(drawInfo.getContent()).isEqualTo("추첨 내용");
        assertThat(drawInfo.getRewardImage1()).isEqualTo("draw_reward_image_1.jpg");
        assertThat(drawInfo.getRewardImage2()).isEqualTo("draw_reward_image_2.jpg");
        assertThat(drawInfo.getRewardImage3()).isEqualTo("draw_reward_image_3.jpg");
    }

    @Test
    @DisplayName("메인 페이지의 이벤트 정보를 올바르게 반환한다.")
    void testGetEventPageInfo() {
        // Given
        Map<String, String> textContentMap = new HashMap<>();

        when(staticResourceUtil.getTextContentMap()).thenReturn(textContentMap);

        FcfsSettingDto firstFcfsSetting = new FcfsSettingDto();
        firstFcfsSetting.setStartTime(LocalDateTime.of(2024, 8, 21, 10, 0));
        firstFcfsSetting.setWinnerNum(5);

        FcfsSettingDto secondFcfsSetting = new FcfsSettingDto();
        secondFcfsSetting.setStartTime(LocalDateTime.of(2024, 8, 22, 11, 0));

        when(fcfsSettingManager.getFcfsSettingByRound(1)).thenReturn(firstFcfsSetting);
        when(fcfsSettingManager.getFcfsSettingByRound(2)).thenReturn(secondFcfsSetting);

        when(drawSettingManager.getWinnerNum1()).thenReturn(10);
        when(drawSettingManager.getWinnerNum2()).thenReturn(20);
        when(drawSettingManager.getWinnerNum3()).thenReturn(30);
        when(drawRepository.count()).thenReturn(15L);
        when(drawSettingManager.getStartDate()).thenReturn(LocalDate.of(2024, 8, 1));
        when(drawSettingManager.getEndDate()).thenReturn(LocalDate.of(2024, 8, 31));
        when(quizManager.getHint()).thenReturn("퀴즈 힌트");
        when(fcfsSettingManager.getNowOrNextFcfsTime(any())).thenReturn(LocalDateTime.of(2024, 8, 22, 11, 0));

        // When
        MainPageEventInfoResponseDto response = mainPageService.getEventPageInfo();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStartDate()).isEqualTo("2024.08.01");
        assertThat(response.getEndDate()).isEqualTo("2024.08.31");
        assertThat(response.getFcfsHint()).isEqualTo("퀴즈 힌트");
        assertThat(response.getNextFcfsStartTime()).isEqualTo(LocalDateTime.of(2024, 8, 22, 11, 0));
    }

    @Test
    @DisplayName("메인 페이지의 자동차 정보를 올바르게 반환한다.")
    void testGetCarPage() {
        // Given
        Map<String, String> textContentMap = new HashMap<>();
        textContentMap.put(StaticTextName.MAIN_TITLE.name(), "자동차 메인 제목");
        textContentMap.put(StaticTextName.MAIN_SUBTITLE.name(), "자동차 메인 부제목");
        textContentMap.put(StaticTextName.INTERIOR_TITLE.name(), "인테리어 제목");
        textContentMap.put(StaticTextName.INTERIOR_SUBTITLE.name(), "인테리어 부제목");
        textContentMap.put(StaticTextName.PERFORMANCE_TITLE.name(), "성능 제목");
        textContentMap.put(StaticTextName.PERFORMANCE_SUBTITLE.name(), "성능 부제목");
        textContentMap.put(StaticTextName.CHARGING_TITLE.name(), "충전 제목");
        textContentMap.put(StaticTextName.CHARGING_SUBTITLE.name(), "충전 부제목");
        textContentMap.put(StaticTextName.SAFE_TITLE.name(), "안전 제목");
        textContentMap.put(StaticTextName.SAFE_SUBTITLE.name(), "안전 부제목");

        Map<String, String> s3ContentMap = new HashMap<>();
        s3ContentMap.put(S3FileName.IONIQ_VIDEO.name(), "ioniq_video.mp4");
        s3ContentMap.put(S3FileName.MAIN_BACKGROUND_IMAGE.name(), "main_background.jpg");
        s3ContentMap.put(S3FileName.INTERIOR_THUMBNAIL_IMAGE.name(), "interior_thumbnail.jpg");
        s3ContentMap.put(S3FileName.INTERIOR_BACKGROUND_IMAGE.name(), "interior_background.jpg");
        s3ContentMap.put(S3FileName.PERFORMANCE_THUMBNAIL_IMAGE.name(), "performance_thumbnail.jpg");
        s3ContentMap.put(S3FileName.PERFORMANCE_BACKGROUND_IMAGE.name(), "performance_background.jpg");
        s3ContentMap.put(S3FileName.CHARGING_THUMBNAIL_IMAGE.name(), "charging_thumbnail.jpg");
        s3ContentMap.put(S3FileName.CHARGING_BACKGROUND_IMAGE.name(), "charging_background.jpg");
        s3ContentMap.put(S3FileName.SAFE_THUMBNAIL_IMAGE.name(), "safe_thumbnail.jpg");
        s3ContentMap.put(S3FileName.SAFE_BACKGROUND_IMAGE.name(), "safe_background.jpg");

        when(staticResourceUtil.getTextContentMap()).thenReturn(textContentMap);
        when(staticResourceUtil.getS3ContentMap()).thenReturn(s3ContentMap);

        // When
        MainPageCarResponseDto response = mainPageService.getCarPage();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCarInfoList()).hasSize(5);

        MainPageCarResponseDto.CarInfo mainCarInfo = response.getCarInfoList().get(0);
        assertThat(mainCarInfo.getTitle()).isEqualTo("자동차 메인 제목");
        assertThat(mainCarInfo.getSubTitle()).isEqualTo("자동차 메인 부제목");
        assertThat(mainCarInfo.getImgUrl()).isEqualTo("ioniq_video.mp4");
        assertThat(mainCarInfo.getBackgroundImgUrl()).isEqualTo("main_background.jpg");
    }
}
