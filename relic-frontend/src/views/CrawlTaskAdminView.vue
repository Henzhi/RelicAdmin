<template>
    <div class="page-container">
        <el-card shadow="never">
            <template #header>
                <div class="card-header">
                    <span class="card-title">爬取任务管理</span>
                    <el-button type="primary" :icon="Plus" @click="openCreateDialog">添加任务</el-button>
                </div>
            </template>

            <div class="search-bar">
                <el-select v-model="searchStatus" placeholder="任务状态" clearable style="width:140px" @change="handleSearch">
                    <el-option label="空闲" value="idle" />
                    <el-option label="运行中" value="running" />
                    <el-option label="已暂停" value="paused" />
                    <el-option label="已完成" value="completed" />
                    <el-option label="失败" value="failed" />
                </el-select>
                <el-select v-model="searchPriority" placeholder="优先级" clearable style="width:120px" @change="handleSearch">
                    <el-option label="高" :value="10" />
                    <el-option label="中" :value="5" />
                    <el-option label="低" :value="1" />
                </el-select>
                <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
                <el-button :icon="Refresh" @click="handleReset">重置</el-button>
                <el-tag type="info" style="margin-left:auto">共 {{ pagination.total }} 条</el-tag>
            </div>

            <div v-loading="loading">
                <div v-if="tableData.length === 0 && !loading" class="empty-state">
                    <el-empty description="暂无爬取任务" />
                </div>
                <el-table v-else :data="tableData" border stripe style="width:100%">
                    <el-table-column prop="id" label="ID" width="60" />
                    <el-table-column prop="taskName" label="任务名称" min-width="160" />
                    <el-table-column prop="taskCode" label="编码" width="130" />
                    <el-table-column prop="sourceUrl" label="数据源URL" min-width="200" show-overflow-tooltip />
                    <el-table-column prop="sourceType" label="源类型" width="80">
                        <template #default="{ row }">
                            <el-tag v-if="row.sourceType === 'web'" size="small">Web</el-tag>
                            <el-tag v-else-if="row.sourceType === 'api'" type="success" size="small">API</el-tag>
                            <el-tag v-else-if="row.sourceType === 'rss'" type="warning" size="small">RSS</el-tag>
                            <span v-else>{{ row.sourceType }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="priority" label="优先级" width="85">
                        <template #default="{ row }">
                            <el-tag v-if="row.priority === 10" type="danger" size="small">高</el-tag>
                            <el-tag v-else-if="row.priority === 5" type="warning" size="small">中</el-tag>
                            <el-tag v-else type="info" size="small">低</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="状态" width="100">
                        <template #default="{ row }">
                            <el-tag v-if="row.status === 'idle'" type="success" size="small">空闲</el-tag>
                            <el-tag v-else-if="row.status === 'running'" type="warning" size="small" effect="dark">运行中</el-tag>
                            <el-tag v-else-if="row.status === 'paused'" type="info" size="small">已暂停</el-tag>
                            <el-tag v-else-if="row.status === 'failed'" type="danger" size="small">失败</el-tag>
                            <el-tag v-else-if="row.status === 'completed'" size="small">已完成</el-tag>
                            <span v-else>{{ row.status }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="cronExpression" label="定时表达式" width="140" show-overflow-tooltip />
                    <el-table-column label="执行统计" width="130">
                        <template #default="{ row }">
                            <span style="color:#67c23a">{{ row.successRuns }}</span>
                            /
                            <span>{{ row.totalRuns }}</span>
                            <span v-if="row.failRuns > 0" style="color:#f56c6c"> ({{ row.failRuns }}失败)</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="lastRunTime" label="上次执行" width="160" />
                    <el-table-column label="操作" width="280" fixed="right">
                        <template #default="{ row }">
                            <el-button size="small" link type="primary" @click="openEditDialog(row)">编辑</el-button>
                            <el-button v-if="row.status !== 'running'" size="small" link type="success" @click="handleExecute(row)">执行</el-button>
                            <el-button v-if="row.status === 'running'" size="small" link type="warning" @click="handlePause(row)">暂停</el-button>
                            <el-button v-if="row.status === 'paused'" size="small" link type="primary" @click="handleResume(row)">恢复</el-button>
                            <el-button size="small" link type="info" @click="openLogDialog(row)">日志</el-button>
                            <el-button size="small" link type="danger" @click="handleDelete(row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>

            <div class="pagination-container" v-if="tableData.length > 0">
                <el-pagination
                    v-model:current-page="pagination.page"
                    v-model:page-size="pagination.pageSize"
                    :page-sizes="[10,20,50]"
                    :total="pagination.total"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handleSearch"
                    @current-change="handlePageChange"
                />
            </div>
        </el-card>

        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑爬取任务' : '添加爬取任务'"
                   width="620px" :close-on-click-modal="false" @closed="resetForm">
            <el-form :model="form" label-width="110px">
                <el-row :gutter="16">
                    <el-col :span="12">
                        <el-form-item label="任务名称">
                            <el-input v-model="form.taskName" placeholder="任务显示名称" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="任务编码">
                            <el-input v-model="form.taskCode" placeholder="唯一编码" :disabled="isEdit" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="数据源URL">
                    <el-input v-model="form.sourceUrl" placeholder="目标网址或API地址" />
                </el-form-item>
                <el-row :gutter="16">
                    <el-col :span="12">
                        <el-form-item label="数据源类型">
                            <el-select v-model="form.sourceType" style="width:100%">
                                <el-option label="Web网页" value="web" />
                                <el-option label="API接口" value="api" />
                                <el-option label="RSS源" value="rss" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="优先级">
                            <el-select v-model="form.priority" style="width:100%">
                                <el-option label="高" :value="10" />
                                <el-option label="中" :value="5" />
                                <el-option label="低" :value="1" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="定时表达式">
                    <el-input v-model="form.cronExpression" placeholder="为空则不启用定时，如: 0 0 9 * * ?" />
                </el-form-item>
                <el-row :gutter="16">
                    <el-col :span="8">
                        <el-form-item label="最大重试">
                            <el-input-number v-model="form.maxRetry" :min="0" :max="10" style="width:100%" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="重试间隔(秒)">
                            <el-input-number v-model="form.retryDelay" :min="0" :max="3600" style="width:100%" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="超时(秒)">
                            <el-input-number v-model="form.timeoutSeconds" :min="10" :max="3600" style="width:100%" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="爬取规则">
                    <el-input v-model="form.crawlRule" type="textarea" :rows="3" placeholder="JSON格式爬取规则配置" />
                </el-form-item>
                <el-form-item label="任务描述">
                    <el-input v-model="form.description" type="textarea" :rows="2" placeholder="任务描述说明" />
                </el-form-item>
                <el-form-item label="启用状态" v-if="isEdit">
                    <el-radio-group v-model="form.enabled">
                        <el-radio :value="1">启用</el-radio>
                        <el-radio :value="0">禁用</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" :loading="submitLoading" @click="submitForm">{{ isEdit ? '保存' : '添加' }}</el-button>
            </template>
        </el-dialog>

        <el-drawer v-model="logDrawerVisible" title="任务执行日志" size="700px">
            <div class="log-search-bar">
                <el-select v-model="logSearchStatus" placeholder="执行状态" clearable style="width:130px" @change="fetchLogs">
                    <el-option label="运行中" value="running" />
                    <el-option label="成功" value="success" />
                    <el-option label="失败" value="failed" />
                </el-select>
                <el-button type="primary" size="small" :icon="Search" @click="fetchLogs">查询</el-button>
                <el-tag type="info" size="small" style="margin-left:auto">共 {{ logPagination.total }} 条</el-tag>
            </div>
            <div v-loading="logLoading">
                <div v-if="logData.length === 0 && !logLoading" style="text-align:center;padding:40px">
                    <el-empty description="暂无执行日志" />
                </div>
                <el-table v-else :data="logData" border stripe size="small">
                    <el-table-column prop="id" label="ID" width="60" />
                    <el-table-column prop="startTime" label="开始时间" width="160" />
                    <el-table-column prop="endTime" label="结束时间" width="160" />
                    <el-table-column label="状态" width="80">
                        <template #default="{ row: lr }">
                            <el-tag v-if="lr.status === 'success'" type="success" size="small">成功</el-tag>
                            <el-tag v-else-if="lr.status === 'failed'" type="danger" size="small">失败</el-tag>
                            <el-tag v-else-if="lr.status === 'running'" type="warning" size="small">运行中</el-tag>
                            <span v-else>{{ lr.status }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="crawledCount" label="爬取数量" width="90" />
                    <el-table-column prop="retryCount" label="重试次数" width="90" />
                    <el-table-column prop="errorMessage" label="错误信息" min-width="180" show-overflow-tooltip />
                </el-table>
            </div>
            <div class="pagination-container" v-if="logData.length > 0">
                <el-pagination
                    v-model:current-page="logPagination.page"
                    v-model:page-size="logPagination.pageSize"
                    :page-sizes="[10,20,50]"
                    :total="logPagination.total"
                    layout="total, prev, pager, next"
                    size="small"
                    @size-change="fetchLogs"
                    @current-change="fetchLogs"
                />
            </div>
        </el-drawer>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getCrawlTaskPage, getCrawlTaskDetail, createCrawlTask, updateCrawlTask,
         executeCrawlTask, pauseCrawlTask, resumeCrawlTask, deleteCrawlTask,
         getCrawlTaskLogs } from '../api/crawlTaskAdmin'

const loading = ref(false)
const tableData = ref([])
const searchStatus = ref(null)
const searchPriority = ref(null)
const pagination = reactive({ page:1, pageSize:10, total:0 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitLoading = ref(false)
const form = reactive({
    taskName:'', taskCode:'', sourceUrl:'', sourceType:'web',
    crawlRule:'', priority:5, cronExpression:'', maxRetry:3,
    retryDelay:60, timeoutSeconds:300, description:'', enabled:1
})

const logDrawerVisible = ref(false)
const logData = ref([])
const logLoading = ref(false)
const logSearchStatus = ref(null)
const currentLogTaskId = ref(null)
const logPagination = reactive({ page:1, pageSize:10, total:0 })

onMounted(() => { fetchData() })

async function fetchData() {
    loading.value = true
    try {
        const res = await getCrawlTaskPage({
            page: pagination.page, pageSize: pagination.pageSize,
            status: searchStatus.value, priority: searchPriority.value
        })
        tableData.value = res.data.records
        pagination.total = res.data.total
    } catch {
        ElMessage.error('加载爬取任务列表失败')
        tableData.value = []
    } finally {
        loading.value = false
    }
}

function handleSearch() { pagination.page = 1; fetchData() }
function handlePageChange(page) { pagination.page = page; fetchData() }
function handleReset() { searchStatus.value = null; searchPriority.value = null; handleSearch() }

function resetForm() {
    form.taskName = ''; form.taskCode = ''; form.sourceUrl = ''; form.sourceType = 'web';
    form.crawlRule = ''; form.priority = 5; form.cronExpression = ''; form.maxRetry = 3;
    form.retryDelay = 60; form.timeoutSeconds = 300; form.description = ''; form.enabled = 1;
    isEdit.value = false; editId.value = null;
}

function openCreateDialog() { resetForm(); dialogVisible.value = true }

function openEditDialog(row) {
    isEdit.value = true; editId.value = row.id;
    form.taskName = row.taskName; form.taskCode = row.taskCode;
    form.sourceUrl = row.sourceUrl; form.sourceType = row.sourceType;
    form.crawlRule = row.crawlRule; form.priority = row.priority;
    form.cronExpression = row.cronExpression; form.maxRetry = row.maxRetry;
    form.retryDelay = row.retryDelay; form.timeoutSeconds = row.timeoutSeconds;
    form.description = row.description; form.enabled = row.enabled;
    dialogVisible.value = true;
}

async function submitForm() {
    if (!form.taskName.trim()) { ElMessage.warning('请输入任务名称'); return }
    if (!form.taskCode.trim() && !isEdit.value) { ElMessage.warning('请输入任务编码'); return }
    if (!form.sourceUrl.trim()) { ElMessage.warning('请输入数据源URL'); return }
    submitLoading.value = true
    try {
        if (isEdit.value) {
            await updateCrawlTask(editId.value, {
                taskName: form.taskName, sourceUrl: form.sourceUrl,
                sourceType: form.sourceType, crawlRule: form.crawlRule,
                priority: form.priority, cronExpression: form.cronExpression,
                maxRetry: form.maxRetry, retryDelay: form.retryDelay,
                timeoutSeconds: form.timeoutSeconds, description: form.description,
                enabled: form.enabled
            })
            ElMessage.success('已更新')
        } else {
            await createCrawlTask({
                taskName: form.taskName, taskCode: form.taskCode,
                sourceUrl: form.sourceUrl, sourceType: form.sourceType,
                crawlRule: form.crawlRule, priority: form.priority,
                cronExpression: form.cronExpression, maxRetry: form.maxRetry,
                retryDelay: form.retryDelay, timeoutSeconds: form.timeoutSeconds,
                description: form.description
            })
            ElMessage.success('已添加')
        }
        dialogVisible.value = false; fetchData()
    } catch (err) {
        ElMessage.error(err.message || '操作失败')
    } finally {
        submitLoading.value = false
    }
}

async function handleExecute(row) {
    try {
        await ElMessageBox.confirm(`确定要执行任务「${row.taskName}」吗？`, '确认执行', { type:'info' })
        await executeCrawlTask(row.id)
        ElMessage.success('任务执行成功')
        fetchData()
    } catch {}
}

async function handlePause(row) {
    try {
        await pauseCrawlTask(row.id)
        ElMessage.success('任务已暂停')
        fetchData()
    } catch (err) {
        ElMessage.error(err.message || '操作失败')
    }
}

async function handleResume(row) {
    try {
        await resumeCrawlTask(row.id)
        ElMessage.success('任务已恢复')
        fetchData()
    } catch (err) {
        ElMessage.error(err.message || '操作失败')
    }
}

function openLogDialog(row) {
    currentLogTaskId.value = row.id
    logSearchStatus.value = null
    logPagination.page = 1
    logDrawerVisible.value = true
    fetchLogs()
}

async function fetchLogs() {
    logLoading.value = true
    try {
        const res = await getCrawlTaskLogs({
            page: logPagination.page, pageSize: logPagination.pageSize,
            taskId: currentLogTaskId.value, status: logSearchStatus.value
        })
        logData.value = res.data.records
        logPagination.total = res.data.total
    } catch {
        ElMessage.error('加载日志失败')
        logData.value = []
    } finally {
        logLoading.value = false
    }
}

async function handleDelete(row) {
    try {
        await ElMessageBox.confirm(`确定要删除任务「${row.taskName}」吗？相关日志也会一并删除。`, '确认删除', { type:'warning' })
        await deleteCrawlTask(row.id)
        ElMessage.success('已删除')
        fetchData()
    } catch {}
}

setInterval(() => {
    const hasRunning = tableData.value.some(r => r.status === 'running')
    if (hasRunning) fetchData()
}, 10000)
</script>

<style scoped>
.page-container { padding:20px }
.card-header { display:flex; justify-content:space-between; align-items:center }
.card-title { font-size:18px; font-weight:600 }
.search-bar { margin-bottom:16px; display:flex; align-items:center; gap:8px; flex-wrap:wrap }
.pagination-container { display:flex; justify-content:flex-end; margin-top:16px }
.empty-state { display:flex; flex-direction:column; align-items:center; padding:40px 0; gap:16px }
.log-search-bar { margin-bottom:12px; display:flex; align-items:center; gap:8px }
</style>