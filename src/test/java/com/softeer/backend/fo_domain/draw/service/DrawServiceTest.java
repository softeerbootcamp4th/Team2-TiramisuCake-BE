package com.softeer.backend.fo_domain.draw.service;

import com.softeer.backend.fo_domain.draw.domain.Draw;
import com.softeer.backend.fo_domain.draw.domain.DrawParticipationInfo;
import com.softeer.backend.fo_domain.draw.dto.history.DrawHistoryDto;
import com.softeer.backend.fo_domain.draw.dto.main.DrawMainFullAttendResponseDto;
import com.softeer.backend.fo_domain.draw.dto.main.DrawMainResponseDto;
import com.softeer.backend.fo_domain.draw.dto.modal.WinModal;
import com.softeer.backend.fo_domain.draw.dto.participate.DrawLoseModalResponseDto;
import com.softeer.backend.fo_domain.draw.dto.participate.DrawModalResponseDto;
import com.softeer.backend.fo_domain.draw.dto.participate.DrawWinModalResponseDto;
import com.softeer.backend.fo_domain.draw.dto.history.DrawHistoryLoserResponseDto;
import com.softeer.backend.fo_domain.draw.dto.history.DrawHistoryResponseDto;
import com.softeer.backend.fo_domain.draw.dto.history.DrawHistoryWinnerResponseDto;
import com.softeer.backend.fo_domain.draw.exception.DrawException;
import com.softeer.backend.fo_domain.draw.repository.DrawParticipationInfoRepository;
import com.softeer.backend.fo_domain.draw.repository.DrawRepository;
import com.softeer.backend.fo_domain.draw.util.*;
import com.softeer.backend.fo_domain.share.domain.ShareInfo;
import com.softeer.backend.fo_domain.share.repository.ShareInfoRepository;
import com.softeer.backend.global.util.DrawRedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;


@Transactional
@ExtendWith(MockitoExtension.class)
class DrawServiceTest {
    @Mock
    private DrawParticipationInfoRepository drawParticipationInfoRepository;
    @Mock
    private ShareInfoRepository shareInfoRepository;
    @Mock
    private DrawRedisUtil drawRedisUtil;
    @Mock
    private DrawUtil drawUtil;
    @Mock
    private DrawResponseGenerateUtil drawResponseGenerateUtil;
    @Mock
    private DrawModalGenerateUtil drawModalGenerateUtil;
    @Mock
    private DrawAttendanceCountUtil drawAttendanceCountUtil;
    @Mock
    private DrawRemainDrawCountUtil drawRemainDrawCountUtil;
    @Mock
    private DrawSettingManager drawSettingManager;
    @Mock
    DrawRepository drawRepository;

    @InjectMocks
    private DrawService drawService;

    @BeforeEach
    @DisplayName("getDrawMainPageInfo를 위한 초기화")
    void setUpGetDrawMainPageInfo() {
        WinModal fullAttendModal = WinModal.builder()
                .title("7일 연속 출석하셨네요!")
                .subtitle("등록된 번호로 스타벅스 기프티콘을 보내드려요.")
                .img("https://d1wv99asbppzjv.cloudfront.net/draw-page/7th_complete_image.svg")
                .description("본 이벤트는 The new IONIQ 5 출시 이벤트 기간 내 한 회선당 1회만 참여 가능합니다.\n" +
                        "이벤트 경품 수령을 위해 등록된 전화번호로 영업일 기준 3~5일 내 개별 안내가 진행될 예정입니다.\n" +
                        "이벤트 당첨 이후 개인정보 제공을 거부하거나 개별 안내를 거부하는 경우, 당첨이 취소될 수 있습니다.\n")
                .build();

        Mockito.lenient().when(drawModalGenerateUtil.generateWinModal(7)).thenReturn(fullAttendModal);

        DrawMainFullAttendResponseDto drawMainFullAttendResponseDto = DrawMainFullAttendResponseDto
                .builder()
                .invitedNum(3)
                .remainDrawCount(1)
                .drawAttendanceCount(7)
                .fullAttendModal(fullAttendModal)
                .build();

        Mockito.lenient().when(drawResponseGenerateUtil.generateMainFullAttendResponse(3, 1, 7)).thenReturn(drawMainFullAttendResponseDto);

        DrawMainResponseDto drawMainNotAttendResponseDto = DrawMainResponseDto
                .builder()
                .invitedNum(3)
                .remainDrawCount(1)
                .drawAttendanceCount(1)
                .build();

        Mockito.lenient().when(drawResponseGenerateUtil.generateMainNotAttendResponse(3, 1, 1)).thenReturn(drawMainNotAttendResponseDto);
    }

    @Test
    @DisplayName("7일 연속출석자의 추첨 이벤트 페이지 접속")
    void getDrawMainPageFullAttend() {
        // given
        Integer userId = 6;

        WinModal fullAttendModal = WinModal.builder()
                .title("7일 연속 출석하셨네요!")
                .subtitle("등록된 번호로 스타벅스 기프티콘을 보내드려요.")
                .img("https://d1wv99asbppzjv.cloudfront.net/draw-page/7th_complete_image.svg")
                .description("본 이벤트는 The new IONIQ 5 출시 이벤트 기간 내 한 회선당 1회만 참여 가능합니다.\n" +
                        "이벤트 경품 수령을 위해 등록된 전화번호로 영업일 기준 3~5일 내 개별 안내가 진행될 예정입니다.\n" +
                        "이벤트 당첨 이후 개인정보 제공을 거부하거나 개별 안내를 거부하는 경우, 당첨이 취소될 수 있습니다.\n")
                .build();

        DrawParticipationInfo drawParticipationInfo = DrawParticipationInfo.builder()
                .userId(userId)
                .drawWinningCount(10)
                .drawLosingCount(10)
                .drawAttendanceCount(7)
                .lastAttendance(LocalDateTime.parse("2024-08-23 15:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        Clock fixedClock = Clock.fixed(LocalDateTime.of(2024, 8, 23, 15, 30, 0)
                .atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        LocalDateTime now = LocalDateTime.now(fixedClock);

        when(drawParticipationInfoRepository.findDrawParticipationInfoByUserId(userId))
                .thenReturn(Optional.ofNullable(drawParticipationInfo));

        ShareInfo shareInfo = ShareInfo.builder()
                .userId(userId)
                .invitedNum(3)
                .remainDrawCount(1)
                .build();

        when(shareInfoRepository.findShareInfoByUserId(userId))
                .thenReturn(Optional.ofNullable(shareInfo));

        lenient().when(drawAttendanceCountUtil.handleAttendanceCount(userId, drawParticipationInfo)).thenReturn(7);

        lenient().when(drawRemainDrawCountUtil.handleRemainDrawCount(userId, 1, drawParticipationInfo))
                .thenReturn(1);

        DrawMainFullAttendResponseDto expectedResponse = DrawMainFullAttendResponseDto.builder()
                .invitedNum(3)
                .remainDrawCount(1)
                .drawAttendanceCount(7)
                .fullAttendModal(fullAttendModal)
                .build();

        lenient().when(drawResponseGenerateUtil.generateMainFullAttendResponse(3, 3, 7 % 8))
                .thenReturn(expectedResponse);

        // when
        DrawMainResponseDto actualResponse = drawService.getDrawMainPageInfo(userId);

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getInvitedNum()).isEqualTo(expectedResponse.getInvitedNum());
        assertThat(actualResponse.getRemainDrawCount()).isEqualTo(expectedResponse.getRemainDrawCount());
        assertThat(actualResponse.getDrawAttendanceCount()).isEqualTo(expectedResponse.getDrawAttendanceCount());

        DrawMainFullAttendResponseDto actualFullAttendResponse = (DrawMainFullAttendResponseDto) actualResponse;

        assertThat(actualFullAttendResponse.getFullAttendModal().getTitle()).isEqualTo(expectedResponse.getFullAttendModal().getTitle());
        assertThat(actualFullAttendResponse.getFullAttendModal().getSubtitle()).isEqualTo(expectedResponse.getFullAttendModal().getSubtitle());
        assertThat(actualFullAttendResponse.getFullAttendModal().getImg()).isEqualTo(expectedResponse.getFullAttendModal().getImg());
        assertThat(actualFullAttendResponse.getFullAttendModal().getDescription()).isEqualTo(expectedResponse.getFullAttendModal().getDescription());
    }

    @Test
    @DisplayName("1일 출석자의 추첨 페이지 접속")
    void getDrawMainPageNotAttend() {
        // given
        Integer userId = 6;

        DrawParticipationInfo drawParticipationInfo = DrawParticipationInfo.builder()
                .userId(6)
                .drawWinningCount(10)
                .drawLosingCount(10)
                .drawAttendanceCount(1)
                .build();

        when(drawParticipationInfoRepository.findDrawParticipationInfoByUserId(userId))
                .thenReturn(Optional.ofNullable(drawParticipationInfo));

        ShareInfo shareInfo = ShareInfo.builder()
                .userId(userId)
                .invitedNum(3)
                .remainDrawCount(1)
                .build();

        when(shareInfoRepository.findShareInfoByUserId(userId))
                .thenReturn(Optional.ofNullable(shareInfo));

        when(drawAttendanceCountUtil.handleAttendanceCount(userId, drawParticipationInfo)).thenReturn(1);

        lenient().when(drawRemainDrawCountUtil.handleRemainDrawCount(userId, shareInfo.getRemainDrawCount(), drawParticipationInfo))
                .thenReturn(1);

        DrawMainResponseDto expectedResponse = DrawMainResponseDto.builder()
                .invitedNum(3)
                .remainDrawCount(1)
                .drawAttendanceCount(1)
                .build();

        when(drawResponseGenerateUtil.generateMainNotAttendResponse(3, 1, 1)).thenReturn(expectedResponse);

        // when
        DrawMainResponseDto actualResponse = drawService.getDrawMainPageInfo(userId);

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getInvitedNum()).isEqualTo(expectedResponse.getInvitedNum());
        assertThat(actualResponse.getRemainDrawCount()).isEqualTo(expectedResponse.getRemainDrawCount());
        assertThat(actualResponse.getDrawAttendanceCount()).isEqualTo(expectedResponse.getDrawAttendanceCount());
    }

    @Test
    @DisplayName("남은 기회 0회인 사용자의 추첨 참여")
    void participateDrawEventZero() {
        // given
        Integer userId = 6;

        ShareInfo shareInfo = ShareInfo.builder()
                .userId(userId)
                .invitedNum(3)
                .remainDrawCount(0)
                .build();

        when(shareInfoRepository.findShareInfoByUserId(userId)).thenReturn(Optional.ofNullable(shareInfo));

        // When & Then
        assertThatThrownBy(() -> drawService.participateDrawEvent(userId))
                .isInstanceOf(DrawException.class);
    }

    @Test
    @DisplayName("이미 하루 중 당첨된 적이 있는 사용자의 추첨 참여")
    void participateDrawEventTwice() {
        // given
        Integer userId = 6;

        ShareInfo shareInfo = ShareInfo.builder()
                .userId(userId)
                .invitedNum(3)
                .remainDrawCount(2)
                .build();

        when(shareInfoRepository.findShareInfoByUserId(userId)).thenReturn(Optional.ofNullable(shareInfo));

        ArrayList<String> images = new ArrayList<>();
        images.add("left");
        images.add("left");
        images.add("right");

        DrawLoseModalResponseDto drawLoseModalResponseDto = DrawLoseModalResponseDto.builder()
                .isDrawWin(false)
                .images(images)
                .shareUrl("https://softeer.site/share/of8w")
                .build();

        when(drawResponseGenerateUtil.generateDrawLoserResponse(userId)).thenReturn(drawLoseModalResponseDto);

        when(drawRedisUtil.getRankingIfWinner(userId)).thenReturn(3);

        // when
        DrawModalResponseDto actualResponse = drawService.participateDrawEvent(userId);

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.isDrawWin()).isEqualTo(false);
        assertThat(((DrawLoseModalResponseDto) actualResponse).getShareUrl()).isEqualTo("https://softeer.site/share/of8w");
        assertThat(actualResponse.getImages().get(0)).isEqualTo("left");
        assertThat(actualResponse.getImages().get(1)).isEqualTo("left");
        assertThat(actualResponse.getImages().get(2)).isEqualTo("right");
    }

    @Test
    @DisplayName("낙첨자의 응답 반환")
    void participateDrawEventLoser() {
        // given
        Integer userId = 6;

        ShareInfo shareInfo = ShareInfo.builder()
                .userId(userId)
                .invitedNum(3)
                .remainDrawCount(2)
                .build();

        when(shareInfoRepository.findShareInfoByUserId(userId)).thenReturn(Optional.ofNullable(shareInfo));

        when(drawRedisUtil.getRankingIfWinner(userId)).thenReturn(0);

        when(drawUtil.isDrawWin()).thenReturn(false);

        ArrayList<String> images = new ArrayList<>();
        images.add("left");
        images.add("left");
        images.add("right");

        DrawLoseModalResponseDto drawLoseModalResponseDto = DrawLoseModalResponseDto.builder()
                .isDrawWin(false)
                .images(images)
                .shareUrl("https://softeer.site/share/of8w")
                .build();

        when(drawResponseGenerateUtil.generateDrawLoserResponse(userId)).thenReturn(drawLoseModalResponseDto);

        // when
        DrawModalResponseDto actualResponse = drawService.participateDrawEvent(userId);

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.isDrawWin()).isEqualTo(false);
        assertThat(((DrawLoseModalResponseDto) actualResponse).getShareUrl()).isEqualTo("https://softeer.site/share/of8w");
        assertThat(actualResponse.getImages().get(0)).isEqualTo("left");
        assertThat(actualResponse.getImages().get(1)).isEqualTo("left");
        assertThat(actualResponse.getImages().get(2)).isEqualTo("right");
    }

    @Test
    @DisplayName("추첨 1등 응답 반환")
    void participateDrawEventFirst() {
        // given
        Integer userId = 6;

        ShareInfo shareInfo = ShareInfo.builder()
                .userId(userId)
                .invitedNum(3)
                .remainDrawCount(2)
                .build();

        when(shareInfoRepository.findShareInfoByUserId(userId)).thenReturn(Optional.ofNullable(shareInfo));

        when(drawRedisUtil.getRankingIfWinner(userId)).thenReturn(0);

        when(drawSettingManager.getWinnerNum1()).thenReturn(1);
        when(drawSettingManager.getWinnerNum2()).thenReturn(10);
        when(drawSettingManager.getWinnerNum3()).thenReturn(100);

        when(drawUtil.isDrawWin()).thenReturn(true);
        when(drawUtil.getRanking()).thenReturn(1);

        when(drawRedisUtil.isWinner(userId, 1, 1)).thenReturn(true);

        ArrayList<String> images = new ArrayList<>();
        images.add("up");
        images.add("up");
        images.add("up");

        WinModal winModal = WinModal.builder()
                .title("축하합니다!")
                .subtitle("아이패드에 당첨됐어요!")
                .img("https://d1wv99asbppzjv.cloudfront.net/main-page/draw_reward_image_1.svg")
                .description("이벤트 경품 수령을 위해 등록된 전화번호로 영업일 기준 3~5일 내 개별 안내가 진행될 예정입니다.\n" +
                        "이벤트 당첨 이후 개인정보 제공을 거부하거나 개별 안내를 거부하는 경우, 당첨이 취소될 수 있습니다.")
                .build();

        DrawWinModalResponseDto drawWinModalResponseDto = DrawWinModalResponseDto.builder()
                .isDrawWin(true)
                .images(images)
                .winModal(winModal)
                .build();

        lenient().when(drawResponseGenerateUtil.generateDrawWinnerResponse(1)).thenReturn(drawWinModalResponseDto);

        // when
        DrawModalResponseDto actualResponse = drawService.participateDrawEvent(userId);

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.isDrawWin()).isEqualTo(true);
        assertThat(actualResponse.getImages().get(0)).isEqualTo("up");
        assertThat(actualResponse.getImages().get(1)).isEqualTo("up");
        assertThat(actualResponse.getImages().get(2)).isEqualTo("up");
        assertThat(((DrawWinModalResponseDto) actualResponse).getWinModal().getTitle()).isEqualTo("축하합니다!");
        assertThat(((DrawWinModalResponseDto) actualResponse).getWinModal().getSubtitle()).isEqualTo("아이패드에 당첨됐어요!");
        assertThat(((DrawWinModalResponseDto) actualResponse).getWinModal().getImg()).isEqualTo("https://d1wv99asbppzjv.cloudfront.net/main-page/draw_reward_image_1.svg");
        assertThat(((DrawWinModalResponseDto) actualResponse).getWinModal().getDescription()).isEqualTo("이벤트 경품 수령을 위해 등록된 전화번호로 영업일 기준 3~5일 내 개별 안내가 진행될 예정입니다.\n" +
                "이벤트 당첨 이후 개인정보 제공을 거부하거나 개별 안내를 거부하는 경우, 당첨이 취소될 수 있습니다.");
    }

    @Test
    @DisplayName("추첨 2등 응답 반환")
    void participateDrawEventSecond() {
        // given
        Integer userId = 6;

        ShareInfo shareInfo = ShareInfo.builder()
                .userId(userId)
                .invitedNum(3)
                .remainDrawCount(2)
                .build();

        when(shareInfoRepository.findShareInfoByUserId(userId)).thenReturn(Optional.ofNullable(shareInfo));

        when(drawRedisUtil.getRankingIfWinner(userId)).thenReturn(0);

        when(drawSettingManager.getWinnerNum1()).thenReturn(1);
        when(drawSettingManager.getWinnerNum2()).thenReturn(10);
        when(drawSettingManager.getWinnerNum3()).thenReturn(100);

        when(drawUtil.isDrawWin()).thenReturn(true);
        when(drawUtil.getRanking()).thenReturn(2);

        when(drawRedisUtil.isWinner(userId, 2, 10)).thenReturn(true);

        ArrayList<String> images = new ArrayList<>();
        images.add("up");
        images.add("up");
        images.add("up");

        WinModal winModal = WinModal.builder()
                .title("축하합니다!")
                .subtitle("현대백화점 쿠폰 10만원퀀에 당첨됐어요!")
                .img("https://d1wv99asbppzjv.cloudfront.net/main-page/draw_reward_image_2.svg")
                .description("이벤트 경품 수령을 위해 등록된 전화번호로 영업일 기준 3~5일 내 개별 안내가 진행될 예정입니다.\n" +
                        "이벤트 당첨 이후 개인정보 제공을 거부하거나 개별 안내를 거부하는 경우, 당첨이 취소될 수 있습니다.")
                .build();

        DrawWinModalResponseDto drawWinModalResponseDto = DrawWinModalResponseDto.builder()
                .isDrawWin(true)
                .images(images)
                .winModal(winModal)
                .build();

        when(drawResponseGenerateUtil.generateDrawWinnerResponse(2)).thenReturn(drawWinModalResponseDto);

        // when
        DrawModalResponseDto actualResponse = drawService.participateDrawEvent(userId);

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.isDrawWin()).isEqualTo(true);
        assertThat(actualResponse.getImages().get(0)).isEqualTo("up");
        assertThat(actualResponse.getImages().get(1)).isEqualTo("up");
        assertThat(actualResponse.getImages().get(2)).isEqualTo("up");
        assertThat(((DrawWinModalResponseDto) actualResponse).getWinModal().getTitle()).isEqualTo("축하합니다!");
        assertThat(((DrawWinModalResponseDto) actualResponse).getWinModal().getSubtitle()).isEqualTo("현대백화점 쿠폰 10만원퀀에 당첨됐어요!");
        assertThat(((DrawWinModalResponseDto) actualResponse).getWinModal().getImg()).isEqualTo("https://d1wv99asbppzjv.cloudfront.net/main-page/draw_reward_image_2.svg");
        assertThat(((DrawWinModalResponseDto) actualResponse).getWinModal().getDescription()).isEqualTo("이벤트 경품 수령을 위해 등록된 전화번호로 영업일 기준 3~5일 내 개별 안내가 진행될 예정입니다.\n" +
                "이벤트 당첨 이후 개인정보 제공을 거부하거나 개별 안내를 거부하는 경우, 당첨이 취소될 수 있습니다.");
    }

    @Test
    @DisplayName("추첨 3등 응답 반환")
    void participateDrawEventThird() {
        // given
        Integer userId = 6;

        ShareInfo shareInfo = ShareInfo.builder()
                .userId(userId)
                .invitedNum(3)
                .remainDrawCount(2)
                .build();

        when(shareInfoRepository.findShareInfoByUserId(userId)).thenReturn(Optional.ofNullable(shareInfo));

        when(drawRedisUtil.getRankingIfWinner(userId)).thenReturn(0);

        when(drawSettingManager.getWinnerNum1()).thenReturn(1);
        when(drawSettingManager.getWinnerNum2()).thenReturn(10);
        when(drawSettingManager.getWinnerNum3()).thenReturn(100);

        when(drawUtil.isDrawWin()).thenReturn(true);
        when(drawUtil.getRanking()).thenReturn(3);

        when(drawRedisUtil.isWinner(userId, 3, 100)).thenReturn(true);

        ArrayList<String> images = new ArrayList<>();
        images.add("up");
        images.add("up");
        images.add("up");

        WinModal winModal = WinModal.builder()
                .title("축하합니다!")
                .subtitle("현대백화점 쿠폰 1만원퀀에 당첨됐어요!")
                .img("https://d1wv99asbppzjv.cloudfront.net/main-page/draw_reward_image_3.svg")
                .description("이벤트 경품 수령을 위해 등록된 전화번호로 영업일 기준 3~5일 내 개별 안내가 진행될 예정입니다.\n" +
                        "이벤트 당첨 이후 개인정보 제공을 거부하거나 개별 안내를 거부하는 경우, 당첨이 취소될 수 있습니다.")
                .build();

        DrawWinModalResponseDto drawWinModalResponseDto = DrawWinModalResponseDto.builder()
                .isDrawWin(true)
                .images(images)
                .winModal(winModal)
                .build();

        when(drawResponseGenerateUtil.generateDrawWinnerResponse(3)).thenReturn(drawWinModalResponseDto);

        // when
        DrawModalResponseDto actualResponse = drawService.participateDrawEvent(userId);

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.isDrawWin()).isEqualTo(true);
        assertThat(actualResponse.getImages().get(0)).isEqualTo("up");
        assertThat(actualResponse.getImages().get(1)).isEqualTo("up");
        assertThat(actualResponse.getImages().get(2)).isEqualTo("up");
        assertThat(((DrawWinModalResponseDto) actualResponse).getWinModal().getTitle()).isEqualTo("축하합니다!");
        assertThat(((DrawWinModalResponseDto) actualResponse).getWinModal().getSubtitle()).isEqualTo("현대백화점 쿠폰 1만원퀀에 당첨됐어요!");
        assertThat(((DrawWinModalResponseDto) actualResponse).getWinModal().getImg()).isEqualTo("https://d1wv99asbppzjv.cloudfront.net/main-page/draw_reward_image_3.svg");
        assertThat(((DrawWinModalResponseDto) actualResponse).getWinModal().getDescription()).isEqualTo("이벤트 경품 수령을 위해 등록된 전화번호로 영업일 기준 3~5일 내 개별 안내가 진행될 예정입니다.\n" +
                "이벤트 당첨 이후 개인정보 제공을 거부하거나 개별 안내를 거부하는 경우, 당첨이 취소될 수 있습니다.");
    }

    @Test
    @DisplayName("로직상 당첨이어도 레디스에 자리가 없는 경우 실패 응답 반환")
    void participateDrawEventWithoutSeat() {
        // given
        Integer userId = 6;

        ShareInfo shareInfo = ShareInfo.builder()
                .userId(userId)
                .invitedNum(3)
                .remainDrawCount(2)
                .build();

        when(shareInfoRepository.findShareInfoByUserId(userId)).thenReturn(Optional.ofNullable(shareInfo));

        when(drawRedisUtil.getRankingIfWinner(userId)).thenReturn(0);

        when(drawSettingManager.getWinnerNum1()).thenReturn(1);
        when(drawSettingManager.getWinnerNum2()).thenReturn(10);
        when(drawSettingManager.getWinnerNum3()).thenReturn(100);

        when(drawUtil.isDrawWin()).thenReturn(true);
        when(drawUtil.getRanking()).thenReturn(1);

        when(drawRedisUtil.isWinner(userId, 1, 1)).thenReturn(false);

        ArrayList<String> images = new ArrayList<>();
        images.add("up");
        images.add("up");
        images.add("down");

        DrawLoseModalResponseDto expectedResponse = DrawLoseModalResponseDto.builder()
                .isDrawWin(false)
                .images(images)
                .shareUrl("https://softeer.shop/share/of8w")
                .build();

        when(drawResponseGenerateUtil.generateDrawLoserResponse(userId)).thenReturn(expectedResponse);

        // when
        DrawModalResponseDto actualResponse = drawService.participateDrawEvent(userId);

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.isDrawWin()).isEqualTo(false);
        assertThat(actualResponse.getImages().get(0)).isEqualTo("up");
        assertThat(actualResponse.getImages().get(1)).isEqualTo("up");
        assertThat(actualResponse.getImages().get(2)).isEqualTo("down");
        assertThat(((DrawLoseModalResponseDto) actualResponse).getShareUrl()).isEqualTo("https://softeer.shop/share/of8w");
    }

    @BeforeEach
    @DisplayName("getDrawHistory를 위한 초기화")
    void setUpGetDrawHistory() {
        lenient().when(drawService.getDrawHistory(6)).thenReturn(DrawHistoryLoserResponseDto.builder()
                .isDrawWin(false)
                .shareUrl("https://softeer.shop/share/of8w")
                .build());
    }

    @Test
    @DisplayName("getDrawHistory - 사용자가 당첨자인 경우")
    void testGetDrawHistory_WhenUserIsWinner() {
        // Given
        Integer userId = 1;
        List<Draw> drawList;
        int redisRank = 1; // Mock된 redis 순위 (1, 2, 3 중 하나로 설정)
        List<DrawHistoryDto> drawHistoryList;

        drawList = Arrays.asList(
                Draw.builder().rank(2).winningDate(LocalDate.of(2022, 1, 1)).build(),
                Draw.builder().rank(3).winningDate(LocalDate.of(2022, 2, 1)).build()
        );

        drawHistoryList = Arrays.asList(
                DrawHistoryDto.builder().drawRank(2).winningDate(LocalDate.of(2022, 1, 1)).image("url1").build(),
                DrawHistoryDto.builder().drawRank(3).winningDate(LocalDate.of(2022, 2, 1)).image("url2").build(),
                DrawHistoryDto.builder().drawRank(redisRank).winningDate(LocalDate.now()).image("url3").build()
        );

        when(drawRedisUtil.getRankingIfWinner(userId)).thenReturn(redisRank);
        when(drawRepository.findAllByUserIdOrderByWinningDateAsc(userId)).thenReturn(drawList);
        when(drawResponseGenerateUtil.getImageUrl(anyInt())).thenReturn("url1", "url2", "url3");
        when(drawResponseGenerateUtil.generateDrawHistoryWinnerResponse(anyList())).thenReturn(
                DrawHistoryWinnerResponseDto.builder()
                        .isDrawWin(true)
                        .historyList(drawHistoryList)
                        .build()
        );

        // When
        DrawHistoryResponseDto response = drawService.getDrawHistory(userId);

        // Then
        assertThat((DrawHistoryWinnerResponseDto) response).isNotNull();
        assertThat(((DrawHistoryWinnerResponseDto) response).getHistoryList()).hasSize(3);

        assertThat(((DrawHistoryWinnerResponseDto) response).getHistoryList().get(0).getDrawRank()).isEqualTo(2);
        assertThat(((DrawHistoryWinnerResponseDto) response).getHistoryList().get(1).getDrawRank()).isEqualTo(3);
        assertThat(((DrawHistoryWinnerResponseDto) response).getHistoryList().get(2).getDrawRank()).isEqualTo(redisRank);
    }

    @Test
    @DisplayName("당첨자가 아닐 경우 당첨 내역 조회 시 낙첨 결과")
    void getDrawHistoryLoser() {
        // given
        Integer userId = 6;

        when(drawRedisUtil.getRankingIfWinner(userId)).thenReturn(0);
        when(drawRepository.findAllByUserIdOrderByWinningDateAsc(userId)).thenReturn(new ArrayList<>());

        DrawHistoryLoserResponseDto expectedResponse = DrawHistoryLoserResponseDto.builder()
                .isDrawWin(false)
                .shareUrl("https://softeer.shop/share/of8w")
                .build();

        // when
        DrawHistoryResponseDto actualResponse = drawService.getDrawHistory(userId);

        // then
        assertThat(actualResponse).isNotEqualTo(null);
        assertThat(actualResponse.isDrawWin()).isEqualTo(false);
        assertThat(((DrawHistoryLoserResponseDto) actualResponse).getShareUrl()).isEqualTo(expectedResponse.getShareUrl());
    }
}