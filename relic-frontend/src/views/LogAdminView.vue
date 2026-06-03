<template>
    <div class="page-container">
        <el-card shadow="never">
            <template #header>
                <div class="card-header">
                    <span class="card-title">日志管理</span>
                </div>
            </template>

            <el-tabs v-model="activeTab" @tab-change="onTabChange">
                <el-tab-pane label="操作日志" name="operation">
                    <div class="search-bar">
                        <el-input v-model="opSearch.keyword" placeholder="关键字搜索" clearable style="width:180px" @clear="handleOpSearch" @keyup.enter="handleOpSearch" />
                        <el-select v-model="opSearch.operationType" placeholder="操作类型" clearable style="width:130px" @change="handleOpSearch">
                            <el-option label="新增" value="INSERT" />
                            <el-option label="更新" value="UPDATE" />
                            <el-option label="删除" value="DELETE" />
                        </el-select>
                        <el-select v-model="opSearch.targetType" placeholder="目标模块" clearable style="width:150px" @change="handleOpSearch">
                            <el-option label="公告" value="Announcement" />
                            <el-option label="违规类型" value="ViolationType" />
                            <el-option label="敏感词" value="SensitiveWord" />
                            <el-option label="爬取任务" value="CrawlTask" />
                            <el-option label="系统配置" value="SystemConfig" />
                            <el-option label="审核策略" value="AuditStrategy" />
                            <el-option label="备份" value="Backup" />
                            <el-option label="恢复" value="Restore" />
                        </el-select>
                        <el-date-picker v-model="opSearch.dateRange" type="datetimerange" range-separator="至"
                            start-placeholder="开始时间" end-placeholder="结束时间" value-format="YYYY-MM-DD HH:mm:ss"
                            style="width:350px" @change="handleOpSearch" />
                        <el-button type="primary" :icon="Search" @click="handleOpSearch">查询</el-button>
                        <el-button :icon="Refresh" @click="handleOpReset">重置</el-button>
                        <el-tag type="info" style="margin-left:auto">共 {{ opPagination.total }} 条</el-tag>
                        <el-button type="success" :icon="Download" size="small" :loading="opExportingCsv" @click="exportOpCSV">CSV</el-button>
                        <el-button type="warning" :icon="Download" size="small" :loading="opExportingExcel" @click="exportOpExcel">Excel</el-button>
                    </div>
                    <div v-loading="opLoading">
                        <div v-if="opData.length === 0 && !opLoading" class="empty-state">
                            <el-empty description="暂无操作日志" />
                        </div>
                        <el-table v-else :data="opData" border stripe size="small">
                            <el-table-column prop="id" label="ID" width="60" />
                            <el-table-column prop="operatorName" label="操作人" width="120" />
                            <el-table-column label="操作类型" width="90">
                                <template #default="{ row }">
                                    <el-tag v-if="row.operationType === 'INSERT'" type="success" size="small">新增</el-tag>
                                    <el-tag v-else-if="row.operationType === 'UPDATE'" type="warning" size="small">更新</el-tag>
                                    <el-tag v-else-if="row.operationType === 'DELETE'" type="danger" size="small">删除</el-tag>
                                    <span v-else>{{ row.operationType }}</span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="targetType" label="目标模块" width="110" />
                            <el-table-column prop="targetId" label="目标ID" width="80" />
                            <el-table-column prop="ip" label="IP地址" width="140" />
                            <el-table-column prop="newValue" label="操作详情" min-width="220" show-overflow-tooltip />
                            <el-table-column prop="createdAt" label="操作时间" width="160" />
                        </el-table>
                    </div>
                    <div class="pagination-container" v-if="opData.length > 0">
                        <el-pagination
          background v-model:current-page="opPagination.page" v-model:page-size="opPagination.pageSize"
                            :page-sizes="[10,20,50]" :total="opPagination.total" layout="total, sizes, prev, pager, next, jumper"
                            @size-change="handleOpSearch" @current-change="handleOpPageChange" />
                    </div>
                </el-tab-pane>

                <el-tab-pane label="系统日志" name="system">
                    <div class="search-bar">
                        <el-input v-model="sysSearch.keyword" placeholder="关键字搜索" clearable style="width:180px" @clear="handleSysSearch" @keyup.enter="handleSysSearch" />
                        <el-select v-model="sysSearch.logLevel" placeholder="日志级别" clearable style="width:120px" @change="handleSysSearch">
                            <el-option label="DEBUG" value="DEBUG" />
                            <el-option label="INFO" value="INFO" />
                            <el-option label="WARN" value="WARN" />
                            <el-option label="ERROR" value="ERROR" />
                        </el-select>
                        <el-select v-model="sysSearch.module" placeholder="模块" clearable style="width:140px" @change="handleSysSearch">
                            <el-option label="Crawler" value="Crawler" />
                            <el-option label="Backup" value="Backup" />
                            <el-option label="LogAspect" value="LogAspect" />
                            <el-option label="Auth" value="Auth" />
                            <el-option label="System" value="System" />
                        </el-select>
                        <el-date-picker v-model="sysSearch.dateRange" type="datetimerange" range-separator="至"
                            start-placeholder="开始时间" end-placeholder="结束时间" value-format="YYYY-MM-DD HH:mm:ss"
                            style="width:350px" @change="handleSysSearch" />
                        <el-button type="primary" :icon="Search" @click="handleSysSearch">查询</el-button>
                        <el-button :icon="Refresh" @click="handleSysReset">重置</el-button>
                        <el-tag type="info" style="margin-left:auto">共 {{ sysPagination.total }} 条</el-tag>
                        <el-button type="success" :icon="Download" size="small" :loading="sysExportingCsv" @click="exportSysCSV">CSV</el-button>
                        <el-button type="warning" :icon="Download" size="small" :loading="sysExportingExcel" @click="exportSysExcel">Excel</el-button>
                    </div>
                    <div v-loading="sysLoading">
                        <div v-if="sysData.length === 0 && !sysLoading" class="empty-state">
                            <el-empty description="暂无系统日志" />
                        </div>
                        <el-table v-else :data="sysData" border stripe size="small">
                            <el-table-column prop="id" label="ID" width="60" />
                            <el-table-column label="级别" width="80">
                                <template #default="{ row }">
                                    <el-tag v-if="row.logLevel === 'DEBUG'" size="small">DEBUG</el-tag>
                                    <el-tag v-else-if="row.logLevel === 'INFO'" type="success" size="small">INFO</el-tag>
                                    <el-tag v-else-if="row.logLevel === 'WARN'" type="warning" size="small">WARN</el-tag>
                                    <el-tag v-else-if="row.logLevel === 'ERROR'" type="danger" size="small">ERROR</el-tag>
                                    <span v-else>{{ row.logLevel }}</span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="module" label="模块" width="110" />
                            <el-table-column prop="message" label="消息" min-width="200" show-overflow-tooltip />
                            <el-table-column prop="detail" label="详细信息" min-width="250" show-overflow-tooltip />
                            <el-table-column prop="createdAt" label="发生时间" width="160" />
                        </el-table>
                    </div>
                    <div class="pagination-container" v-if="sysData.length > 0">
                        <el-pagination
          background v-model:current-page="sysPagination.page" v-model:page-size="sysPagination.pageSize"
                            :page-sizes="[10,20,50]" :total="sysPagination.total" layout="total, sizes, prev, pager, next, jumper"
                            @size-change="handleSysSearch" @current-change="handleSysPageChange" />
                    </div>
                </el-tab-pane>

                <el-tab-pane label="安全日志" name="security">
                    <div class="search-bar">
                        <el-input v-model="secSearch.keyword" placeholder="关键字搜索" clearable style="width:180px" @clear="handleSecSearch" @keyup.enter="handleSecSearch" />
                        <el-select v-model="secSearch.eventType" placeholder="事件类型" clearable style="width:140px" @change="handleSecSearch">
                            <el-option label="登录成功" value="LOGIN_SUCCESS" />
                            <el-option label="登录失败" value="LOGIN_FAILED" />
                            <el-option label="权限变更" value="PERMISSION_CHANGE" />
                            <el-option label="敏感访问" value="SENSITIVE_ACCESS" />
                            <el-option label="密码修改" value="PASSWORD_CHANGE" />
                            <el-option label="令牌过期" value="TOKEN_EXPIRED" />
                        </el-select>
                        <el-date-picker v-model="secSearch.dateRange" type="datetimerange" range-separator="至"
                            start-placeholder="开始时间" end-placeholder="结束时间" value-format="YYYY-MM-DD HH:mm:ss"
                            style="width:350px" @change="handleSecSearch" />
                        <el-button type="primary" :icon="Search" @click="handleSecSearch">查询</el-button>
                        <el-button :icon="Refresh" @click="handleSecReset">重置</el-button>
                        <el-tag type="info" style="margin-left:auto">共 {{ secPagination.total }} 条</el-tag>
                        <el-button type="success" :icon="Download" size="small" :loading="secExportingCsv" @click="exportSecCSV">CSV</el-button>
                        <el-button type="warning" :icon="Download" size="small" :loading="secExportingExcel" @click="exportSecExcel">Excel</el-button>
                    </div>
                    <div v-loading="secLoading">
                        <div v-if="secData.length === 0 && !secLoading" class="empty-state">
                            <el-empty description="暂无安全日志" />
                        </div>
                        <el-table v-else :data="secData" border stripe size="small">
                            <el-table-column prop="id" label="ID" width="60" />
                            <el-table-column prop="userName" label="用户" width="110" />
                            <el-table-column label="事件类型" width="110">
                                <template #default="{ row }">
                                    <el-tag v-if="row.eventType === 'LOGIN_SUCCESS'" type="success" size="small">登录成功</el-tag>
                                    <el-tag v-else-if="row.eventType === 'LOGIN_FAILED'" type="danger" size="small">登录失败</el-tag>
                                    <el-tag v-else-if="row.eventType === 'PERMISSION_CHANGE'" type="warning" size="small">权限变更</el-tag>
                                    <el-tag v-else-if="row.eventType === 'SENSITIVE_ACCESS'" type="danger" size="small">敏感访问</el-tag>
                                    <el-tag v-else-if="row.eventType === 'PASSWORD_CHANGE'" type="info" size="small">密码修改</el-tag>
                                    <el-tag v-else-if="row.eventType === 'TOKEN_EXPIRED'" type="warning" size="small">令牌过期</el-tag>
                                    <span v-else>{{ row.eventType }}</span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="ip" label="IP地址" width="140" />
                            <el-table-column prop="detail" label="详细信息" min-width="250" show-overflow-tooltip />
                            <el-table-column prop="createdAt" label="发生时间" width="160" />
                        </el-table>
                    </div>
                    <div class="pagination-container" v-if="secData.length > 0">
                        <el-pagination
          background v-model:current-page="secPagination.page" v-model:page-size="secPagination.pageSize"
                            :page-sizes="[10,20,50]" :total="secPagination.total" layout="total, sizes, prev, pager, next, jumper"
                            @size-change="handleSecSearch" @current-change="handleSecPageChange" />
                    </div>
                </el-tab-pane>
            </el-tabs>
        </el-card>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Download } from '@element-plus/icons-vue'
import {
    getOperationLogPage, exportOperationLogCSV, exportOperationLogExcel,
    getSystemLogPage, exportSystemLogCSV, exportSystemLogExcel,
    getSecurityLogPage, exportSecurityLogCSV, exportSecurityLogExcel
} from '../api/logAdmin'

const activeTab = ref('operation')

const opLoading = ref(false)
const opData = ref([])
const opExportingCsv = ref(false)
const opExportingExcel = ref(false)
const opPagination = reactive({ page:1, pageSize:10, total:0 })
const opSearch = reactive({ keyword:null, operationType:null, targetType:null, dateRange:null })

const sysLoading = ref(false)
const sysData = ref([])
const sysExportingCsv = ref(false)
const sysExportingExcel = ref(false)
const sysPagination = reactive({ page:1, pageSize:10, total:0 })
const sysSearch = reactive({ keyword:null, logLevel:null, module:null, dateRange:null })

const secLoading = ref(false)
const secData = ref([])
const secExportingCsv = ref(false)
const secExportingExcel = ref(false)
const secPagination = reactive({ page:1, pageSize:10, total:0 })
const secSearch = reactive({ keyword:null, eventType:null, dateRange:null })

onMounted(() => { fetchOpLogs() })

function buildSearchParams(search, dateRange) {
    const params = {}
    Object.keys(search).filter(k => k !== 'dateRange').forEach(k => {
        if (search[k]) params[k] = search[k]
    })
    if (dateRange && dateRange.length === 2) {
        params.startTime = dateRange[0]
        params.endTime = dateRange[1]
    }
    return params
}

async function fetchOpLogs() {
    opLoading.value = true
    try {
        const res = await getOperationLogPage({
            page: opPagination.page, pageSize: opPagination.pageSize,
            ...buildSearchParams(opSearch, opSearch.dateRange)
        })
        opData.value = res.data.records
        opPagination.total = res.data.total
    } catch { ElMessage.error('加载操作日志失败'); opData.value = [] } finally { opLoading.value = false }
}

function handleOpSearch() { opPagination.page = 1; fetchOpLogs() }
function handleOpPageChange(page) { opPagination.page = page; fetchOpLogs() }
function handleOpReset() { opSearch.keyword=null; opSearch.operationType=null; opSearch.targetType=null; opSearch.dateRange=null; handleOpSearch() }

async function downloadBlob(apiFn, loadingRef, fileName) {
    loadingRef.value = true
    try {
        const res = await apiFn
        const blob = new Blob([res.data])
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url; a.download = fileName; a.click()
        window.URL.revokeObjectURL(url)
        ElMessage.success('导出成功')
    } catch { ElMessage.error('导出失败') } finally { loadingRef.value = false }
}

async function exportOpCSV() {
    await downloadBlob(
        exportOperationLogCSV(buildSearchParams(opSearch, opSearch.dateRange)),
        opExportingCsv, 'operation_log.csv')
}

async function exportOpExcel() {
    await downloadBlob(
        exportOperationLogExcel(buildSearchParams(opSearch, opSearch.dateRange)),
        opExportingExcel, 'operation_log.xlsx')
}

async function fetchSysLogs() {
    sysLoading.value = true
    try {
        const res = await getSystemLogPage({
            page: sysPagination.page, pageSize: sysPagination.pageSize,
            ...buildSearchParams(sysSearch, sysSearch.dateRange)
        })
        sysData.value = res.data.records
        sysPagination.total = res.data.total
    } catch { ElMessage.error('加载系统日志失败'); sysData.value = [] } finally { sysLoading.value = false }
}

function handleSysSearch() { sysPagination.page = 1; fetchSysLogs() }
function handleSysPageChange(page) { sysPagination.page = page; fetchSysLogs() }
function handleSysReset() { sysSearch.keyword=null; sysSearch.logLevel=null; sysSearch.module=null; sysSearch.dateRange=null; handleSysSearch() }

async function exportSysCSV() {
    await downloadBlob(
        exportSystemLogCSV(buildSearchParams(sysSearch, sysSearch.dateRange)),
        sysExportingCsv, 'system_log.csv')
}

async function exportSysExcel() {
    await downloadBlob(
        exportSystemLogExcel(buildSearchParams(sysSearch, sysSearch.dateRange)),
        sysExportingExcel, 'system_log.xlsx')
}

async function fetchSecLogs() {
    secLoading.value = true
    try {
        const res = await getSecurityLogPage({
            page: secPagination.page, pageSize: secPagination.pageSize,
            ...buildSearchParams(secSearch, secSearch.dateRange)
        })
        secData.value = res.data.records
        secPagination.total = res.data.total
    } catch { ElMessage.error('加载安全日志失败'); secData.value = [] } finally { secLoading.value = false }
}

function handleSecSearch() { secPagination.page = 1; fetchSecLogs() }
function handleSecPageChange(page) { secPagination.page = page; fetchSecLogs() }
function handleSecReset() { secSearch.keyword=null; secSearch.eventType=null; secSearch.dateRange=null; handleSecSearch() }

async function exportSecCSV() {
    await downloadBlob(
        exportSecurityLogCSV(buildSearchParams(secSearch, secSearch.dateRange)),
        secExportingCsv, 'security_log.csv')
}

async function exportSecExcel() {
    await downloadBlob(
        exportSecurityLogExcel(buildSearchParams(secSearch, secSearch.dateRange)),
        secExportingExcel, 'security_log.xlsx')
}

function onTabChange(tabName) {
    if (tabName === 'operation') fetchOpLogs()
    if (tabName === 'system') fetchSysLogs()
    if (tabName === 'security') fetchSecLogs()
}

setInterval(() => {
    if (activeTab.value === 'operation') fetchOpLogs()
    else if (activeTab.value === 'system') fetchSysLogs()
    else if (activeTab.value === 'security') fetchSecLogs()
}, 30000)
</script>

<style scoped>
.page-container { padding:20px }
.card-header { display:flex; justify-content:space-between; align-items:center }
.card-title { font-size:18px; font-weight:600 }
.search-bar { margin-bottom:16px; display:flex; align-items:center; gap:8px; flex-wrap:wrap }
.pagination-container { display:flex; justify-content:flex-end; margin-top:16px }
.empty-state { display:flex; flex-direction:column; align-items:center; padding:40px 0; gap:16px }
</style>