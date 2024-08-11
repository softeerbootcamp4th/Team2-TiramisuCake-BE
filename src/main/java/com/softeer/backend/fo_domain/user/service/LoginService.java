package com.softeer.backend.fo_domain.user.service;

import com.softeer.backend.fo_domain.draw.repository.DrawParticipationInfoRepository;
import com.softeer.backend.fo_domain.share.domain.ShareInfo;
import com.softeer.backend.fo_domain.share.repository.ShareInfoRepository;
import com.softeer.backend.fo_domain.share.repository.ShareUrlInfoRepository;
import com.softeer.backend.fo_domain.user.domain.User;
import com.softeer.backend.fo_domain.user.dto.LoginRequestDto;
import com.softeer.backend.global.common.dto.JwtTokenResponseDto;
import com.softeer.backend.fo_domain.user.exception.UserException;
import com.softeer.backend.fo_domain.user.repository.UserRepository;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.constant.RoleType;
import com.softeer.backend.global.common.dto.JwtClaimsDto;
import com.softeer.backend.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final ShareInfoRepository shareInfoRepository;
    private final ShareUrlInfoRepository shareUrlInfoRepository;
    private final DrawParticipationInfoRepository drawParticipationInfoRepository;
    private final JwtUtil jwtUtil;

    /**
     * 1. Login 정보애서 인증 번호가 인증되지 않은 경우, 예외가 발생한다.
     * 2. 전화번호가 User DB에 등록되어 있지 않은 경우, DB에 User를 등록한다.
     * 3. 전화번호가 이미 User DB에 등록되어 있는 경우, 전화번호로 User 객체를 조회한다.
     * 4. User 객체의 id를 얻은 후에, access & refresh token을 client에게 전달한다.
     */
    @Transactional(readOnly = true)
    public JwtTokenResponseDto handleLogin(LoginRequestDto loginRequestDto) {

        // 인증번호가 인증 되지 않은 경우, 예외 발생
        if (!loginRequestDto.getHasCodeVerified()) {
            log.error("hasCodeVerified is false in loginRequest.");
            throw new UserException(ErrorStatus._AUTH_CODE_NOT_VERIFIED);
        }

        int userId;

        // 전화번호가 User DB에 등록되어 있지 않은 경우
        // User를 DB에 등록
        // 추첨 이벤트 참여 정보 생성
        // 공유 정보 생성(초대한 친구 수, 남은 추첨 횟수)
        // 공유 url 생성
        if (!userRepository.existsByPhoneNumber(loginRequestDto.getPhoneNumber())) {
            User user = User.builder()
                    .name(loginRequestDto.getName())
                    .phoneNumber(loginRequestDto.getPhoneNumber())
                    .privacyConsent(loginRequestDto.getPrivacyConsent())
                    .marketingConsent(loginRequestDto.getMarketingConsent())
                    .build();

            User registeredUser = userRepository.save(user);
            userId = registeredUser.getId();

            createDrawParticipationInfo(userId); // 추첨 이벤트 참여 정보 생성
            createShareInfo(userId); // 공유 정보 생성(초대한 친구 수, 남은 추첨 횟수)
            createShareUrlInfo(userId); // 공유 url 생성
        }
        // 전화번호가 이미 User DB에 등록되어 있는 경우
        // 전화번호로 User 객체 조회
        else {
            User user = userRepository.findByPhoneNumber(loginRequestDto.getPhoneNumber());
            userId = user.getId();
        }

        return jwtUtil.createServiceToken(JwtClaimsDto.builder()
                .id(userId)
                .roleType(RoleType.ROLE_USER)
                .build());

    }

    private void createShareInfo(Integer userId) {
        ShareInfo shareInfo = ShareInfo.builder()
                .userId(userId)
                .invitedNum(0)
                .remainDrawCount(1)
                .build();

        shareInfoRepository.save(shareInfo);
    }

    private void createShareUrlInfo(Integer userId) {

    }

    private void createDrawParticipationInfo(Integer userId) {

    }
}
