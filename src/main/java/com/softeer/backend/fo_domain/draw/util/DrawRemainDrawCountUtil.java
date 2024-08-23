package com.softeer.backend.fo_domain.draw.util;

import com.softeer.backend.fo_domain.draw.domain.DrawParticipationInfo;
import com.softeer.backend.fo_domain.share.repository.ShareInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 남은 추첨 기회를 관리하는 클래스
 */
@Component
@RequiredArgsConstructor
public class DrawRemainDrawCountUtil {
    private final ShareInfoRepository shareInfoRepository;

    public int handleRemainDrawCount(Integer userId, int remainDrawCount, DrawParticipationInfo drawParticipationInfo) {
        LocalDate lastAttendance = drawParticipationInfo.getLastAttendance() != null
                ? drawParticipationInfo.getLastAttendance().toLocalDate()
                : null;
        LocalDate now = LocalDate.now();

        if (lastAttendance == null) {
            // lastAttendance가 null인 경우 remainDrawCount를 반환
            return remainDrawCount;
        }

        if (now.isAfter(lastAttendance)) {
            shareInfoRepository.increaseRemainDrawCount(userId);
            return remainDrawCount + 1;
        } else {
            return remainDrawCount;
        }
    }
}
