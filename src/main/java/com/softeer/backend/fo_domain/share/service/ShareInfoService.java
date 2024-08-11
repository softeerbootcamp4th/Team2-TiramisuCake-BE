package com.softeer.backend.fo_domain.share.service;

import com.softeer.backend.fo_domain.share.dto.ShareUrlResponseDto;
import com.softeer.backend.fo_domain.share.exception.ShareInfoException;
import com.softeer.backend.fo_domain.share.repository.ShareInfoRepository;
import com.softeer.backend.fo_domain.share.repository.ShareUrlInfoRepository;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShareInfoService {
    private final ShareInfoRepository shareInfoRepository;
    private final ShareUrlInfoRepository shareUrlInfoRepository;

    public ResponseDto<ShareUrlResponseDto> getShortenShareUrl(Integer userId) {
        String sharedUrl = shareUrlInfoRepository.findShareUrlByUserId(userId).orElseThrow(
                () -> new ShareInfoException(ErrorStatus._NOT_FOUND)
        );

        // DB에 이미 생성된 단축 url 반환
        return ResponseDto.onSuccess(ShareUrlResponseDto.builder()
                .shareUrl(sharedUrl)
                .build());
    }
}
