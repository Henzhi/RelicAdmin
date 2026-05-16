<template>
    <div class="page-container">
        <el-card shadow="never">
            <template #header>
                <div class="card-header">
                    <span class="card-title">违规类型管理</span>
                    <el-button type="primary" :icon="Plus" @click="openCreateDialog">添加违规类型</el-button>
                </div>
            </template>

            <div class="search-bar">
                <el-select v-model="searchStatus" placeholder="状态" clearable style="width:120px" @change="handleSearch">
                    <el-option label="启用" :value="1" />
                    <el-option label="禁用" :value="0" />
                </el-select>
                <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
                <el-button :icon="Refresh" @click="handleReset">重置</el-button>
                <el-tag type="info" style="margin-left:auto">共 {{ pagination.total }} 条</el-tag>
            </div>

            <div v-loading="loading">
                <div v-if="tableData.length === 0 && !loading" class="empty-state">
                    <el-empty description="暂无违规类型" />
                </div>
                <el-table v-else :data="tableData" border stripe style="width:100%">
                    <el-table-column prop="id" label="ID" width="60" />
                    <el-table-column prop="typeCode" label="类型编码" width="160" />
                    <el-table-column prop="typeName" label="类型名称" min-width="150" />
                    <el-table-column prop="severityLevel" label="严重等级" width="110">
                        <template #default="{ row }">
                            <el-tag v-if="row.severityLevel === 1" size="small">轻微</el-tag>
                            <el-tag v-else-if="row.severityLevel === 2" type="warning" size="small">一般</el-tag>
                            <el-tag v-else-if="row.severityLevel === 3" type="danger" size="small">严重</el-tag>
                            <el-tag v-else-if="row.severityLevel === 4" type="danger" effect="dark" size="small">特别严重</el-tag>
                            <span v-else>{{ row.severityLevel }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="defaultPenalty" label="默认处罚" width="120">
                        <template #default="{ row }">
                            <el-tag v-if="row.defaultPenalty === 'warning'" size="small">警告</el-tag>
                            <el-tag v-else-if="row.defaultPenalty === 'temp_ban'" type="warning" size="small">临时封禁</el-tag>
                            <el-tag v-else-if="row.defaultPenalty === 'permanent_ban'" type="danger" size="small">永久封禁</el-tag>
                            <span v-else>{{ row.defaultPenalty }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
                    <el-table-column label="状态" width="90">
                        <template #default="{ row }">
                            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                                {{ row.status === 1 ? '启用' : '禁用' }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createdAt" label="创建时间" width="170" />
                    <el-table-column label="操作" width="200" fixed="right">
                        <template #default="{ row }">
                            <el-button size="small" link type="primary" @click="openEditDialog(row)">编辑</el-button>
                            <el-button size="small" link :type="row.status === 1 ? 'warning' : 'success'"
                                       @click="toggleStatus(row)">
                                {{ row.status === 1 ? '禁用' : '启用' }}
                            </el-button>
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

        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑违规类型' : '添加违规类型'"
                   width="520px" :close-on-click-modal="false" @closed="resetForm">
            <el-form :model="form" label-width="100px">
                <el-form-item label="类型编码">
                    <el-input v-model="form.typeCode" placeholder="唯一编码，如 spam_comment" :disabled="isEdit" />
                </el-form-item>
                <el-form-item label="类型名称">
                    <el-input v-model="form.typeName" placeholder="显示名称" />
                </el-form-item>
                <el-form-item label="严重等级">
                    <el-select v-model="form.severityLevel" style="width:100%">
                        <el-option label="轻微" :value="1" />
                        <el-option label="一般" :value="2" />
                        <el-option label="严重" :value="3" />
                        <el-option label="特别严重" :value="4" />
                    </el-select>
                </el-form-item>
                <el-form-item label="默认处罚">
                    <el-select v-model="form.defaultPenalty" style="width:100%">
                        <el-option label="警告" value="warning" />
                        <el-option label="临时封禁" value="temp_ban" />
                        <el-option label="永久封禁" value="permanent_ban" />
                    </el-select>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input v-model="form.description" type="textarea" :rows="3" placeholder="说明" />
                </el-form-item>
                <el-form-item label="状态" v-if="isEdit">
                    <el-radio-group v-model="form.status">
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
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getViolationTypePage, createViolationType, updateViolationType, updateViolationTypeStatus, deleteViolationType } from '../api/violationTypeAdmin'

const loading = ref(false)
const tableData = ref([])
const searchStatus = ref(null)
const pagination = reactive({ page:1, pageSize:10, total:0 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitLoading = ref(false)
const form = reactive({ typeCode:'', typeName:'', severityLevel:2, defaultPenalty:'warning', description:'', status:1 })

onMounted(() => { fetchData() })

async function fetchData() {
    loading.value = true
    try {
        const res = await getViolationTypePage({
            page: pagination.page, pageSize: pagination.pageSize, status: searchStatus.value
        })
        tableData.value = res.data.records
        pagination.total = res.data.total
    } catch {
        ElMessage.error('加载违规类型列表失败')
        tableData.value = []
    } finally {
        loading.value = false
    }
}

function handleSearch() { pagination.page = 1; fetchData() }
function handlePageChange(page) { pagination.page = page; fetchData() }
function handleReset() { searchStatus.value = null; handleSearch() }

function resetForm() {
    form.typeCode = ''; form.typeName = ''; form.severityLevel = 2; form.defaultPenalty = 'warning';
    form.description = ''; form.status = 1; isEdit.value = false; editId.value = null;
}

function openCreateDialog() { resetForm(); dialogVisible.value = true }

function openEditDialog(row) {
    isEdit.value = true; editId.value = row.id;
    form.typeCode = row.typeCode; form.typeName = row.typeName;
    form.severityLevel = row.severityLevel; form.defaultPenalty = row.defaultPenalty;
    form.description = row.description; form.status = row.status;
    dialogVisible.value = true;
}

async function submitForm() {
    if (!form.typeCode.trim() && !isEdit.value) { ElMessage.warning('请输入类型编码'); return }
    if (!form.typeName.trim()) { ElMessage.warning('请输入类型名称'); return }
    if (!form.severityLevel) { ElMessage.warning('请选择严重等级'); return }
    submitLoading.value = true
    try {
        if (isEdit.value) {
            await updateViolationType(editId.value, {
                typeName: form.typeName, severityLevel: form.severityLevel,
                defaultPenalty: form.defaultPenalty, description: form.description, status: form.status
            })
            ElMessage.success('已更新')
        } else {
            await createViolationType({
                typeCode: form.typeCode, typeName: form.typeName,
                severityLevel: form.severityLevel, defaultPenalty: form.defaultPenalty,
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

async function toggleStatus(row) {
    const newStatus = row.status === 1 ? 0 : 1
    try {
        await updateViolationTypeStatus(row.id, newStatus)
        ElMessage.success(newStatus ===1 ? '已启用' : '已禁用')
        fetchData()
    } catch {
        ElMessage.error('操作失败')
    }
}

async function handleDelete(row) {
    try {
        await ElMessageBox.confirm(`确定要删除违规类型「${row.typeName}」吗？`, '确认删除', { type:'warning' })
        await deleteViolationType(row.id)
        ElMessage.success('已删除')
        fetchData()
    } catch {}
}
</script>

<style scoped>
.page-container { padding:20px }
.card-header { display:flex; justify-content:space-between; align-items:center }
.card-title { font-size:18px; font-weight:600 }
.search-bar { margin-bottom:16px; display:flex; align-items:center; gap:8px; flex-wrap:wrap }
.pagination-container { display:flex; justify-content:flex-end; margin-top:16px }
.empty-state { display:flex; flex-direction:column; align-items:center; padding:40px 0; gap:16px }
</style>