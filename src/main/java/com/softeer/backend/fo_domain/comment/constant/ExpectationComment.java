package com.softeer.backend.fo_domain.comment.constant;

import com.softeer.backend.fo_domain.comment.exception.CommentException;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 기대평을 관리하는 Enum 클래스
 */
@Slf4j
@Getter
public enum ExpectationComment {
    COMMENT_1("기대돼요!"),
    COMMENT_2("경품 당첨되고 싶어요"),
    COMMENT_3("재밌을 것 같아요"),
    COMMENT_4("The new IONIQ 5 최고"),
    COMMENT_5("좋은 이벤트에요");

    private final String comment;

    ExpectationComment(String comment) {
        this.comment = comment;
    }

    public int getCommentOrder() {
        return this.ordinal() + 1;
    }

    public static ExpectationComment of(int commentNum) {
        for (ExpectationComment comment : values()) {
            if (comment.getCommentOrder() == commentNum) {
                return comment;
            }
        }

        log.error("Invalid comment number: " + commentNum);
        throw new CommentException(ErrorStatus._COMMENT_NUM_INVALID);
    }
}
