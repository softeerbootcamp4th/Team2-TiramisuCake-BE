package com.softeer.backend.fo_domain.comment.domain;


import com.softeer.backend.fo_domain.comment.constant.CommentNickname;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "comment")
public class Comment {

    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "commentType", nullable = false)
    private Integer commentType;

    @CreatedDate
    @Column(name = "upload_time", updatable = false)
    private LocalDateTime uploadTime;

    @Column(name = "user_id", nullable = true)
    private Integer userId;

    @PrePersist
    public void assignRandomNickname() {
        if (userId != null) {
            this.nickname = CommentNickname.getMyRandomNickname(userId);
        } else {
            this.nickname = CommentNickname.getRandomNickname();
        }
    }

}
