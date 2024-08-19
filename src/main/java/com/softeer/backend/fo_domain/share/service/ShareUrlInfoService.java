package com.softeer.backend.fo_domain.share.service;

import com.softeer.backend.fo_domain.share.dto.ShareUrlInfoResponseDto;
import com.softeer.backend.fo_domain.share.exception.ShareInfoException;
import com.softeer.backend.fo_domain.share.repository.ShareUrlInfoRepository;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShareUrlInfoService {
    private final ShareUrlInfoRepository shareUrlInfoRepository;

    public ShareUrlInfoResponseDto getShortenShareUrl(Integer userId) {
        if (userId == null) {
            // 로그인하지 않은 사용자
            return ShareUrlInfoResponseDto.builder()
                    .shareUrl("https://softeer.site")
                    .build();
        } else {
            // 로그인한 사용자
            String shareCode = shareUrlInfoRepository.findShareUrlByUserId(userId).orElseThrow(
                    () -> new ShareInfoException(ErrorStatus._NOT_FOUND)
            );

            // DB에 이미 생성된 단축 url 코드 반환
            return ShareUrlInfoResponseDto.builder()
                    .shareUrl("https://softeer.site/" + shareCode)
                    .build();
        }
    }
}
