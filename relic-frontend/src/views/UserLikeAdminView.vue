<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户喜欢与点赞</span>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="searchUsername" placeholder="用户名搜索" clearable style="width:160px" @keyup.enter="handleCommonSearch" />
        <el-date-picker v-model="dateRange" type="datetimerange" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width:360px" clearable @change="handleCommonSearch" />
        <el-button type="primary" @click="handleCommonSearch">查询</el-button>
        <el-button @click="handleCommonReset">重置</el-button>
      </div>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="文物喜欢" name="artifact">
          <el-form :inline="true" :model="artifactFilter" class="filter-form">
            <el-form-item label="用户ID">
              <el-input v-model="artifactFilter.userId" placeholder="请输入用户ID" clearable
                @keyup.enter="handleArtifactSearch" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleArtifactSearch">查询</el-button>
              <el-button @click="resetArtifactFilter">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="artifactTableData" v-loading="artifactLoading" stripe border>
            <el-table-column prop="username" label="用户名" min-width="150" show-overflow-tooltip />
            <el-table-column prop="artifactName" label="文物名称" min-width="200" show-overflow-tooltip />
            <el-table-column prop="likedAt" label="喜欢时间" width="180" />
          </el-table>

          <div class="pagination-wrap">
            <el-pagination
              v-model:current-page="artifactPagination.page"
              v-model:page-size="artifactPagination.pageSize"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next"
              :total="artifactPagination.total"
              @size-change="handleArtifactSearch"
              @current-change="handleArtifactPageChange"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="动态点赞" name="post">
          <el-form :inline="true" :model="postFilter" class="filter-form">
            <el-form-item label="用户ID">
              <el-input v-model="postFilter.userId" placeholder="请输入用户ID" clearable
                @keyup.enter="handlePostSearch" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handlePostSearch">查询</el-button>
              <el-button @click="resetPostFilter">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="postTableData" v-loading="postLoading" stripe border>
            <el-table-column prop="username" label="用户名" min-width="150" show-overflow-tooltip />
            <el-table-column prop="postTitle" label="动态标题" min-width="250" show-overflow-tooltip />
            <el-table-column prop="likedAt" label="点赞时间" width="180" />
          </el-table>

          <div class="pagination-wrap">
            <el-pagination
              v-model:current-page="postPagination.page"
              v-model:page-size="postPagination.pageSize"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next"
              :total="postPagination.total"
              @size-change="handlePostSearch"
              @current-change="handlePostPageChange"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="评论点赞" name="comment">
          <el-form :inline="true" :model="commentFilter" class="filter-form">
            <el-form-item label="用户ID">
              <el-input v-model="commentFilter.userId" placeholder="请输入用户ID" clearable
                @keyup.enter="handleCommentSearch" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleCommentSearch">查询</el-button>
              <el-button @click="resetCommentFilter">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="commentTableData" v-loading="commentLoading" stripe border>
            <el-table-column prop="username" label="用户名" min-width="150" show-overflow-tooltip />
            <el-table-column prop="commentContent" label="评论内容" min-width="300" show-overflow-tooltip />
            <el-table-column prop="likedAt" label="点赞时间" width="180" />
          </el-table>

          <div class="pagination-wrap">
            <el-pagination
              v-model:current-page="commentPagination.page"
              v-model:page-size="commentPagination.pageSize"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next"
              :total="commentPagination.total"
              @size-change="handleCommentSearch"
              @current-change="handleCommentPageChange"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getArtifactLikesPage, getPostLikesPage, getCommentLikesPage } from '../api/userBehaviorAdmin'

const activeTab = ref('artifact')

const searchUsername = ref('')
const dateRange = ref(null)

const artifactLoading = ref(false)
const artifactTableData = ref([])
const artifactPagination = reactive({ page: 1, pageSize: 10, total: 0 })
const artifactFilter = reactive({ userId: '' })

const postLoading = ref(false)
const postTableData = ref([])
const postPagination = reactive({ page: 1, pageSize: 10, total: 0 })
const postFilter = reactive({ userId: '' })

const commentLoading = ref(false)
const commentTableData = ref([])
const commentPagination = reactive({ page: 1, pageSize: 10, total: 0 })
const commentFilter = reactive({ userId: '' })

async function fetchArtifactLikes() {
  artifactLoading.value = true
  try {
    const startTime = dateRange.value?.[0] ? dateRange.value[0] : undefined
    const endTime = dateRange.value?.[1] ? dateRange.value[1] : undefined
    const res = await getArtifactLikesPage({
      page: artifactPagination.page,
      pageSize: artifactPagination.pageSize,
      userId: artifactFilter.userId || undefined,
      username: searchUsername.value || undefined,
      startTime,
      endTime
    })
    artifactTableData.value = res.data.records
    artifactPagination.total = res.data.total
  } catch {
  } finally {
    artifactLoading.value = false
  }
}

function handleArtifactSearch() {
  artifactPagination.page = 1
  fetchArtifactLikes()
}

function handleArtifactPageChange(page) {
  artifactPagination.page = page
  fetchArtifactLikes()
}

function resetArtifactFilter() {
  artifactFilter.userId = ''
  handleArtifactSearch()
}

async function fetchPostLikes() {
  postLoading.value = true
  try {
    const startTime = dateRange.value?.[0] ? dateRange.value[0] : undefined
    const endTime = dateRange.value?.[1] ? dateRange.value[1] : undefined
    const res = await getPostLikesPage({
      page: postPagination.page,
      pageSize: postPagination.pageSize,
      userId: postFilter.userId || undefined,
      username: searchUsername.value || undefined,
      startTime,
      endTime
    })
    postTableData.value = res.data.records
    postPagination.total = res.data.total
  } catch {
  } finally {
    postLoading.value = false
  }
}

function handlePostSearch() {
  postPagination.page = 1
  fetchPostLikes()
}

function handlePostPageChange(page) {
  postPagination.page = page
  fetchPostLikes()
}

function resetPostFilter() {
  postFilter.userId = ''
  handlePostSearch()
}

async function fetchCommentLikes() {
  commentLoading.value = true
  try {
    const startTime = dateRange.value?.[0] ? dateRange.value[0] : undefined
    const endTime = dateRange.value?.[1] ? dateRange.value[1] : undefined
    const res = await getCommentLikesPage({
      page: commentPagination.page,
      pageSize: commentPagination.pageSize,
      userId: commentFilter.userId || undefined,
      username: searchUsername.value || undefined,
      startTime,
      endTime
    })
    commentTableData.value = res.data.records
    commentPagination.total = res.data.total
  } catch {
  } finally {
    commentLoading.value = false
  }
}

function handleCommentSearch() {
  commentPagination.page = 1
  fetchCommentLikes()
}

function handleCommentPageChange(page) {
  commentPagination.page = page
  fetchCommentLikes()
}

function resetCommentFilter() {
  commentFilter.userId = ''
  handleCommentSearch()
}

function handleCommonSearch() {
  if (activeTab.value === 'artifact') {
    artifactPagination.page = 1
    fetchArtifactLikes()
  } else if (activeTab.value === 'post') {
    postPagination.page = 1
    fetchPostLikes()
  } else if (activeTab.value === 'comment') {
    commentPagination.page = 1
    fetchCommentLikes()
  }
}

function handleCommonReset() {
  searchUsername.value = ''
  dateRange.value = null
  artifactFilter.userId = ''
  postFilter.userId = ''
  commentFilter.userId = ''
  handleCommonSearch()
}

function handleTabChange(tabName) {
  if (tabName === 'artifact' && artifactTableData.value.length === 0) {
    fetchArtifactLikes()
  } else if (tabName === 'post' && postTableData.value.length === 0) {
    fetchPostLikes()
  } else if (tabName === 'comment' && commentTableData.value.length === 0) {
    fetchCommentLikes()
  }
}

onMounted(() => {
  fetchArtifactLikes()
})
</script>

<style scoped>
.card-header {
  font-weight: bold;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.filter-form {
  margin-bottom: 16px;
}
.search-bar {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>