package com.relic.converter;

import com.relic.entity.Permission;
import com.relic.entity.Role;
import com.relic.entity.User;
import com.relic.vo.LoginVO;
import com.relic.vo.PermissionVO;
import com.relic.vo.RoleVO;
import com.relic.vo.UserVO;

public class VoConverter {

    public static LoginVO toLoginVO(User user) {
        return LoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    public static UserVO toUserVO(User user) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .nickname(user.getNickname())
                .status(user.getStatus())
                .banReason(user.getBanReason())
                .registeredAt(user.getRegisteredAt())
                .lastLogin(user.getLastLogin())
                .lastIp(user.getLastIp())
                .source(user.getSource())
                .commentDisabled(user.getCommentDisabled())
                .uploadDisabled(user.getUploadDisabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static RoleVO toRoleVO(Role role) {
        return RoleVO.builder()
                .id(role.getId())
                .name(role.getName())
                .displayName(role.getDisplayName())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }

    public static PermissionVO toPermissionVO(Permission permission) {
        return PermissionVO.builder()
                .id(permission.getId())
                .name(permission.getName())
                .displayName(permission.getDisplayName())
                .module(permission.getModule())
                .createdAt(permission.getCreatedAt())
                .build();
    }
}