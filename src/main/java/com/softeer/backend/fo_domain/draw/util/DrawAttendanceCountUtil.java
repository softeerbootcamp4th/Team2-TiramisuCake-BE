package com.softeer.backend.fo_domain.draw.util;

import com.softeer.backend.fo_domain.draw.domain.DrawParticipationInfo;
import com.softeer.backend.fo_domain.draw.repository.DrawParticipationInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DrawAttendanceCountUtil {
    private final DrawParticipationInfoRepository drawParticipationInfoRepository;

    /**
     * 연속 출석인지 판단
     * 1. 연속 출석이면 연속 출석일수 1 증가하여 DB에 업데이트
     * 2. 연속 출석이 아니면 DB에 연속 출석일수 1로 초기화
     * 3. 현재 출석시각을 마지막 출석시각으로 DB에 업데이트
     *
     * @param userId                사용자 아이디
     * @param drawParticipationInfo 참여 정보
     * @return 연속출석 일수 반환
     */
    public int handleAttendanceCount(Integer userId, DrawParticipationInfo drawParticipationInfo) {
        LocalDateTime lastAttendance = drawParticipationInfo.getLastAttendance();

        // 한 번도 접속한 적이 없는 사람이라면
        if (lastAttendance == null) {
            // 연속출석일수 1로 초기화
            drawParticipationInfoRepository.setAttendanceCountToOne(userId);

            // lastAttendance를 현재 시각으로 설정
            drawParticipationInfoRepository.setLastAttendance(userId, LocalDateTime.now());

            return 1;
        }

        // 마지막 접속 시간이 오늘이라면 false 반환
        if (isLastAttendanceToday(lastAttendance)) {
            // lastAttendance를 현재 시각으로 설정
            drawParticipationInfoRepository.setLastAttendance(userId, LocalDateTime.now());

            return drawParticipationInfo.getDrawAttendanceCount();
        }

        if (isContinuousAttendance(lastAttendance)) {
            // 연속 출석이라면 연속출석일수 1 증가
            drawParticipationInfoRepository.increaseAttendanceCount(userId);

            // lastAttendance를 현재 시각으로 설정
            drawParticipationInfoRepository.setLastAttendance(userId, LocalDateTime.now());
            return drawParticipationInfo.getDrawAttendanceCount() + 1;
        } else {
            // 연속출석이 아니라면 연속출석일수 1로 초기화
            drawParticipationInfoRepository.setAttendanceCountToOne(userId);

            // lastAttendance를 현재 시각으로 설정
            drawParticipationInfoRepository.setLastAttendance(userId, LocalDateTime.now());
            return 1;
        }
    }

    /**
     * 연속 출석인지 판단
     *
     * @param lastAttendance 마지막 출석 날짜
     * @return 연속 출석이면 true, 연속출석이 아니면 false 반환
     */
    private boolean isContinuousAttendance(LocalDateTime lastAttendance) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = lastAttendance.plusDays(1).with(LocalTime.MIDNIGHT); // 마지막 접속일자의 다음날 자정
        LocalDateTime endDateTime = lastAttendance.plusDays(2).with(LocalTime.MIDNIGHT); // 마지막 접속일자의 2일 후 자정

        return (now.isAfter(startDateTime) && now.isBefore(endDateTime));
    }

    /**
     * 마지막 출석 시간이 오늘인지 판단
     *
     * @param lastAttendance 마지막 출석 날짜
     * @return 마지막 출석 시간이 오늘이면 true, 아니면 false 반환
     */
    private boolean isLastAttendanceToday(LocalDateTime lastAttendance) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = lastAttendance.with(LocalTime.MIDNIGHT);
        LocalDateTime endDateTime = lastAttendance.plusDays(1).with(LocalTime.MIDNIGHT);

        return (now.isAfter(startDateTime) && now.isBefore(endDateTime));
    }
}
