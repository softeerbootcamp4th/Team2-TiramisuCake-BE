package com.softeer.backend.fo_domain.comment.controller;

import com.softeer.backend.fo_domain.comment.dto.CommentsResponseDto;
import com.softeer.backend.fo_domain.comment.dto.CommentsResponsePageDto;
import com.softeer.backend.fo_domain.comment.exception.CommentException;
import com.softeer.backend.fo_domain.comment.service.CommentService;
import com.softeer.backend.global.annotation.AuthInfo;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 기대평 요청을 처리하는 컨트롤러 클래스
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    /**
     * cursor값을 기준으로 기대평을 반환하는 메서드
     * <p>
     * cursor값이 null일 경우, Integer의 최대값으로 설정한다.
     */
    @GetMapping("/comment")
    ResponseDto<CommentsResponseDto> getComment(@RequestParam(name = "cursor", required = false) Integer cursor,
                                                @Parameter(hidden = true) @AuthInfo Integer userId) {
        if (cursor == null) {
            cursor = Integer.MAX_VALUE;
        }

        CommentsResponseDto commentsResponseDto = commentService.getComments(userId, cursor);

        return ResponseDto.onSuccess(commentsResponseDto);
    }

    @GetMapping("/comment/page")
    ResponseDto<CommentsResponsePageDto> getCommentByPage(@RequestParam(name = "page", defaultValue = "0") Integer page) {

        CommentsResponsePageDto commentsResponsePageDto = commentService.getCommentsByPage(page);

        return ResponseDto.onSuccess(commentsResponsePageDto);
    }

    /**
     * 기대평을 등록하는 메서드
     */
    @PostMapping("/comment")
    ResponseDto<Void> saveComment(@RequestParam(name = "commentType") Integer commentType,
                                  @Parameter(hidden = true) @AuthInfo Integer userId) {

        if (commentType == null || commentType < 1 || commentType > 5) {

            log.error("Invalid commentType value: {}. It must be between 1 and 5.", commentType);
            throw new CommentException(ErrorStatus._VALIDATION_ERROR);
        }

        commentService.saveComment(userId, commentType);

        return ResponseDto.onSuccess();

    }

    @PostMapping("/comment/test")
    ResponseDto<Void> saveCommentTest(@RequestParam(name = "num") Integer num) {

        commentService.saveCommentTest(num);

        return ResponseDto.onSuccess();

    }
}
