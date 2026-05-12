package com.relic.converter;

import com.relic.entity.Artifact;
import com.relic.entity.ArtifactImage;
import com.relic.entity.Artist;
import com.relic.entity.Dynasty;
import com.relic.entity.Location;
import com.relic.entity.Museum;
import com.relic.entity.Permission;
import com.relic.entity.Role;
import com.relic.entity.User;
import com.relic.vo.ArtifactImageVO;
import com.relic.vo.ArtifactVO;
import com.relic.vo.ArtistVO;
import com.relic.vo.DynastyVO;
import com.relic.vo.LocationVO;
import com.relic.vo.LoginVO;
import com.relic.vo.MuseumVO;
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
                .userType(user.getUserType())
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
                .userType(user.getUserType())
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

    public static MuseumVO toMuseumVO(Museum museum) {
        return MuseumVO.builder()
                .id(museum.getId())
                .name(museum.getName())
                .shortName(museum.getShortName())
                .country(museum.getCountry())
                .city(museum.getCity())
                .website(museum.getWebsite())
                .collectionUrl(museum.getCollectionUrl())
                .createdAt(museum.getCreatedAt())
                .updatedAt(museum.getUpdatedAt())
                .build();
    }

    public static DynastyVO toDynastyVO(Dynasty dynasty) {
        return DynastyVO.builder()
                .id(dynasty.getId())
                .nameZh(dynasty.getNameZh())
                .nameEn(dynasty.getNameEn())
                .startYear(dynasty.getStartYear())
                .endYear(dynasty.getEndYear())
                .description(dynasty.getDescription())
                .createdAt(dynasty.getCreatedAt())
                .build();
    }

    public static ArtistVO toArtistVO(Artist artist) {
        return ArtistVO.builder()
                .id(artist.getId())
                .nameZh(artist.getNameZh())
                .nameEn(artist.getNameEn())
                .birthYear(artist.getBirthYear())
                .deathYear(artist.getDeathYear())
                .dynastyId(artist.getDynastyId())
                .biography(artist.getBiography())
                .baiduUrl(artist.getBaiduUrl())
                .wikiUrl(artist.getWikiUrl())
                .createdAt(artist.getCreatedAt())
                .updatedAt(artist.getUpdatedAt())
                .build();
    }

    public static LocationVO toLocationVO(Location location) {
        return LocationVO.builder()
                .id(location.getId())
                .nameZh(location.getNameZh())
                .nameEn(location.getNameEn())
                .type(location.getType())
                .parentId(location.getParentId())
                .createdAt(location.getCreatedAt())
                .build();
    }

    public static ArtifactVO toArtifactVO(Artifact artifact) {
        return ArtifactVO.builder()
                .id(artifact.getId())
                .objectId(artifact.getObjectId())
                .titleZh(artifact.getTitleZh())
                .titleEn(artifact.getTitleEn())
                .timePeriod(artifact.getTimePeriod())
                .dynastyId(artifact.getDynastyId())
                .type(artifact.getType())
                .material(artifact.getMaterial())
                .description(artifact.getDescription())
                .dimensions(artifact.getDimensions())
                .museumId(artifact.getMuseumId())
                .locationId(artifact.getLocationId())
                .detailUrl(artifact.getDetailUrl())
                .imageUrl(artifact.getImageUrl())
                .imagePath(artifact.getImagePath())
                .creditLine(artifact.getCreditLine())
                .accessionNumber(artifact.getAccessionNumber())
                .crawlDate(artifact.getCrawlDate())
                .imageValidated(artifact.getImageValidated())
                .lastUpdated(artifact.getLastUpdated())
                .createdAt(artifact.getCreatedAt())
                .build();
    }

    public static ArtifactImageVO toArtifactImageVO(ArtifactImage image) {
        return ArtifactImageVO.builder()
                .id(image.getId())
                .artifactId(image.getArtifactId())
                .imageUrl(image.getImageUrl())
                .imagePath(image.getImagePath())
                .isPrimary(image.getIsPrimary())
                .sortOrder(image.getSortOrder())
                .build();
    }
}