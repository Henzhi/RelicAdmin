<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">艺术家管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增艺术家</el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="searchForm.nameZh" placeholder="中文名" clearable style="width: 160px" @keyup.enter="handleSearch" />
        <el-input v-model="searchForm.nameEn" placeholder="英文名" clearable style="width: 160px" @keyup.enter="handleSearch" />
        <el-select v-model="searchForm.dynastyId" placeholder="所属朝代" clearable style="width: 160px">
          <el-option v-for="d in dynastyList" :key="d.id" :label="d.nameZh" :value="d.id" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe row-key="id" style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="nameZh" label="中文名" min-width="120" />
        <el-table-column prop="nameEn" label="英文名" min-width="150" show-overflow-tooltip />
        <el-table-column prop="birthYear" label="出生年份" width="100" />
        <el-table-column prop="deathYear" label="逝世年份" width="100" />
        <el-table-column prop="dynastyId" label="朝代ID" width="80" />
        <el-table-column prop="biography" label="简介" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" :icon="Edit" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除该艺术家？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button type="danger" size="small" :icon="Delete" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="中文名" prop="nameZh">
          <el-input v-model="formData.nameZh" placeholder="请输入中文名" />
        </el-form-item>
        <el-form-item label="英文名" prop="nameEn">
          <el-input v-model="formData.nameEn" placeholder="请输入英文名" />
        </el-form-item>
        <el-form-item label="出生年份" prop="birthYear">
          <el-input-number v-model="formData.birthYear" :min="-5000" :max="3000" placeholder="出生年份" style="width: 100%" />
        </el-form-item>
        <el-form-item label="逝世年份" prop="deathYear">
          <el-input-number v-model="formData.deathYear" :min="-5000" :max="3000" placeholder="逝世年份" style="width: 100%" />
        </el-form-item>
        <el-form-item label="所属朝代" prop="dynastyId">
          <el-select v-model="formData.dynastyId" placeholder="请选择朝代" style="width: 100%">
            <el-option v-for="d in dynastyList" :key="d.id" :label="d.nameZh" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="简介" prop="biography">
          <el-input v-model="formData.biography" type="textarea" :rows="4" placeholder="请输入艺术家简介" />
        </el-form-item>
        <el-form-item label="百度百科" prop="baiduUrl">
          <el-input v-model="formData.baiduUrl" placeholder="请输入百度百科链接" />
        </el-form-item>
        <el-form-item label="维基百科" prop="wikiUrl">
          <el-input v-model="formData.wikiUrl" placeholder="请输入维基百科链接" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import { getArtistPage, createArtist, updateArtist, deleteArtist } from '@/api/artist'
import { getDynastyList } from '@/api/dynasty'

const loading = ref(false)
const tableData = ref([])
const dynastyList = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const searchForm = reactive({ nameZh: '', nameEn: '', dynastyId: null })

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const editId = ref(null)
const submitLoading = ref(false)
const formRef = ref(null)
const formData = reactive({
  nameZh: '', nameEn: '', birthYear: null, deathYear: null,
  dynastyId: null, biography: '', baiduUrl: '', wikiUrl: ''
})
const formRules = {
  nameZh: [{ required: true, message: '请输入中文名', trigger: 'blur' }],
  dynastyId: [{ required: true, message: '请选择朝代', trigger: 'change' }]
}

onMounted(() => {
  fetchData()
  loadDynasties()
})

async function loadDynasties() {
  try {
    const res = await getDynastyList()
    dynastyList.value = res.data
  } catch (e) {
    console.error('加载朝代列表失败')
  }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getArtistPage({
      page: pagination.page,
      pageSize: pagination.pageSize,
      nameZh: searchForm.nameZh || undefined,
      nameEn: searchForm.nameEn || undefined,
      dynastyId: searchForm.dynastyId || undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (e) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  fetchData()
}

function handlePageChange(page) {
  pagination.page = page
  fetchData()
}

function handleReset() {
  searchForm.nameZh = ''
  searchForm.nameEn = ''
  searchForm.dynastyId = null
  handleSearch()
}

function handleAdd() {
  dialogTitle.value = '新增艺术家'
  isEdit.value = false
  editId.value = null
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑艺术家'
  isEdit.value = true
  editId.value = row.id
  formData.nameZh = row.nameZh
  formData.nameEn = row.nameEn || ''
  formData.birthYear = row.birthYear
  formData.deathYear = row.deathYear
  formData.dynastyId = row.dynastyId
  formData.biography = row.biography || ''
  formData.baiduUrl = row.baiduUrl || ''
  formData.wikiUrl = row.wikiUrl || ''
  dialogVisible.value = true
}

function resetForm() {
  formData.nameZh = ''
  formData.nameEn = ''
  formData.birthYear = null
  formData.deathYear = null
  formData.dynastyId = null
  formData.biography = ''
  formData.baiduUrl = ''
  formData.wikiUrl = ''
  formRef.value?.resetFields()
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateArtist(editId.value, { ...formData })
      ElMessage.success('更新成功')
    } else {
      await createArtist({ ...formData })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  try {
    await deleteArtist(row.id)
    ElMessage.success('删除成功')
    if (tableData.value.length === 1 && pagination.page > 1) {
      pagination.page--
    }
    fetchData()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '删除失败')
  }
}
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 18px; font-weight: 600; }
.search-bar { margin-bottom: 16px; display: flex; align-items: center; flex-wrap: wrap; gap: 8px; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>