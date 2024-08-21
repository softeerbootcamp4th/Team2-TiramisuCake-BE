package com.softeer.backend.bo_domain.admin.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 어드민 계정 정보 엔티티 클래스
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "admin")
public class Admin {

    @Id
    @Column(name = "admin_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "admin_account", nullable = false, unique = true)
    private String account;

    @Column(name = "password", nullable = false)
    private String password;

}
