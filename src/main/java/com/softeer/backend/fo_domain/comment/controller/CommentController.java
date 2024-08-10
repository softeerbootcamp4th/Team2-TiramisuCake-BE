package com.softeer.backend.fo_domain.comment.controller;

import com.softeer.backend.fo_domain.comment.dto.CommentsResponseDto;
import com.softeer.backend.fo_domain.comment.service.CommentService;
import com.softeer.backend.global.annotation.AuthInfo;
import com.softeer.backend.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/comment")
    ResponseDto<CommentsResponseDto> getComment(@RequestParam(name = "cursor", required = false) Integer cursor,
                                                @AuthInfo Integer userId) {
        if (cursor == null) {
            cursor = Integer.MAX_VALUE;
        }

        CommentsResponseDto commentsResponseDto = commentService.getComments(userId, cursor);

        if (commentsResponseDto.getNextCursor() != CommentsResponseDto.LAST_CURSOR)
            return ResponseDto.onSuccess(commentsResponseDto);

        return ResponseDto.onSuccess(commentsResponseDto);
    }

    @PostMapping("/comment")
    ResponseDto<Void> saveComment(@RequestParam(name = "commentNum") int commentNum,
                                  @AuthInfo Integer userId) {

        commentService.saveComment(userId, commentNum);

        return ResponseDto.onSuccess();

    }
}
