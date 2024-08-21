package com.softeer.backend.fo_domain.share.service;

import com.softeer.backend.fo_domain.share.dto.ShareUrlInfoResponseDto;
import com.softeer.backend.fo_domain.share.exception.ShareInfoException;
import com.softeer.backend.fo_domain.share.repository.ShareUrlInfoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@Transactional
@ExtendWith(MockitoExtension.class)
class ShareUrlInfoServiceTest {
    public static final String NON_USER_SHARE_URL = "https://softeer.site";
    public static final String BASE_URL = "https://softeer.site/share/";
    private static final Integer VALID_USER_ID = 6;
    private static final String VALID_SHARE_CODE = "of8w";

    @Mock
    private ShareUrlInfoRepository shareUrlInfoRepository;

    @InjectMocks
    private ShareUrlInfoService shareUrlInfoService;

    @Test
    @DisplayName("로그인하지 않은 사용자 (userId가 null)인 경우")
    void testGetShortenShareUrl_forNonLoggedInUser() {
        ShareUrlInfoResponseDto response = shareUrlInfoService.getShortenShareUrl(null);

        assertThat(response).isNotNull();
        assertThat(response.getShareUrl()).isEqualTo(NON_USER_SHARE_URL);
    }

    @Test
    @DisplayName("로그인한 사용자, 유효한 userId와 shareCode가 존재하는 경우")
    void testGetShortenShareUrl_forLoggedInUser_validShareCode() {
        // given
        when(shareUrlInfoRepository.findShareUrlByUserId(VALID_USER_ID))
                .thenReturn(Optional.of(VALID_SHARE_CODE));

        // when
        ShareUrlInfoResponseDto response = shareUrlInfoService.getShortenShareUrl(VALID_USER_ID);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getShareUrl()).isEqualTo(BASE_URL + VALID_SHARE_CODE);
    }

    @Test
    @DisplayName("로그인한 사용자, 유효한 userId에 해당하는 shareCode가 없는 경우")
    void testGetShortenShareUrl_forLoggedInUser_shareCodeNotFound() {
        // when
        when(shareUrlInfoRepository.findShareUrlByUserId(VALID_USER_ID))
                .thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> shareUrlInfoService.getShortenShareUrl(VALID_USER_ID))
                .isInstanceOf(ShareInfoException.class);
    }
}