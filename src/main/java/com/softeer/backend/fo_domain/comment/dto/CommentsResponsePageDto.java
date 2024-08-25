package com.softeer.backend.fo_domain.comment.dto;

import com.softeer.backend.fo_domain.comment.domain.Comment;
import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class CommentsResponsePageDto {


    private int nextPage;

    private int totalComments;

    private List<CommentPageResponse> comments;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class CommentPageResponse {

        private Boolean isMine;

        private String nickName;

        private int commentType;
    }

    public static CommentsResponsePageDto of(List<Comment> comments, int nextPage) {
        List<CommentPageResponse> commentPageResponseList = comments.stream()
                .map((comment) -> CommentPageResponse.builder()
                        .isMine(false)
                        .nickName(comment.getNickname())
                        .commentType(comment.getCommentType())
                        .build())
                .toList();

        return CommentsResponsePageDto.builder()
                .nextPage(nextPage)
                .totalComments(comments.size())
                .comments(commentPageResponseList)
                .build();
    }
}
