import adminApi from './request'

export function getFollowPage(params) {
  return adminApi.get('/follow/page', { params })
}

export function deleteFollow(followerId, followeeId) {
  return adminApi.delete(`/follow/${followerId}/${followeeId}`)
}