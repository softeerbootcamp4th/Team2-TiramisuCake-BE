package com.softeer.backend.fo_domain.user.service;

import com.softeer.backend.fo_domain.draw.domain.DrawParticipationInfo;
import com.softeer.backend.fo_domain.draw.repository.DrawParticipationInfoRepository;
import com.softeer.backend.fo_domain.share.domain.ShareInfo;
import com.softeer.backend.fo_domain.share.domain.ShareUrlInfo;
import com.softeer.backend.fo_domain.share.exception.ShareUrlInfoException;
import com.softeer.backend.fo_domain.share.repository.ShareInfoRepository;
import com.softeer.backend.fo_domain.share.repository.ShareUrlInfoRepository;
import com.softeer.backend.fo_domain.user.domain.User;
import com.softeer.backend.fo_domain.user.dto.LoginRequestDto;
import com.softeer.backend.fo_domain.user.exception.UserException;
import com.softeer.backend.fo_domain.user.repository.UserRepository;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.dto.JwtClaimsDto;
import com.softeer.backend.global.common.dto.JwtTokenResponseDto;
import com.softeer.backend.global.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ShareInfoRepository shareInfoRepository;

    @Mock
    private ShareUrlInfoRepository shareUrlInfoRepository;

    @Mock
    private DrawParticipationInfoRepository drawParticipationInfoRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private LoginService loginService;

    private LoginRequestDto loginRequestDto;
    private final String shareCode = "ABCD";
    private final String phoneNumber = "01012345678";

    @BeforeEach
    void setUp() {
        loginRequestDto = LoginRequestDto.builder()
                .phoneNumber(phoneNumber)
                .name("TestUser")
                .hasCodeVerified(true)
                .privacyConsent(true)
                .marketingConsent(true)
                .build();

    }

    @Test
    @DisplayName("인증되지 않은 코드로 로그인 시도 시 예외 발생")
    void testHandleLogin_UnverifiedCode() {
        // given
        loginRequestDto.setHasCodeVerified(false);

        // when / then
        assertThatThrownBy(() -> loginService.handleLogin(loginRequestDto, shareCode))
                .isInstanceOf(UserException.class)
                .extracting(ex -> ((UserException) ex).getCode())
                .isEqualTo(ErrorStatus._AUTH_CODE_NOT_VERIFIED);
    }

    @Test
    @DisplayName("전화번호가 DB에 없는 경우 새로운 User 등록 및 관련 정보 생성")
    void testHandleLogin_NewUserRegistration() {
        // given
        lenient().when(shareUrlInfoRepository.findUserIdByShareUrl(anyString())).thenReturn(Optional.of(1));
        when(userRepository.existsByPhoneNumber(phoneNumber)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1);  // 임의의 ID 설정
            return user;
        });

        when(jwtUtil.createServiceToken(any(JwtClaimsDto.class)))
                .thenReturn(JwtTokenResponseDto.builder()
                        .accessToken("accessToken")
                        .refreshToken("refreshToken")
                        .build());

        // when
        JwtTokenResponseDto response = loginService.handleLogin(loginRequestDto, shareCode);

        // then
        verify(userRepository, times(1)).save(any(User.class));
        verify(drawParticipationInfoRepository, times(1)).save(any(DrawParticipationInfo.class));
        verify(shareInfoRepository, times(1)).save(any(ShareInfo.class));
        verify(shareUrlInfoRepository, times(1)).save(any(ShareUrlInfo.class));

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
    }

    @Test
    @DisplayName("전화번호가 DB에 있는 경우 기존 User 객체 조회")
    void testHandleLogin_ExistingUser() {
        // given
        User existingUser = User.builder()
                .id(1)
                .name("TestUser")
                .phoneNumber(phoneNumber)
                .privacyConsent(true)
                .marketingConsent(true)
                .build();

        when(userRepository.existsByPhoneNumber(phoneNumber)).thenReturn(true);
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(existingUser);

        when(jwtUtil.createServiceToken(any(JwtClaimsDto.class)))
                .thenReturn(JwtTokenResponseDto.builder()
                        .accessToken("accessToken")
                        .refreshToken("refreshToken")
                        .build());

        // when
        JwtTokenResponseDto response = loginService.handleLogin(loginRequestDto, shareCode);

        // then
        verify(userRepository, times(1)).findByPhoneNumber(phoneNumber);
        verify(userRepository, never()).save(any(User.class));  // 새로운 User는 저장되지 않음

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
    }

    @Test
    @DisplayName("공유 URL을 통해 로그인한 경우 공유자에게 추첨 기회 추가")
    void testHandleLogin_SharedUrl() {
        // given
        User newUser = User.builder()
                .id(1)
                .name("TestUser")
                .phoneNumber(phoneNumber)
                .privacyConsent(true)
                .marketingConsent(true)
                .build();

        when(userRepository.existsByPhoneNumber(phoneNumber)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(shareUrlInfoRepository.findUserIdByShareUrl(shareCode)).thenReturn(Optional.of(2));

        when(jwtUtil.createServiceToken(any(JwtClaimsDto.class)))
                .thenReturn(JwtTokenResponseDto.builder()
                        .accessToken("accessToken")
                        .refreshToken("refreshToken")
                        .build());

        // when
        JwtTokenResponseDto response = loginService.handleLogin(loginRequestDto, shareCode);

        // then
        verify(shareInfoRepository, times(1)).increaseInvitedNumAndRemainDrawCount(2);
        verify(shareUrlInfoRepository, times(1)).findUserIdByShareUrl(shareCode);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
    }
}
