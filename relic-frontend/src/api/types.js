/**
 * 后端响应结构 JSDoc 类型约定。
 *
 * 后端统一返回 `Result` 包装（com.relic.result.Result），
 * request.js / requestUser.js 拦截器在 code===200 时解包并返回整个 `Result` 对象，
 * 调用方通过 `res.data`（列表接口为 `res.data.records / res.data.total`）取业务数据。
 *
 * 各 api/*.js 导出函数请按此约定补充 @returns JSDoc，获得编辑器提示；
 * 后续如迁移 TypeScript，可按此文件的定义直接转为 interface。
 */

/**
 * 后端统一响应包装。
 * @typedef {Object} Result
 * @property {number} code 200 表示成功，其余为失败（失败时拦截器已弹出 msg 并 reject）
 * @property {string} msg 提示信息
 * @property {*} data 业务数据
 */

/**
 * 分页数据结构（PageResultVO）。
 * @typedef {Object} PageResult
 * @property {number} total 总记录数
 * @property {Array<{id: number|string}>} records 当前页记录列表
 */

/**
 * 仪表盘统计汇总。
 * @typedef {Object} DashboardOverview
 * @property {number} onlineUsers 当前在线用户（15分钟活跃）
 * @property {number} todayActiveUsers 当日活跃用户
 * @property {number} todayNewUsers 当日新增用户
 * @property {number} todayContentCount 当日新增内容数
 * @property {number} auditBacklog 待审核积压数
 * @property {number} total_users 用户总数
 * @property {number} total_artifacts 文物总数
 * @property {number} total_museums 博物馆总数
 * @property {number} total_comments 评论总数
 */

export {}
