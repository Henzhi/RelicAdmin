import userApi from './requestUser'

export function likeArtifact(artifactId) {
  return userApi.post('/like/artifact/' + artifactId)
}

export function unlikeArtifact(artifactId) {
  return userApi.delete('/like/artifact/' + artifactId)
}

export function likeComment(commentId) {
  return userApi.post('/like/comment/' + commentId)
}

export function unlikeComment(commentId) {
  return userApi.delete('/like/comment/' + commentId)
}

export function likePost(postId) {
  return userApi.post('/like/post/' + postId)
}

export function unlikePost(postId) {
  return userApi.delete('/like/post/' + postId)
}