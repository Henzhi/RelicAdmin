package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLike {
    private Integer userId;
    private Integer postId;
    private LocalDateTime likedAt;
}