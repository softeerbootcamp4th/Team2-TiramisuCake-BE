package com.softeer.backend.fo_domain.share.service;

import com.softeer.backend.fo_domain.share.dto.ShareUrlInfoResponseDto;
import com.softeer.backend.fo_domain.share.exception.ShareInfoException;
import com.softeer.backend.fo_domain.share.repository.ShareUrlInfoRepository;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 공유 url 요청과 응답을 처리하기 위한 클래스
 */
@Service
@RequiredArgsConstructor
public class ShareUrlInfoService {
    // 인증하지 않은 사용자를 위한 공유 url
    public static final String NON_USER_SHARE_URL = "https://softeer.site";
    // 인증한 사용자를 위한 base url
    public static final String BASE_URL = "https://softeer.site/share/";

    private final ShareUrlInfoRepository shareUrlInfoRepository;

    /**
     * 공유 url 응답을 반환하는 메서드
     *
     * 1. 로그인 한 사용자인지 확인
     *  1-1. 로그인 하지 않은 사용자이면 기본 url 반환
     *  1-2. 로그인 한 사용자이면 고유의 공유 코드를 붙인 공유 url 반환
     */
    public ShareUrlInfoResponseDto getShortenShareUrl(Integer userId) {
        if (userId == null) {
            // 로그인하지 않은 사용자
            return ShareUrlInfoResponseDto.builder()
                    .shareUrl(NON_USER_SHARE_URL)
                    .build();
        } else {
            // 로그인한 사용자
            String shareCode = shareUrlInfoRepository.findShareUrlByUserId(userId).orElseThrow(
                    () -> new ShareInfoException(ErrorStatus._NOT_FOUND)
            );

            // DB에 이미 생성된 단축 url 코드 반환
            return ShareUrlInfoResponseDto.builder()
                    .shareUrl(BASE_URL + shareCode)
                    .build();
        }
    }
}
