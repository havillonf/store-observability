package com.store.observability.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_interactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String service;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private Long timestamp;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
