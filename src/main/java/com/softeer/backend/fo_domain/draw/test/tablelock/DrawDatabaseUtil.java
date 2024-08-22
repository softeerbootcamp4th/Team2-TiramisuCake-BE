package com.softeer.backend.fo_domain.draw.test.tablelock;

import com.softeer.backend.fo_domain.draw.test.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DrawDatabaseUtil {
    private final DrawTestFirstWinnerListRepository drawTestFirstWinnerListRepository;
    private final DrawTestSecondWinnerListRepository drawTestSecondWinnerListRepository;
    private final DrawTestThirdWinnerListRepository drawTestThirdWinnerListRepository;
    private final DrawTestParticipantCountRepository drawTestParticipantCountRepository;

    public void increaseDrawParticipationCount() {
        drawTestParticipantCountRepository.increaseParticipantCount();
    }

    @Transactional
    public int getRankingIfWinner(Integer userId) {
        if (drawTestFirstWinnerListRepository.existsByUserId(userId)) {
            return 1;
        }

        if (drawTestSecondWinnerListRepository.existsByUserId(userId)) {
            return 2;
        }

        if (drawTestThirdWinnerListRepository.existsByUserId(userId)) {
            return 3;
        }
        return 0;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean isWinner(Integer userId, int ranking, int winnerNum) {
        if (ranking == 1) {
            return isFirstWinner(userId, winnerNum);
        }
        if (ranking == 2) {
            return isSecondWinner(userId, winnerNum);
        }
        if (ranking == 3) {
            return isThirdWinner(userId, winnerNum);
        }
        return false;
    }

    @Transactional
    public boolean isFirstWinner(Integer userId, int winnerNum) {
        // 비관적 잠금으로 데이터 획득
        List<DrawTestFirstWinnerList> winners = drawTestFirstWinnerListRepository.findAllForUpdate();

        if (winners.size() < winnerNum) {
            DrawTestFirstWinnerList firstWinner = DrawTestFirstWinnerList.builder()
                    .userId(userId)
                    .build();

            drawTestFirstWinnerListRepository.save(firstWinner);
            return true;
        }

        return false;
    }

    @Transactional
    public boolean isSecondWinner(Integer userId, int winnerNum) {
        // 비관적 잠금으로 데이터 획득
        List<DrawTestSecondWinnerList> winners = drawTestSecondWinnerListRepository.findAllForUpdate();

        if (winners.size() < winnerNum) {
            DrawTestSecondWinnerList secondWinner = DrawTestSecondWinnerList.builder()
                    .userId(userId)
                    .build();

            drawTestSecondWinnerListRepository.save(secondWinner);
            return true;
        }

        return false;
    }

    @Transactional
    public boolean isThirdWinner(Integer userId, int winnerNum) {
        // 비관적 잠금으로 데이터 획득
        List<DrawTestThirdWinnerList> winners = drawTestThirdWinnerListRepository.findAllForUpdate();

        if (winners.size() < winnerNum) {
            DrawTestThirdWinnerList thirdWinner = DrawTestThirdWinnerList.builder()
                    .userId(userId)
                    .build();

            drawTestThirdWinnerListRepository.save(thirdWinner);
            return true;
        }

        return false;
    }
}
