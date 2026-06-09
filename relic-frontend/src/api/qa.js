import request from './request'

// 问答日志
export const getQaLogs = (params) => request.get('/qa/logs', { params })

// 用户反馈
export const getQaFeedback = (params) => request.get('/qa/feedback', { params })

// 失败问题
export const getQaFailedQuestions = (params) => request.get('/qa/failed-questions', { params })

// 审核任务
export const getQaReviewTasks = (params) => request.get('/qa/review-tasks', { params })

// 处理审核任务
export const reviewQaTask = (id, data) => request.post(`/qa/review-tasks/${id}/review`, data)

// 统计 - 失败类型
export const getQaFailureTypeStats = () => request.get('/qa/statistics/failure-types')

// 统计 - 不准确类型
export const getQaInaccurateTypeStats = () => request.get('/qa/statistics/inaccurate-types')
