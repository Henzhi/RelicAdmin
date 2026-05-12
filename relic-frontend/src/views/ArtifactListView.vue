<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">文物管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增文物</el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="searchForm.titleZh" placeholder="中文名" clearable style="width: 160px" @keyup.enter="handleSearch" />
        <el-input v-model="searchForm.titleEn" placeholder="英文名" clearable style="width: 160px" @keyup.enter="handleSearch" />
        <el-select v-model="searchForm.type" placeholder="类型" clearable style="width: 130px">
          <el-option v-for="t in artifactTypeList" :key="t.id" :label="t.name" :value="t.name" />
        </el-select>
        <el-input v-model="searchForm.material" placeholder="材质" clearable style="width: 130px" @keyup.enter="handleSearch" />
        <el-select v-model="searchForm.dynastyId" placeholder="朝代" clearable style="width: 150px">
          <el-option v-for="d in dynastyList" :key="d.id" :label="d.nameZh" :value="d.id" />
        </el-select>
        <el-select v-model="searchForm.museumId" placeholder="博物馆" clearable style="width: 180px">
          <el-option v-for="m in museumList" :key="m.id" :label="m.name" :value="m.id" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe row-key="id" style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="titleZh" label="中文名" min-width="150" show-overflow-tooltip />
        <el-table-column prop="titleEn" label="英文名" min-width="180" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="material" label="材质" width="120" show-overflow-tooltip />
        <el-table-column prop="timePeriod" label="年代" width="100" />
        <el-table-column prop="museumName" label="所属博物馆" width="120" show-overflow-tooltip />
        <el-table-column label="图片校验" width="90">
          <template #default="{ row }">
            <el-tag :type="row.imageValidated === 1 ? 'success' : 'danger'" size="small">{{ row.imageValidated === 1 ? '已校验' : '未校验' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="success" size="small" :icon="Picture" link @click="handleImages(row)">图片</el-button>
            <el-button type="primary" size="small" :icon="Edit" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除该文物？" @confirm="handleDelete(row)">
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="中文名" prop="titleZh">
              <el-input v-model="formData.titleZh" placeholder="请输入中文名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="英文名" prop="titleEn">
              <el-input v-model="formData.titleEn" placeholder="请输入英文名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型" prop="type">
              <el-select v-model="formData.type" placeholder="请选择类型" style="width: 100%">
                <el-option v-for="t in artifactTypeList" :key="t.id" :label="t.name" :value="t.name" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="材质" prop="material">
              <el-input v-model="formData.material" placeholder="请输入材质" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年代" prop="timePeriod">
              <el-input v-model="formData.timePeriod" placeholder="请输入年代" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="朝代" prop="dynastyId">
              <el-select v-model="formData.dynastyId" placeholder="请选择朝代" style="width: 100%">
                <el-option v-for="d in dynastyList" :key="d.id" :label="d.nameZh" :value="d.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="博物馆" prop="museumId">
              <el-select v-model="formData.museumId" placeholder="请选择博物馆" style="width: 100%">
                <el-option v-for="m in museumList" :key="m.id" :label="m.name" :value="m.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="城市" prop="locationId">
              <el-select-v2
                v-model="formData.locationId"
                :options="cityOptions"
                placeholder="搜索并选择城市"
                clearable
                filterable
                remote
                :remote-method="searchCities"
                :loading="cityLoading"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="编号" prop="objectId">
              <el-input v-model="formData.objectId" placeholder="请输入编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登记号" prop="accessionNumber">
              <el-input v-model="formData.accessionNumber" placeholder="请输入登记号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源" prop="creditLine">
              <el-input v-model="formData.creditLine" placeholder="请输入来源" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="尺寸" prop="dimensions">
              <el-input v-model="formData.dimensions" placeholder="请输入尺寸" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="爬取日期" prop="crawlDate">
              <el-date-picker v-model="formData.crawlDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="详情URL" prop="detailUrl">
              <el-input v-model="formData.detailUrl" placeholder="请输入详情URL" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入文物描述" />
            </el-form-item>
          </el-col>
        </el-row>
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
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, Picture } from '@element-plus/icons-vue'
import { getArtifactPage, createArtifact, updateArtifact, deleteArtifact } from '@/api/artifact'
import { getDynastyList } from '@/api/dynasty'
import { getMuseumPage } from '@/api/museum'
import { getArtifactTypeList } from '@/api/artifactType'
import { getLocationList } from '@/api/location'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const dynastyList = ref([])
const museumList = ref([])
const artifactTypeList = ref([])
const cityOptions = ref([])
const cityLoading = ref(false)
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const searchForm = reactive({
  titleZh: '', titleEn: '', type: '', material: '', dynastyId: null, museumId: null
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const editId = ref(null)
const submitLoading = ref(false)
const formRef = ref(null)
const formData = reactive({
  titleZh: '', titleEn: '', type: '', material: '', timePeriod: '',
  dynastyId: null, museumId: null, locationId: null,
  objectId: '', accessionNumber: '', creditLine: '', dimensions: '',
  crawlDate: null, detailUrl: '', description: ''
})
const formRules = {
  titleZh: [{ required: true, message: '请输入中文名', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  museumId: [{ required: true, message: '请选择博物馆', trigger: 'change' }]
}

onMounted(() => {
  fetchData()
  loadDynasties()
  loadMuseums()
  loadArtifactTypes()
})

async function loadDynasties() {
  try { const res = await getDynastyList(); dynastyList.value = res.data } catch (e) {}
}

async function loadMuseums() {
  try {
    const res = await getMuseumPage({ page: 1, pageSize: 100 })
    museumList.value = res.data.records
  } catch (e) {}
}

async function loadArtifactTypes() {
  try {
    const res = await getArtifactTypeList()
    artifactTypeList.value = res.data
  } catch (e) {}
}

async function searchCities(query) {
  if (!query || query.length < 1) {
    cityOptions.value = []
    return
  }
  cityLoading.value = true
  try {
    const res = await getLocationList({ type: 'city' })
    const data = res.data || []
    const filtered = data.filter(item => item.nameZh && item.nameZh.includes(query))
    cityOptions.value = filtered.map(item => ({
      label: item.nameZh,
      value: item.id
    }))
  } catch (e) {
    cityOptions.value = []
  } finally {
    cityLoading.value = false
  }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getArtifactPage({
      page: pagination.page, pageSize: pagination.pageSize,
      titleZh: searchForm.titleZh || undefined,
      titleEn: searchForm.titleEn || undefined,
      type: searchForm.type || undefined,
      material: searchForm.material || undefined,
      dynastyId: searchForm.dynastyId || undefined,
      museumId: searchForm.museumId || undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (e) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() { pagination.page = 1; fetchData() }
function handlePageChange(page) { pagination.page = page; fetchData() }

function handleReset() {
  Object.assign(searchForm, { titleZh: '', titleEn: '', type: '', material: '', dynastyId: null, museumId: null })
  handleSearch()
}

function handleAdd() {
  dialogTitle.value = '新增文物'
  isEdit.value = false
  editId.value = null
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑文物'
  isEdit.value = true
  editId.value = row.id
  Object.assign(formData, {
    titleZh: row.titleZh, titleEn: row.titleEn || '', type: row.type, material: row.material || '',
    timePeriod: row.timePeriod || '', dynastyId: row.dynastyId, museumId: row.museumId,
    locationId: row.locationId, objectId: row.objectId || '', accessionNumber: row.accessionNumber || '',
    creditLine: row.creditLine || '', dimensions: row.dimensions || '',
    crawlDate: row.crawlDate, detailUrl: row.detailUrl || '', description: row.description || ''
  })
  if (row.locationId) {
    cityOptions.value = [{ label: row.locationName || '', value: row.locationId }]
  }
  dialogVisible.value = true
}

function resetForm() {
  Object.assign(formData, {
    titleZh: '', titleEn: '', type: '', material: '', timePeriod: '',
    dynastyId: null, museumId: null, locationId: null,
    objectId: '', accessionNumber: '', creditLine: '', dimensions: '',
    crawlDate: null, detailUrl: '', description: ''
  })
  cityOptions.value = []
  formRef.value?.resetFields()
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    const payload = { ...formData }
    // 处理空值：将 0 或空字符串转为 null
    if (!payload.crawlDate) payload.crawlDate = null
    if (!payload.museumId || payload.museumId === 0 || payload.museumId === '') {
      payload.museumId = null
    }
    if (!payload.dynastyId || payload.dynastyId === 0 || payload.dynastyId === '') {
      payload.dynastyId = null
    }
    if (!payload.locationId || payload.locationId === 0 || payload.locationId === '') {
      payload.locationId = null
    }
    console.log('Submit payload:', payload)
    if (isEdit.value) {
      await updateArtifact(editId.value, payload)
      ElMessage.success('更新成功')
    } else {
      await createArtifact(payload)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    console.error('Submit error:', e)
    ElMessage.error(e.response?.data?.message || e.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  try {
    await deleteArtifact(row.id)
    ElMessage.success('删除成功')
    if (tableData.value.length === 1 && pagination.page > 1) pagination.page--
    fetchData()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '删除失败')
  }
}

function handleImages(row) {
  router.push('/artifact-images/' + row.id)
}
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 18px; font-weight: 600; }
.search-bar { margin-bottom: 16px; display: flex; align-items: center; flex-wrap: wrap; gap: 8px; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>