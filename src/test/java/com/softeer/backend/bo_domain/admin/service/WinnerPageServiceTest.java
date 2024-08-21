package com.softeer.backend.bo_domain.admin.service;

import com.softeer.backend.bo_domain.admin.dto.winner.*;
import com.softeer.backend.bo_domain.admin.service.WinnerPageService;
import com.softeer.backend.fo_domain.draw.domain.Draw;
import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.repository.DrawRepository;
import com.softeer.backend.fo_domain.draw.repository.DrawSettingRepository;
import com.softeer.backend.fo_domain.fcfs.domain.Fcfs;
import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsRepository;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsSettingRepository;
import com.softeer.backend.fo_domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WinnerPageServiceTest {

    @Mock
    private FcfsRepository fcfsRepository;

    @Mock
    private DrawRepository drawRepository;

    @Mock
    private FcfsSettingRepository fcfsSettingRepository;

    @Mock
    private DrawSettingRepository drawSettingRepository;

    @InjectMocks
    private WinnerPageService winnerPageService;

    @Test
    @DisplayName("당첨 관리 페이지 정보를 반환한다.")
    void getWinnerPage() {
        // Given
        List<FcfsSetting> fcfsSettings = Arrays.asList(
                FcfsSetting.builder().round(1).startTime(LocalDateTime.of(LocalDate.now(), LocalTime.now())).winnerNum(10).build(),
                FcfsSetting.builder().round(2).startTime(LocalDateTime.of(LocalDate.now(), LocalTime.now())).winnerNum(20).build()
        );

        DrawSetting drawSetting = DrawSetting.builder()
                .winnerNum1(5)
                .winnerNum2(10)
                .winnerNum3(15)
                .build();

        when(fcfsSettingRepository.findAll()).thenReturn(fcfsSettings);
        when(drawSettingRepository.findAll()).thenReturn(Collections.singletonList(drawSetting));

        // When
        WinnerPageResponseDto response = winnerPageService.getWinnerPage();

        // Then
        assertThat(response.getFcfsEventList()).hasSize(2);
        assertThat(response.getDrawEventList()).hasSize(3);
        assertThat(response.getDrawEventList().get(0).getWinnerNum()).isEqualTo(5);
        assertThat(response.getDrawEventList().get(1).getWinnerNum()).isEqualTo(10);
        assertThat(response.getDrawEventList().get(2).getWinnerNum()).isEqualTo(15);

        verify(fcfsSettingRepository).findAll();
        verify(drawSettingRepository).findAll();
    }

    @Test
    @DisplayName("선착순 당첨자 목록을 반환한다.")
    void getFcfsWinnerList() {
        // Given
        int round = 1;
        List<Fcfs> fcfsList = Arrays.asList(
                Fcfs.builder().user(User.builder().name("Alice").phoneNumber("010-1234-5678").build()).build(),
                Fcfs.builder().user(User.builder().name("Bob").phoneNumber("010-2345-6789").build()).build()
        );

        when(fcfsRepository.findFcfsWithUser(round)).thenReturn(fcfsList);

        // When
        FcfsWinnerListResponseDto response = winnerPageService.getFcfsWinnerList(round);

        // Then
        assertThat(response.getFcfsWinnerList()).hasSize(2);
        assertThat(response.getFcfsWinnerList().get(0).getName()).isEqualTo("Alice");
        assertThat(response.getFcfsWinnerList().get(1).getName()).isEqualTo("Bob");

        verify(fcfsRepository).findFcfsWithUser(round);
    }

    @Test
    @DisplayName("추첨 당첨자 목록을 반환한다.")
    void getDrawWinnerList() {
        // Given
        int rank = 1;
        List<Draw> drawList = Arrays.asList(
                Draw.builder().user(User.builder().name("Charlie").phoneNumber("010-3456-7890").build()).build(),
                Draw.builder().user(User.builder().name("David").phoneNumber("010-4567-8901").build()).build()
        );

        when(drawRepository.findDrawWithUser(rank)).thenReturn(drawList);

        // When
        DrawWinnerListResponseDto response = winnerPageService.getDrawWinnerList(rank);

        // Then
        assertThat(response.getDrawWinnerList()).hasSize(2);
        assertThat(response.getDrawWinnerList().get(0).getName()).isEqualTo("Charlie");
        assertThat(response.getDrawWinnerList().get(1).getName()).isEqualTo("David");

        verify(drawRepository).findDrawWithUser(rank);
    }

    @Test
    @DisplayName("선착순 당첨자 수를 수정한다.")
    void updateFcfsWinnerNum() {
        // Given
        FcfsWinnerUpdateRequestDto requestDto = new FcfsWinnerUpdateRequestDto(50);
        List<FcfsSetting> fcfsSettings = Arrays.asList(
                FcfsSetting.builder().round(1).winnerNum(10).build(),
                FcfsSetting.builder().round(2).winnerNum(20).build()
        );

        when(fcfsSettingRepository.findAll()).thenReturn(fcfsSettings);

        // When
        winnerPageService.updateFcfsWinnerNum(requestDto);

        // Then
        assertThat(fcfsSettings.get(0).getWinnerNum()).isEqualTo(50);
        assertThat(fcfsSettings.get(1).getWinnerNum()).isEqualTo(50);

        verify(fcfsSettingRepository).findAll();
    }

    @Test
    @DisplayName("추첨 당첨자 수를 수정한다.")
    void updateDrawWinnerNum() {
        // Given
        DrawWinnerUpdateRequestDto requestDto = new DrawWinnerUpdateRequestDto(5, 10, 15);
        DrawSetting drawSetting = DrawSetting.builder()
                .winnerNum1(1)
                .winnerNum2(2)
                .winnerNum3(3)
                .build();

        when(drawSettingRepository.findAll()).thenReturn(Collections.singletonList(drawSetting));

        // When
        winnerPageService.updateDrawWinnerNum(requestDto);

        // Then
        assertThat(drawSetting.getWinnerNum1()).isEqualTo(5);
        assertThat(drawSetting.getWinnerNum2()).isEqualTo(10);
        assertThat(drawSetting.getWinnerNum3()).isEqualTo(15);

        verify(drawSettingRepository).findAll();
    }
}