package com.softeer.backend.fo_domain.comment.service;

import com.softeer.backend.fo_domain.comment.constant.CommentNickname;
import com.softeer.backend.fo_domain.comment.domain.Comment;
import com.softeer.backend.fo_domain.comment.dto.CommentsResponseDto;
import com.softeer.backend.fo_domain.comment.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    private static final int SCROLL_SIZE = 30;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("getComments - 기대평을 페이징 처리하여 반환")
    void testGetComments() {
        // Given
        Integer userId = 1;
        Integer cursor = 10;
        List<Comment> comments = createComments();

        Page<Comment> page = new PageImpl<>(comments, PageRequest.of(0, SCROLL_SIZE + 1), comments.size());
        when(commentRepository.findAllByIdLessThanOrderByIdDesc(anyInt(), any(PageRequest.class)))
                .thenReturn(page);

        // When
        CommentsResponseDto response = commentService.getComments(userId, cursor);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getComments()).hasSize(2);

        // 첫 번째 기대평
        CommentsResponseDto.CommentResponse firstCommentResponse = response.getComments().get(0);
        assertThat(firstCommentResponse).isNotNull();
        assertThat(firstCommentResponse.getIsMine()).isTrue();
        assertThat(firstCommentResponse.getNickName()).isEqualTo("user1(나)");
        assertThat(firstCommentResponse.getCommentType()).isEqualTo(1);

        // 두 번째 기대평
        CommentsResponseDto.CommentResponse secondCommentResponse = response.getComments().get(1);
        assertThat(secondCommentResponse).isNotNull();
        assertThat(secondCommentResponse.getIsMine()).isFalse();
        assertThat(secondCommentResponse.getNickName()).isEqualTo("user2");
        assertThat(secondCommentResponse.getCommentType()).isEqualTo(1);

        verify(commentRepository, times(1))
                .findAllByIdLessThanOrderByIdDesc(eq(cursor), any(PageRequest.class));
    }

    @Test
    @DisplayName("saveComment - 기대평 저장 테스트")
    void testSaveComment() {
        // Given
        Integer userId = 1;
        int commentType = 1;

        // When
        commentService.saveComment(userId, commentType);

        // Then
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    private List<Comment> createComments() {
        return Arrays.asList(
                Comment.builder()
                        .id(9)
                        .nickname("user1")
                        .commentType(1)
                        .userId(1)
                        .build(),
                Comment.builder()
                        .id(8)
                        .nickname("user2")
                        .commentType(1)
                        .userId(2)
                        .build()
        );
    }
}