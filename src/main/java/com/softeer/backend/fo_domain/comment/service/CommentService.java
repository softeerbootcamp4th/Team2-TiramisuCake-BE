package com.softeer.backend.fo_domain.comment.service;

import com.softeer.backend.fo_domain.comment.constant.CommentNickname;
import com.softeer.backend.fo_domain.comment.constant.ExpectationComment;
import com.softeer.backend.fo_domain.comment.domain.Comment;
import com.softeer.backend.fo_domain.comment.dto.CommentsResponseDto;
import com.softeer.backend.fo_domain.comment.repository.CommentRepository;
import com.softeer.backend.fo_domain.comment.util.ScrollPaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private static final int SCROLL_SIZE = 30;

    private final CommentRepository commentRepository;

    /**
     * SCROLL_SIZE 만큼의 기대평을 반환하는 메서드
     * <p>
     * 커서 기반 무한 스크롤 기능을 사용하여 다음 cursor 값을 받아 해당 값보다 작으면서 정해진 개수 만큼의 기대평을 반환한다.
     */
    public CommentsResponseDto getComments(Integer userId, Integer cursor) {

        PageRequest pageRequest = PageRequest.of(0, SCROLL_SIZE + 1);
        Page<Comment> page = commentRepository.findAllByIdLessThanOrderByIdDesc(cursor, pageRequest);
        List<Comment> comments = page.getContent();

        ScrollPaginationUtil<Comment> commentCursor = ScrollPaginationUtil.of(comments, SCROLL_SIZE);
        return CommentsResponseDto.of(commentCursor, userId);
    }

    /**
     * 기대평을 저장하는 메서드
     */
    public void saveComment(Integer userId, int commentNum) {

        // 로그인 한 유저가 기대평을 등록했다면 User entity의 id값을 기반으로 닉네임을 설정한다.
        // 로그인 하지 않았다면, 랜덤으로 닉네임을 설정한다.
        String randomNickname = (userId != null ?
                CommentNickname.getMyRandomNickname(userId) : CommentNickname.getRandomNickname());

        commentRepository.save(Comment.builder()
                .nickname(randomNickname)
                .expectationComment(ExpectationComment.of(commentNum))
                .userId(userId)
                .build()
        );
    }
}
