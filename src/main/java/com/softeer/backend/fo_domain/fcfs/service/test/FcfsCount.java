package com.softeer.backend.fo_domain.fcfs.service.test;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "fcfs_count")
public class FcfsCount {

    @Id
    @Column(name = "fcfs_count_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "round")
    private int round;

    @Column(name = "fcfs_num")
    private int fcfsNum;
}
