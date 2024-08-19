package com.softeer.backend.global.staticresources.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "text_content")
public class TextContent {
    @Id
    @Column(name = "text_content_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "text_name", nullable = false)
    private String textName;

    @Column(name = "content", nullable = false)
    private String content;
}
