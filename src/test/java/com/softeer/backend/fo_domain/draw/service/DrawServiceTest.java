package com.softeer.backend.fo_domain.draw.service;

import com.softeer.backend.fo_domain.draw.domain.DrawParticipationInfo;
import com.softeer.backend.fo_domain.draw.dto.main.DrawMainFullAttendResponseDto;
import com.softeer.backend.fo_domain.draw.dto.main.DrawMainResponseDto;
import com.softeer.backend.fo_domain.draw.dto.modal.WinModal;
import com.softeer.backend.fo_domain.draw.dto.participate.DrawLoseModalResponseDto;
import com.softeer.backend.fo_domain.draw.dto.participate.DrawModalResponseDto;
import com.softeer.backend.fo_domain.draw.dto.participate.DrawWinModalResponseDto;
import com.softeer.backend.fo_domain.draw.dto.result.DrawHistoryLoserResponseDto;
import com.softeer.backend.fo_domain.draw.dto.result.DrawHistoryResponseDto;
import com.softeer.backend.fo_domain.draw.dto.result.DrawHistoryWinnerResponseDto;
import com.softeer.backend.fo_domain.draw.repository.DrawParticipationInfoRepository;
import com.softeer.backend.fo_domain.draw.util.DrawAttendanceCountUtil;
import com.softeer.backend.fo_domain.draw.util.DrawModalGenerateUtil;
import com.softeer.backend.fo_domain.draw.util.DrawResponseGenerateUtil;
import com.softeer.backend.fo_domain.draw.util.DrawUtil;
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
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


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
    private DrawSettingManager drawSettingManager;

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
                .drawParticipationCount(7)
                .fullAttendModal(fullAttendModal)
                .build();

        Mockito.lenient().when(drawResponseGenerateUtil.generateMainFullAttendResponse(3, 1, 7)).thenReturn(drawMainFullAttendResponseDto);

        DrawMainResponseDto drawMainNotAttendResponseDto = DrawMainResponseDto
                .builder()
                .invitedNum(3)
                .remainDrawCount(1)
                .drawParticipationCount(1)
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
                .userId(6)
                .drawWinningCount(10)
                .drawLosingCount(10)
                .drawAttendanceCount(7)
                .build();

        Mockito.when(drawParticipationInfoRepository.findDrawParticipationInfoByUserId(userId)).thenReturn(Optional.ofNullable(drawParticipationInfo));

        ShareInfo shareInfo = ShareInfo.builder()
                .userId(userId)
                .invitedNum(3)
                .remainDrawCount(1)
                .build();

        Mockito.when(shareInfoRepository.findShareInfoByUserId(userId)).thenReturn(Optional.ofNullable(shareInfo));

        Mockito.when(drawAttendanceCountUtil.handleAttendanceCount(userId, drawParticipationInfo)).thenReturn(7);

        DrawMainFullAttendResponseDto expectedResponse = DrawMainFullAttendResponseDto
                .builder()
                .invitedNum(3)
                .remainDrawCount(1)
                .drawParticipationCount(7)
                .fullAttendModal(fullAttendModal)
                .build();

        // when
        DrawMainResponseDto actualResponse = drawService.getDrawMainPageInfo(userId);

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getInvitedNum()).isEqualTo(expectedResponse.getInvitedNum());
        assertThat(actualResponse.getRemainDrawCount()).isEqualTo(expectedResponse.getRemainDrawCount());
        assertThat(actualResponse.getDrawParticipationCount()).isEqualTo(expectedResponse.getDrawParticipationCount());
        assertThat(((DrawMainFullAttendResponseDto) actualResponse).getFullAttendModal().getTitle()).isEqualTo(expectedResponse.getFullAttendModal().getTitle());
        assertThat(((DrawMainFullAttendResponseDto) actualResponse).getFullAttendModal().getSubtitle()).isEqualTo(expectedResponse.getFullAttendModal().getSubtitle());
        assertThat(((DrawMainFullAttendResponseDto) actualResponse).getFullAttendModal().getImg()).isEqualTo(expectedResponse.getFullAttendModal().getImg());
        assertThat(((DrawMainFullAttendResponseDto) actualResponse).getFullAttendModal().getDescription()).isEqualTo(expectedResponse.getFullAttendModal().getDescription());
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

        Mockito.when(drawParticipationInfoRepository.findDrawParticipationInfoByUserId(userId)).thenReturn(Optional.ofNullable(drawParticipationInfo));

        ShareInfo shareInfo = ShareInfo.builder()
                .userId(userId)
                .invitedNum(3)
                .remainDrawCount(1)
                .build();

        Mockito.when(shareInfoRepository.findShareInfoByUserId(userId)).thenReturn(Optional.ofNullable(shareInfo));

        Mockito.when(drawAttendanceCountUtil.handleAttendanceCount(userId, drawParticipationInfo)).thenReturn(1);

        DrawMainResponseDto expectedResponse = DrawMainResponseDto
                .builder()
                .invitedNum(3)
                .remainDrawCount(1)
                .drawParticipationCount(1)
                .build();

        // when
        DrawMainResponseDto actualResponse = drawService.getDrawMainPageInfo(userId);

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getInvitedNum()).isEqualTo(expectedResponse.getInvitedNum());
        assertThat(actualResponse.getRemainDrawCount()).isEqualTo(expectedResponse.getRemainDrawCount());
        assertThat(actualResponse.getDrawParticipationCount()).isEqualTo(expectedResponse.getDrawParticipationCount());
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

        Mockito.when(shareInfoRepository.findShareInfoByUserId(userId)).thenReturn(Optional.ofNullable(shareInfo));

        ArrayList<String> images = new ArrayList<>();
        images.add("left");
        images.add("left");
        images.add("right");

        DrawLoseModalResponseDto drawLoseModalResponseDto = DrawLoseModalResponseDto.builder()
                .isDrawWin(false)
                .images(images)
                .shareUrl("https://softeer.site/share/of8w")
                .build();

        Mockito.when(drawResponseGenerateUtil.generateDrawLoserResponse(userId)).thenReturn(drawLoseModalResponseDto);

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
    @DisplayName("이미 하루 중 당첨된 적이 있는 사용자의 추첨 참여")
    void participateDrawEventTwice() {
        // given
        Integer userId = 6;

        ShareInfo shareInfo = ShareInfo.builder()
                .userId(userId)
                .invitedNum(3)
                .remainDrawCount(2)
                .build();

        Mockito.when(shareInfoRepository.findShareInfoByUserId(userId)).thenReturn(Optional.ofNullable(shareInfo));

        ArrayList<String> images = new ArrayList<>();
        images.add("left");
        images.add("left");
        images.add("right");

        DrawLoseModalResponseDto drawLoseModalResponseDto = DrawLoseModalResponseDto.builder()
                .isDrawWin(false)
                .images(images)
                .shareUrl("https://softeer.site/share/of8w")
                .build();

        Mockito.when(drawResponseGenerateUtil.generateDrawLoserResponse(userId)).thenReturn(drawLoseModalResponseDto);

        Mockito.when(drawRedisUtil.getRankingIfWinner(userId)).thenReturn(3);

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

        Mockito.when(shareInfoRepository.findShareInfoByUserId(userId)).thenReturn(Optional.ofNullable(shareInfo));

        Mockito.when(drawRedisUtil.getRankingIfWinner(userId)).thenReturn(0);

        Mockito.when(drawUtil.isDrawWin()).thenReturn(false);

        ArrayList<String> images = new ArrayList<>();
        images.add("left");
        images.add("left");
        images.add("right");

        DrawLoseModalResponseDto drawLoseModalResponseDto = DrawLoseModalResponseDto.builder()
                .isDrawWin(false)
                .images(images)
                .shareUrl("https://softeer.site/share/of8w")
                .build();

        Mockito.when(drawResponseGenerateUtil.generateDrawLoserResponse(userId)).thenReturn(drawLoseModalResponseDto);

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

        Mockito.when(shareInfoRepository.findShareInfoByUserId(userId)).thenReturn(Optional.ofNullable(shareInfo));

        Mockito.when(drawRedisUtil.getRankingIfWinner(userId)).thenReturn(0);

        Mockito.when(drawUtil.isDrawWin()).thenReturn(true);
        Mockito.when(drawUtil.getRanking()).thenReturn(1);

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

        Mockito.when(drawResponseGenerateUtil.generateDrawWinnerResponse(1)).thenReturn(drawWinModalResponseDto);

        // when
        DrawModalResponseDto actualResponse = drawService.participateDrawEvent(userId);

        // then
        assertThat(actualResponse).isNotNull();
    }

    @Test
    @DisplayName("추첨 2등 응답 반환")
    void participateDrawEventSecond() {

    }

    @Test
    @DisplayName("추첨 3등 응답 반환")
    void participateDrawEventThird() {

    }

    @BeforeEach
    @DisplayName("getDrawHistory를 위한 초기화")
    void setUpGetDrawHistory() {
        WinModal firstWinModal = WinModal.builder()
                .title("축하합니다!")
                .subtitle("아이패드에 당첨됐어요!")
                .img("https://d1wv99asbppzjv.cloudfront.net/main-page/draw_reward_image_1.svg")
                .description("이벤트 경품 수령을 위해 등록된 전화번호로 영업일 기준 3~5일 내 개별 안내가 진행될 예정입니다.\n" +
                        "이벤트 당첨 이후 개인정보 제공을 거부하거나 개별 안내를 거부하는 경우, 당첨이 취소될 수 있습니다.")
                .build();

        WinModal secondWinModal = WinModal.builder()
                .title("축하합니다!")
                .subtitle("현대백화점 쿠폰 10만원퀀에 당첨됐어요!")
                .img("https://d1wv99asbppzjv.cloudfront.net/main-page/draw_reward_image_2.svg")
                .description("이벤트 경품 수령을 위해 등록된 전화번호로 영업일 기준 3~5일 내 개별 안내가 진행될 예정입니다.\n" +
                        "이벤트 당첨 이후 개인정보 제공을 거부하거나 개별 안내를 거부하는 경우, 당첨이 취소될 수 있습니다.")
                .build();

        WinModal thirdWinModal = WinModal.builder()
                .title("축하합니다!")
                .subtitle("현대백화점 쿠폰 1만원퀀에 당첨됐어요!")
                .img("https://d1wv99asbppzjv.cloudfront.net/main-page/draw_reward_image_3.svg")
                .description("이벤트 경품 수령을 위해 등록된 전화번호로 영업일 기준 3~5일 내 개별 안내가 진행될 예정입니다.\n" +
                        "이벤트 당첨 이후 개인정보 제공을 거부하거나 개별 안내를 거부하는 경우, 당첨이 취소될 수 있습니다.")
                .build();

        DrawHistoryWinnerResponseDto firstWinnerResponse = DrawHistoryWinnerResponseDto.builder()
                .isDrawWin(true)
                .winModal(firstWinModal)
                .build();

        DrawHistoryWinnerResponseDto secondWinnerResponse = DrawHistoryWinnerResponseDto.builder()
                .isDrawWin(true)
                .winModal(secondWinModal)
                .build();

        DrawHistoryWinnerResponseDto thirdWinnerResponse = DrawHistoryWinnerResponseDto.builder()
                .isDrawWin(true)
                .winModal(thirdWinModal)
                .build();

        Mockito.lenient().when(drawResponseGenerateUtil.generateDrawHistoryWinnerResponse(1)).thenReturn(firstWinnerResponse);
        Mockito.lenient().when(drawResponseGenerateUtil.generateDrawHistoryWinnerResponse(2)).thenReturn(secondWinnerResponse);
        Mockito.lenient().when(drawResponseGenerateUtil.generateDrawHistoryWinnerResponse(3)).thenReturn(thirdWinnerResponse);

        DrawHistoryLoserResponseDto loserResponseDto = DrawHistoryLoserResponseDto.builder()
                .isDrawWin(false)
                .shareUrl("https://softeer.shop/share/of8w")
                .build();

        Mockito.lenient().when(drawResponseGenerateUtil.generateDrawHistoryLoserResponse(6)).thenReturn(loserResponseDto);
    }

    @Test
    @DisplayName("1등 당첨자일 경우 당첨 내역 조회 시 당첨 결과 ")
    void getDrawHistoryFirstWinner() {
        // given
        Integer userId = 6;

        Mockito.when(drawRedisUtil.getRankingIfWinner(userId)).thenReturn(1);

        WinModal winModal = WinModal.builder()
                .title("축하합니다!")
                .subtitle("아이패드에 당첨됐어요!")
                .img("https://d1wv99asbppzjv.cloudfront.net/main-page/draw_reward_image_1.svg")
                .description("이벤트 경품 수령을 위해 등록된 전화번호로 영업일 기준 3~5일 내 개별 안내가 진행될 예정입니다.\n" +
                        "이벤트 당첨 이후 개인정보 제공을 거부하거나 개별 안내를 거부하는 경우, 당첨이 취소될 수 있습니다.")
                .build();

        DrawHistoryWinnerResponseDto expectedResponse = DrawHistoryWinnerResponseDto.builder()
                .isDrawWin(true)
                .winModal(winModal)
                .build();

        // when
        DrawHistoryResponseDto actualResponse = drawService.getDrawHistory(userId);

        // then
        assertThat(actualResponse).isNotEqualTo(null);
        assertThat(actualResponse.isDrawWin()).isEqualTo(true);
        assertThat(((DrawHistoryWinnerResponseDto) actualResponse).getWinModal().getTitle()).isEqualTo(expectedResponse.getWinModal().getTitle());
        assertThat(((DrawHistoryWinnerResponseDto) actualResponse).getWinModal().getSubtitle()).isEqualTo(expectedResponse.getWinModal().getSubtitle());
        assertThat(((DrawHistoryWinnerResponseDto) actualResponse).getWinModal().getImg()).isEqualTo(expectedResponse.getWinModal().getImg());
        assertThat(((DrawHistoryWinnerResponseDto) actualResponse).getWinModal().getDescription()).isEqualTo(expectedResponse.getWinModal().getDescription());
    }

    @Test
    @DisplayName("2등 당첨자일 경우 당첨 내역 조회 시 당첨 결과 ")
    void getDrawHistorySecondWinner() {
        // given
        Integer userId = 6;

        Mockito.when(drawRedisUtil.getRankingIfWinner(userId)).thenReturn(2);

        WinModal winModal = WinModal.builder()
                .title("축하합니다!")
                .subtitle("현대백화점 쿠폰 10만원퀀에 당첨됐어요!")
                .img("https://d1wv99asbppzjv.cloudfront.net/main-page/draw_reward_image_2.svg")
                .description("이벤트 경품 수령을 위해 등록된 전화번호로 영업일 기준 3~5일 내 개별 안내가 진행될 예정입니다.\n" +
                        "이벤트 당첨 이후 개인정보 제공을 거부하거나 개별 안내를 거부하는 경우, 당첨이 취소될 수 있습니다.")
                .build();

        DrawHistoryWinnerResponseDto expectedResponse = DrawHistoryWinnerResponseDto.builder()
                .isDrawWin(true)
                .winModal(winModal)
                .build();

        // when
        DrawHistoryResponseDto actualResponse = drawService.getDrawHistory(userId);

        // then
        assertThat(actualResponse).isNotEqualTo(null);
        assertThat(actualResponse.isDrawWin()).isEqualTo(true);
        assertThat(((DrawHistoryWinnerResponseDto) actualResponse).getWinModal().getTitle()).isEqualTo(expectedResponse.getWinModal().getTitle());
        assertThat(((DrawHistoryWinnerResponseDto) actualResponse).getWinModal().getSubtitle()).isEqualTo(expectedResponse.getWinModal().getSubtitle());
        assertThat(((DrawHistoryWinnerResponseDto) actualResponse).getWinModal().getImg()).isEqualTo(expectedResponse.getWinModal().getImg());
        assertThat(((DrawHistoryWinnerResponseDto) actualResponse).getWinModal().getDescription()).isEqualTo(expectedResponse.getWinModal().getDescription());
    }

    @Test
    @DisplayName("3등 당첨자일 경우 당첨 내역 조회 시 당첨 결과 ")
    void getDrawHistoryThirdWinner() {
        // given
        Integer userId = 6;

        Mockito.when(drawRedisUtil.getRankingIfWinner(userId)).thenReturn(3);

        WinModal winModal = WinModal.builder()
                .title("축하합니다!")
                .subtitle("현대백화점 쿠폰 1만원퀀에 당첨됐어요!")
                .img("https://d1wv99asbppzjv.cloudfront.net/main-page/draw_reward_image_3.svg")
                .description("이벤트 경품 수령을 위해 등록된 전화번호로 영업일 기준 3~5일 내 개별 안내가 진행될 예정입니다.\n" +
                        "이벤트 당첨 이후 개인정보 제공을 거부하거나 개별 안내를 거부하는 경우, 당첨이 취소될 수 있습니다.")
                .build();

        DrawHistoryWinnerResponseDto expectedResponse = DrawHistoryWinnerResponseDto.builder()
                .isDrawWin(true)
                .winModal(winModal)
                .build();

        // when
        DrawHistoryResponseDto actualResponse = drawService.getDrawHistory(userId);

        // then
        assertThat(actualResponse).isNotEqualTo(null);
        assertThat(actualResponse.isDrawWin()).isEqualTo(true);
        assertThat(((DrawHistoryWinnerResponseDto) actualResponse).getWinModal().getTitle()).isEqualTo(expectedResponse.getWinModal().getTitle());
        assertThat(((DrawHistoryWinnerResponseDto) actualResponse).getWinModal().getSubtitle()).isEqualTo(expectedResponse.getWinModal().getSubtitle());
        assertThat(((DrawHistoryWinnerResponseDto) actualResponse).getWinModal().getImg()).isEqualTo(expectedResponse.getWinModal().getImg());
        assertThat(((DrawHistoryWinnerResponseDto) actualResponse).getWinModal().getDescription()).isEqualTo(expectedResponse.getWinModal().getDescription());
    }

    @Test
    @DisplayName("당첨자가 아닐 경우 당첨 내역 조회 시 낙첨 결과")
    void getDrawHistoryLoser() {
        // given
        Integer userId = 6;

        Mockito.when(drawRedisUtil.getRankingIfWinner(userId)).thenReturn(0);

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