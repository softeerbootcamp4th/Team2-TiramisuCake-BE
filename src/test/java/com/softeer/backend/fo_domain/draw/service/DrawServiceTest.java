package com.softeer.backend.fo_domain.draw.service;

import com.softeer.backend.fo_domain.draw.dto.modal.WinModal;
import com.softeer.backend.fo_domain.draw.dto.result.DrawHistoryLoserResponseDto;
import com.softeer.backend.fo_domain.draw.dto.result.DrawHistoryResponseDto;
import com.softeer.backend.fo_domain.draw.dto.result.DrawHistoryWinnerResponseDto;
import com.softeer.backend.fo_domain.draw.repository.DrawParticipationInfoRepository;
import com.softeer.backend.fo_domain.draw.util.DrawModalGenerateUtil;
import com.softeer.backend.fo_domain.draw.util.DrawResponseGenerateUtil;
import com.softeer.backend.fo_domain.draw.util.DrawUtil;
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
    private DrawSettingManager drawSettingManager;

    @InjectMocks
    private DrawService drawService;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void getDrawMainPageInfo() {
    }

    @Test
    void participateDrawEvent() {
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
}