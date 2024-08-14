package com.softeer.backend.fo_domain.comment.controller;

import com.softeer.backend.fo_domain.comment.dto.CommentsResponseDto;
import com.softeer.backend.fo_domain.comment.exception.CommentException;
import com.softeer.backend.fo_domain.comment.service.CommentService;
import com.softeer.backend.global.annotation.AuthInfo;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
    ResponseDto<Void> saveComment(@RequestParam(name = "commentType") Integer commentType,
                                  @AuthInfo Integer userId) {

        if(commentType == null || commentType<1 || commentType > 5){

            log.error("Invalid commentType value: {}. It must be between 1 and 5.", commentType);
            throw new CommentException(ErrorStatus._VALIDATION_ERROR);
        }

        commentService.saveComment(userId, commentType);

        return ResponseDto.onSuccess();

    }
}
