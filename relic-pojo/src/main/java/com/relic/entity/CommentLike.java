package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentLike {
    private Integer userId;
    private Integer commentId;
    private LocalDateTime likedAt;
}