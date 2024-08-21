package com.softeer.backend.fo_domain.comment.dto;

import com.softeer.backend.fo_domain.comment.constant.CommentNickname;
import com.softeer.backend.fo_domain.comment.domain.Comment;
import com.softeer.backend.fo_domain.comment.util.ScrollPaginationUtil;
import lombok.*;

import java.util.List;

/**
 * 기대평 응답 Dto 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class CommentsResponseDto {
    public static final int LAST_CURSOR = -1;

    private int nextCursor;

    private int totalComments;

    private List<CommentResponse> comments;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class CommentResponse {

        private Boolean isMine;

        private String nickName;

        private int commentType;
    }

    public static CommentsResponseDto of(ScrollPaginationUtil<Comment> commentsScroll, Integer userId) {
        if (commentsScroll.isLastScroll()) {
            return CommentsResponseDto.newLastScroll(commentsScroll.getCurrentScrollItems(), userId);
        }
        return CommentsResponseDto.newScrollHasNext(commentsScroll.getCurrentScrollItems(), commentsScroll.getNextCursor().getId(),
                userId);
    }

    /**
     * 마지막 스크롤일 때의 응답값을 구성하는 메서드
     * <p>
     * nextCursor 값을 -1로 설정한다.
     */
    private static CommentsResponseDto newLastScroll(List<Comment> commentsScroll, Integer userId) {
        return newScrollHasNext(commentsScroll, LAST_CURSOR, userId);
    }

    /**
     * 마지막 스크롤이 아닐 때의 응답값을 구성하는 메서드
     */
    private static CommentsResponseDto newScrollHasNext(List<Comment> commentsScroll, int nextCursor,
                                                        Integer userId) {
        return CommentsResponseDto.builder()
                .nextCursor(nextCursor)
                .totalComments(commentsScroll.size())
                .comments(getContents(commentsScroll, userId))
                .build();
    }

    /**
     * CommentResponse를 생성하여 반환하는 메서드
     * <p>
     * 유저가 로그인을 한 상태에서 자신의 댓글이 응답에 포함될 경우,
     * isMine 변수값을 true로, nickname의 접미사에 '(나)'를 붙여서 응답을 구성한다.
     */
    private static List<CommentResponse> getContents(List<Comment> commentsScroll, Integer userId) {
        return commentsScroll.stream()
                .map(_comment -> {
                    boolean isMine = false;
                    String nickname = _comment.getNickname();
                    int commentType = _comment.getCommentType();

                    if (userId != null && _comment.getUserId() != null &&
                            _comment.getUserId().equals(userId)) {
                        isMine = true;
                        nickname = nickname + CommentNickname.MY_NICKNAME_SUFFIX;
                    }

                    return CommentResponse.builder()
                            .isMine(isMine)
                            .nickName(nickname)
                            .commentType(commentType)
                            .build();
                })
                .toList();

    }
}
