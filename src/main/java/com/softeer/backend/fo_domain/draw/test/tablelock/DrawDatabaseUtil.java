package com.softeer.backend.fo_domain.draw.test.tablelock;

import com.softeer.backend.fo_domain.draw.test.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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


//    @Transactional
//    public boolean isFirstWinner(Integer userId, int winnerNum) {
//        String sql = "LOCK TABLES test_database.draw_test_first_winner_list WRITE; "
//                + "INSERT INTO test_database.draw_test_first_winner_list (user_id) "
//                + "SELECT ? FROM dual WHERE (SELECT COUNT(*) FROM test_database.draw_test_first_winner_list) < ?; "
//                + "UNLOCK TABLES;";
//
//        int rowsAffected = jdbcTemplate.update(sql, userId, winnerNum);
//
//        return rowsAffected > 0;
//    }
//
//    @Transactional
//    public boolean isSecondWinner(Integer userId, int winnerNum) {
//        String sql = "LOCK TABLES test_database.draw_test_second_winner_list WRITE; "
//                + "INSERT INTO test_database.draw_test_second_winner_list (user_id) "
//                + "SELECT ? FROM dual WHERE (SELECT COUNT(*) FROM test_database.draw_test_second_winner_list) < ?; "
//                + "UNLOCK TABLES;";
//
//        int rowsAffected = jdbcTemplate.update(sql, userId, winnerNum);
//
//        return rowsAffected > 0;
//    }
//
//    @Transactional
//    public boolean isThirdWinner(Integer userId, int winnerNum) {
//        String sql = "LOCK TABLES test_database.draw_test_third_winner_list WRITE; "
//                + "INSERT INTO test_database.draw_test_third_winner_list (user_id) "
//                + "SELECT ? FROM dual WHERE (SELECT COUNT(*) FROM test_database.draw_test_third_winner_list) < ?; "
//                + "UNLOCK TABLES;";
//
//        int rowsAffected = jdbcTemplate.update(sql, userId, winnerNum);
//
//        return rowsAffected > 0;
//    }

//    private boolean isFirstWinner(Integer userId, int winnerNum) {
//        // 테이블 락을 설정합니다.
//        drawTestFirstWinnerListRepository.lockFirstWinnerTable();
//
//        try {
//            if (drawTestFirstWinnerListRepository.count() < winnerNum) {
//                DrawTestFirstWinnerList firstWinner = DrawTestFirstWinnerList.builder()
//                        .userId(userId)
//                        .build();
//
//                drawTestFirstWinnerListRepository.save(firstWinner);
//                return true;
//            }
//        } finally {
//            // 테이블 락을 해제합니다.
//            drawTestFirstWinnerListRepository.unlockFirstWinnerTable();
//        }
//
//        return false;
//    }
//
//    private boolean isSecondWinner(Integer userId, int winnerNum) {
//        // 테이블 락을 설정합니다.
//        drawTestSecondWinnerListRepository.lockSecondWinnerTable();
//
//        try {
//            if (drawTestSecondWinnerListRepository.count() < winnerNum) {
//                DrawTestSecondWinnerList secondWinner = DrawTestSecondWinnerList.builder()
//                        .userId(userId)
//                        .build();
//
//                drawTestSecondWinnerListRepository.save(secondWinner);
//                return true;
//            }
//        } finally {
//            // 테이블 락을 해제합니다.
//            drawTestSecondWinnerListRepository.unlockSecondWinnerTable();
//        }
//
//        return false;
//    }
//
//    private boolean isThirdWinner(Integer userId, int winnerNum) {
//        // 테이블 락을 설정합니다.
//        drawTestThirdWinnerListRepository.lockThirdWinnerTable();
//
//        try {
//            if (drawTestThirdWinnerListRepository.count() < winnerNum) {
//                DrawTestThirdWinnerList thirdWinner = DrawTestThirdWinnerList.builder()
//                        .userId(userId)
//                        .build();
//
//                drawTestThirdWinnerListRepository.save(thirdWinner);
//                return true;
//            }
//        } finally {
//            // 테이블 락을 해제합니다.
//            drawTestThirdWinnerListRepository.unlockThirdWinnerTable();
//        }
//
//        return false;
//    }
}
