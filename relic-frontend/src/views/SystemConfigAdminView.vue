<template>
    <div class="page-container">
        <el-card shadow="never">
            <template #header>
                <div class="card-header">
                    <span class="card-title">系统配置管理</span>
                </div>
            </template>

            <el-tabs v-model="activeTab" @tab-change="onTabChange">
                <el-tab-pane label="全局参数" name="config">
                    <div class="search-bar">
                        <el-select v-model="configSearchGroup" placeholder="配置分组" clearable style="width:150px" @change="handleConfigSearch">
                            <el-option label="通用设置" value="general" />
                            <el-option label="安全设置" value="security" />
                            <el-option label="文件设置" value="file" />
                            <el-option label="通知设置" value="notification" />
                            <el-option label="爬取设置" value="crawler" />
                        </el-select>
                        <el-button type="primary" :icon="Search" @click="handleConfigSearch">查询</el-button>
                        <el-button :icon="Refresh" @click="handleConfigReset">重置</el-button>
                        <el-tag type="info" style="margin-left:auto">共 {{ configPagination.total }} 条</el-tag>
                        <el-button type="primary" :icon="Plus" size="small" @click="openConfigDialog">添加配置</el-button>
                    </div>
                    <div v-loading="configLoading">
                        <div v-if="configData.length === 0 && !configLoading" class="empty-state">
                            <el-empty description="暂无配置项" />
                        </div>
                        <el-table v-else :data="configData" border stripe>
                            <el-table-column prop="id" label="ID" width="60" />
                            <el-table-column prop="configKey" label="配置键" width="160" show-overflow-tooltip />
                            <el-table-column prop="configName" label="名称" width="140" />
                            <el-table-column prop="configValue" label="配置值" min-width="200" show-overflow-tooltip />
                            <el-table-column prop="configType" label="类型" width="90">
                                <template #default="{ row }">
                                    <el-tag size="small">{{ row.configType }}</el-tag>
                                </template>
                            </el-table-column>
                            <el-table-column prop="configGroup" label="分组" width="100">
                                <template #default="{ row }">
                                    <el-tag v-if="row.configGroup === 'general'" size="small">通用</el-tag>
                                    <el-tag v-else-if="row.configGroup === 'security'" type="danger" size="small">安全</el-tag>
                                    <el-tag v-else-if="row.configGroup === 'file'" type="warning" size="small">文件</el-tag>
                                    <el-tag v-else-if="row.configGroup === 'notification'" type="success" size="small">通知</el-tag>
                                    <el-tag v-else-if="row.configGroup === 'crawler'" type="info" size="small">爬取</el-tag>
                                    <span v-else>{{ row.configGroup }}</span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
                            <el-table-column label="状态" width="80">
                                <template #default="{ row }">
                                    <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                                        {{ row.status === 1 ? '启用' : '禁用' }}
                                    </el-tag>
                                </template>
                            </el-table-column>
                            <el-table-column label="操作" width="220" fixed="right">
                                <template #default="{ row }">
                                    <el-button size="small" link type="primary" @click="openConfigEdit(row)">编辑</el-button>
                                    <el-button size="small" link type="warning" @click="openValueEdit(row)">改值</el-button>
                                    <el-button size="small" link type="danger" @click="handleConfigDelete(row)">删除</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                    <div class="pagination-container" v-if="configData.length > 0">
                        <el-pagination
          background
                            v-model:current-page="configPagination.page"
                            v-model:page-size="configPagination.pageSize"
                            :page-sizes="[10,20,50]"
                            :total="configPagination.total"
                            layout="total, sizes, prev, pager, next, jumper"
                            @size-change="handleConfigSearch"
                            @current-change="handleConfigPageChange"
                        />
                    </div>
                </el-tab-pane>

                <el-tab-pane label="日志级别" name="log">
                    <div class="log-config-area">
                        <el-descriptions :column="1" border size="large">
                            <el-descriptions-item label="当前日志级别">
                                <el-tag type="warning" size="large">{{ currentLogLevel }}</el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item label="调整日志级别">
                                <el-radio-group v-model="tempLogLevel" @change="onLogLevelChange">
                                    <el-radio-button value="DEBUG">DEBUG</el-radio-button>
                                    <el-radio-button value="INFO">INFO</el-radio-button>
                                    <el-radio-button value="WARN">WARN</el-radio-button>
                                    <el-radio-button value="ERROR">ERROR</el-radio-button>
                                </el-radio-group>
                                <el-button type="primary" size="small" style="margin-left:20px" :loading="logSaving" @click="saveLogLevel">应用</el-button>
                            </el-descriptions-item>
                            <el-descriptions-item label="说明">
                                <span style="color:#909399">DEBUG: 最详细日志, INFO: 常规信息, WARN: 警告信息, ERROR: 仅错误信息。修改后实时生效。</span>
                            </el-descriptions-item>
                        </el-descriptions>

                        <el-alert title="提示" type="info" style="margin-top:20px" :closable="false"
                            description="日志级别调整将影响系统日志输出详细程度。生产环境建议使用INFO或WARN级别，开发和调试时可使用DEBUG级别。" />
                    </div>
                </el-tab-pane>

                <el-tab-pane label="功能开关" name="feature">
                    <div class="search-bar">
                        <el-tag type="info" style="margin-left:auto">共 {{ toggles.length }} 项功能开关</el-tag>
                        <el-button size="small" :icon="Refresh" @click="loadToggles">刷新</el-button>
                    </div>
                    <div v-loading="togglesLoading">
                        <div v-if="toggles.length === 0 && !togglesLoading" class="empty-state">
                            <el-empty description="暂无功能开关配置" />
                        </div>
                        <el-table v-else :data="toggles" border stripe>
                            <el-table-column prop="id" label="ID" width="60" />
                            <el-table-column prop="configName" label="功能名称" width="160" />
                            <el-table-column prop="configKey" label="配置键" width="180" show-overflow-tooltip />
                            <el-table-column prop="description" label="说明" min-width="220" show-overflow-tooltip />
                            <el-table-column label="当前状态" width="120">
                                <template #default="{ row }">
                                    <el-switch
                                        :model-value="row.configValue === 'true'"
                                        active-text="开启"
                                        inactive-text="关闭"
                                        :loading="row._switching"
                                        inline-prompt
                                        @change="handleToggleSwitch(row, $event)"
                                    />
                                </template>
                            </el-table-column>
                            <el-table-column label="状态标签" width="100">
                                <template #default="{ row }">
                                    <el-tag :type="row.configValue === 'true' ? 'success' : 'danger'" size="small">
                                        {{ row.configValue === 'true' ? '已开启' : '已关闭' }}
                                    </el-tag>
                                </template>
                            </el-table-column>
                            <el-table-column prop="updatedAt" label="最近更新" width="160" />
                        </el-table>
                    </div>
                    <el-alert title="功能开关说明" type="info" style="margin-top:20px" :closable="false"
                        description="功能开关用于在线控制各子系统的功能可用性，修改后实时生效无需重启服务。灰度发布时可逐项关闭非核心功能，紧急情况下可一键开启维护模式。" />
                </el-tab-pane>
            </el-tabs>
        </el-card>

        <el-dialog v-model="configDialogVisible" :title="isConfigEdit ? '编辑配置项' : '添加配置项'"
                   width="500px" :close-on-click-modal="false" @closed="resetConfigForm">
            <el-form :model="configForm" label-width="90px">
                <el-form-item label="配置键">
                    <el-input v-model="configForm.configKey" placeholder="如 site_name" :disabled="isConfigEdit" />
                </el-form-item>
                <el-form-item label="配置名称">
                    <el-input v-model="configForm.configName" placeholder="显示名称" />
                </el-form-item>
                <el-form-item label="配置值">
                    <el-input v-model="configForm.configValue" type="textarea" :rows="3" placeholder="配置值" />
                </el-form-item>
                <el-row :gutter="16">
                    <el-col :span="12">
                        <el-form-item label="值类型">
                            <el-select v-model="configForm.configType" style="width:100%">
                                <el-option label="字符串" value="string" />
                                <el-option label="数字" value="number" />
                                <el-option label="布尔" value="boolean" />
                                <el-option label="JSON" value="json" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="配置分组">
                            <el-select v-model="configForm.configGroup" style="width:100%">
                                <el-option label="通用" value="general" />
                                <el-option label="安全" value="security" />
                                <el-option label="文件" value="file" />
                                <el-option label="通知" value="notification" />
                                <el-option label="爬取" value="crawler" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="描述">
                    <el-input v-model="configForm.description" type="textarea" :rows="2" placeholder="说明" />
                </el-form-item>
                <el-form-item label="排序">
                    <el-input-number v-model="configForm.sortOrder" :min="0" />
                </el-form-item>
                <el-form-item label="状态" v-if="isConfigEdit">
                    <el-radio-group v-model="configForm.status">
                        <el-radio :value="1">启用</el-radio>
                        <el-radio :value="0">禁用</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="configDialogVisible = false">取消</el-button>
                <el-button type="primary" :loading="configSubmitLoading" @click="submitConfigForm">{{ isConfigEdit ? '保存' : '添加' }}</el-button>
            </template>
        </el-dialog>

        <el-dialog v-model="valueDialogVisible" title="修改配置值" width="450px" :close-on-click-modal="false">
            <el-form label-width="80px">
                <el-form-item :label="valueForm.configKey">
                    <el-input v-model="valueForm.configValue" type="textarea" :rows="4" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="valueDialogVisible = false">取消</el-button>
                <el-button type="primary" :loading="valueSaving" @click="submitValue">保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getSystemConfigPage, createSystemConfig, updateSystemConfig,
         updateSystemConfigValue, deleteSystemConfig,
         getSystemConfigByGroup, getFeatureToggles, toggleFeature } from '../api/systemConfigAdmin'

const activeTab = ref('config')

const configLoading = ref(false)
const configData = ref([])
const configSearchGroup = ref(null)
const configPagination = reactive({ page:1, pageSize:10, total:0 })
const configDialogVisible = ref(false)
const isConfigEdit = ref(false)
const configEditId = ref(null)
const configSubmitLoading = ref(false)
const configForm = reactive({
    configKey:'', configName:'', configValue:'', configType:'string',
    configGroup:'general', description:'', sortOrder:0, status:1
})
const valueDialogVisible = ref(false)
const valueSaving = ref(false)
const valueForm = reactive({ id:null, configKey:'', configValue:'' })

const currentLogLevel = ref('INFO')
const tempLogLevel = ref('INFO')
const logSaving = ref(false)

const toggles = ref([])
const togglesLoading = ref(false)

onMounted(() => { fetchConfigData(); loadLogLevel() })

async function fetchConfigData() {
    configLoading.value = true
    try {
        const res = await getSystemConfigPage({
            page: configPagination.page, pageSize: configPagination.pageSize,
            configGroup: configSearchGroup.value
        })
        configData.value = res.data.records
        configPagination.total = res.data.total
    } catch {
        ElMessage.error('加载配置列表失败')
        configData.value = []
    } finally {
        configLoading.value = false
    }
}

function handleConfigSearch() { configPagination.page = 1; fetchConfigData() }
function handleConfigPageChange(page) { configPagination.page = page; fetchConfigData() }
function handleConfigReset() { configSearchGroup.value = null; handleConfigSearch() }

function resetConfigForm() {
    configForm.configKey = ''; configForm.configName = ''; configForm.configValue = '';
    configForm.configType = 'string'; configForm.configGroup = 'general';
    configForm.description = ''; configForm.sortOrder = 0; configForm.status = 1;
    isConfigEdit.value = false; configEditId.value = null;
}

function openConfigDialog() { resetConfigForm(); configDialogVisible.value = true }

function openConfigEdit(row) {
    isConfigEdit.value = true; configEditId.value = row.id;
    configForm.configKey = row.configKey; configForm.configName = row.configName;
    configForm.configValue = row.configValue; configForm.configType = row.configType;
    configForm.configGroup = row.configGroup; configForm.description = row.description;
    configForm.sortOrder = row.sortOrder; configForm.status = row.status;
    configDialogVisible.value = true;
}

async function submitConfigForm() {
    if (!configForm.configKey.trim() && !isConfigEdit.value) { ElMessage.warning('请输入配置键'); return }
    if (!configForm.configName.trim()) { ElMessage.warning('请输入配置名称'); return }
    configSubmitLoading.value = true
    try {
        if (isConfigEdit.value) {
            await updateSystemConfig(configEditId.value, {
                configName: configForm.configName, configValue: configForm.configValue,
                configType: configForm.configType, configGroup: configForm.configGroup,
                description: configForm.description, sortOrder: configForm.sortOrder,
                status: configForm.status
            })
            ElMessage.success('已更新')
        } else {
            await createSystemConfig({
                configKey: configForm.configKey, configName: configForm.configName,
                configValue: configForm.configValue, configType: configForm.configType,
                configGroup: configForm.configGroup, description: configForm.description,
                sortOrder: configForm.sortOrder
            })
            ElMessage.success('已添加')
        }
        configDialogVisible.value = false; fetchConfigData()
    } catch (err) {
        ElMessage.error(err.message || '操作失败')
    } finally {
        configSubmitLoading.value = false
    }
}

function openValueEdit(row) {
    valueForm.id = row.id; valueForm.configKey = row.configKey; valueForm.configValue = row.configValue;
    valueDialogVisible.value = true;
}

async function submitValue() {
    valueSaving.value = true
    try {
        await updateSystemConfigValue(valueForm.id, valueForm.configValue)
        ElMessage.success('配置值已更新')
        valueDialogVisible.value = false; fetchConfigData()
    } catch (err) {
        ElMessage.error(err.message || '操作失败')
    } finally {
        valueSaving.value = false
    }
}

async function handleConfigDelete(row) {
    try {
        await ElMessageBox.confirm(`确定要删除配置项「${row.configName}」吗？`, '确认删除', { type:'warning' })
        await deleteSystemConfig(row.id)
        ElMessage.success('已删除')
        fetchConfigData()
    } catch {}
}

async function loadLogLevel() {
    try {
        const res = await getSystemConfigByGroup('general')
        const logConfig = res.data.find(c => c.configKey === 'log_level')
        if (logConfig && logConfig.configValue) {
            currentLogLevel.value = logConfig.configValue
            tempLogLevel.value = logConfig.configValue
        }
    } catch {}
}

function onLogLevelChange(val) {
    tempLogLevel.value = val
}

async function saveLogLevel() {
    logSaving.value = true
    try {
        const res = await getSystemConfigByGroup('general')
        const logConfig = res.data.find(c => c.configKey === 'log_level')
        if (logConfig) {
            await updateSystemConfigValue(logConfig.id, tempLogLevel.value)
            currentLogLevel.value = tempLogLevel.value
            ElMessage.success('日志级别已更新，实时生效')
        }
    } catch (err) {
        ElMessage.error(err.message || '更新日志级别失败')
    } finally {
        logSaving.value = false
    }
}

function onTabChange(tabName) {
    if (tabName === 'config') fetchConfigData()
    if (tabName === 'log') loadLogLevel()
    if (tabName === 'feature') loadToggles()
}

async function loadToggles() {
    togglesLoading.value = true
    try {
        const res = await getFeatureToggles()
        toggles.value = (res.data || []).map(t => ({ ...t, _switching: false }))
    } catch {
        ElMessage.error('加载功能开关失败')
        toggles.value = []
    } finally {
        togglesLoading.value = false
    }
}

async function handleToggleSwitch(row, value) {
    row._switching = true
    const newValue = value ? 'true' : 'false'
    try {
        await toggleFeature(row.id, newValue)
        row.configValue = newValue
        ElMessage.success(`「${row.configName}」已${value ? '开启' : '关闭'}`)
    } catch (err) {
        ElMessage.error(err.message || '操作失败')
    } finally {
        row._switching = false
    }
}
</script>

<style scoped>
.page-container { padding:20px }
.card-header { display:flex; justify-content:space-between; align-items:center }
.card-title { font-size:18px; font-weight:600 }
.search-bar { margin-bottom:16px; display:flex; align-items:center; gap:8px; flex-wrap:wrap }
.pagination-container { display:flex; justify-content:flex-end; margin-top:16px }
.empty-state { display:flex; flex-direction:column; align-items:center; padding:40px 0; gap:16px }
.log-config-area { padding:10px 0 }
</style>
