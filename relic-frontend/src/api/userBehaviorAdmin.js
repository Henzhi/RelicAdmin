import adminApi from './request'

export function getFavoritesPage(params) {
  return adminApi.get('/user-behavior/favorites/page', { params })
}

export function getArtifactLikesPage(params) {
  return adminApi.get('/user-behavior/artifact-likes/page', { params })
}

export function getPostLikesPage(params) {
  return adminApi.get('/user-behavior/post-likes/page', { params })
}

export function getCommentLikesPage(params) {
  return adminApi.get('/user-behavior/comment-likes/page', { params })
}

export function getPostsPage(params) {
  return adminApi.get('/user-behavior/posts/page', { params })
}

export function getCommentsPage(params) {
  return adminApi.get('/user-behavior/comments/page', { params })
}

export function getFollowsPage(params) {
  return adminApi.get('/user-behavior/follows/page', { params })
}

export function getUploadsPage(params) {
  return adminApi.get('/user-behavior/uploads/page', { params })
}

export function getBrowseHistoryPage(params) {
  return adminApi.get('/user-behavior/browse-history/page', { params })
}

export function getBehaviorLogsPage(params) {
  return adminApi.get('/user-behavior/behavior-logs/page', { params })
}