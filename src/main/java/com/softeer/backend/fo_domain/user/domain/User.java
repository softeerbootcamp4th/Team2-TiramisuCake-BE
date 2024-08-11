package com.softeer.backend.fo_domain.user.domain;

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
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_phone_number", columnList = "phoneNumber")
        })
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "privacy_consent")
    private boolean privacyConsent;

    @Column(name = "marketing_consent")
    private boolean marketingConsent;
}
