package com.softeer.backend.fo_domain.comment.controller;

import com.softeer.backend.fo_domain.comment.dto.CommentsResponse;
import com.softeer.backend.fo_domain.comment.service.CommentService;
import com.softeer.backend.global.annotation.AuthInfo;
import com.softeer.backend.global.common.code.status.SuccessStatus;
import com.softeer.backend.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/comment")
    ResponseDto<CommentsResponse> getComment(@RequestParam(name = "cursor", required = false) Integer cursor,
                                             @AuthInfo Integer userId){
        if (cursor == null) {
            cursor = Integer.MAX_VALUE;
        }

        CommentsResponse commentsResponse = commentService.getComments(userId, cursor);

        if(commentsResponse.getNextCursor() != CommentsResponse.LAST_CURSOR)
            return ResponseDto.onSuccess(SuccessStatus._COMMENT_GET_SUCCESS, commentsResponse);

        return ResponseDto.onSuccess(SuccessStatus._COMMENT_GET_FINAL_SUCCESS, commentsResponse);
    }

    @PostMapping("/comment")
    ResponseDto<Void> saveComment(@RequestParam(name = "commentNum") int commentNum,
            @AuthInfo Integer userId){

        commentService.saveComment(userId, commentNum);

        return ResponseDto.onSuccess(SuccessStatus._COMMENT_SAVE_SUCCESS);

    }
}
