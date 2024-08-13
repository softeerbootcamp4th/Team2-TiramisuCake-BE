package com.softeer.backend.fo_domain.share.service;

import com.softeer.backend.fo_domain.share.dto.ShareUrlInfoResponseDto;
import com.softeer.backend.fo_domain.share.exception.ShareInfoException;
import com.softeer.backend.fo_domain.share.repository.ShareUrlInfoRepository;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.response.ResponseDto;
import com.softeer.backend.global.staticresources.util.StaticResourcesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShareUrlInfoService {
    private final ShareUrlInfoRepository shareUrlInfoRepository;
    private final StaticResourcesUtil staticResourcesUtil;

    public ResponseDto<ShareUrlInfoResponseDto> getShortenShareUrl(Integer userId) {
        if (userId == null) {
            // 로그인하지 않은 사용자
            return ResponseDto.onSuccess(ShareUrlInfoResponseDto.builder()
                    .shareUrl(staticResourcesUtil.getData("NON_USER_SHARE_URL"))
                    .build());
        } else {
            // 로그인한 사용자
            String shareUrl = shareUrlInfoRepository.findShareUrlByUserId(userId).orElseThrow(
                    () -> new ShareInfoException(ErrorStatus._NOT_FOUND)
            );

            // DB에 이미 생성된 단축 url 반환
            return ResponseDto.onSuccess(ShareUrlInfoResponseDto.builder()
                    .shareUrl(staticResourcesUtil.getData("BASE_URL") + shareUrl)
                    .build());
        }
    }
}
