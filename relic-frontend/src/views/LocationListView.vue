 <template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">地点管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd()">新增地点</el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-select v-model="searchType" placeholder="类型筛选" clearable style="width: 150px" @change="handleSearch">
          <el-option label="全部类型" value="" />
          <el-option label="国家" value="country" />
          <el-option label="省份" value="province" />
          <el-option label="城市" value="city" />
          <el-option label="遗址/地点" value="site" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        <el-tag v-if="tableData.length === 0 && !loading" type="warning">暂无数据</el-tag>
        <el-tag v-else type="info">共 {{ tableData.length }} 条数据</el-tag>
      </div>

      <el-table :data="tableData" v-loading="loading" border row-key="id" style="width: 100%">
        <el-table-column type="index" width="50" />
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="nameZh" label="中文名" min-width="150" />
        <el-table-column prop="nameEn" label="英文名" min-width="180" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="typeColor(row.type)" size="small">{{ typeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="parentName" label="上级地点" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" :icon="Edit" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除该地点？子地点也会受到影响" @confirm="handleDelete(row)">
              <template #reference>
                <el-button type="danger" size="small" :icon="Delete" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="中文名" prop="nameZh">
          <el-input v-model="formData.nameZh" placeholder="请输入中文名" />
        </el-form-item>
        <el-form-item label="英文名" prop="nameEn">
          <el-input v-model="formData.nameEn" placeholder="请输入英文名" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择类型" style="width: 100%" @change="onTypeChange">
            <el-option label="国家" value="country" />
            <el-option label="省份" value="province" />
            <el-option label="城市" value="city" />
            <el-option label="遗址/地点" value="site" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="formData.type !== 'country'" label="上级地点" prop="parentId">
          <el-select-v2
            v-model="formData.parentId"
            :options="parentOptions"
            :loading="parentLoading"
            placeholder="请选择上级地点"
            clearable
            style="width: 100%"
          />
          <div class="parent-hint">
            <el-text size="small" type="info">
              {{ parentHint }}
            </el-text>
          </div>
        </el-form-item>
        <el-form-item v-else label="上级地点">
          <el-text type="info">国家类型无需选择上级地点</el-text>
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
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import { getLocationTree, getLocationAll, getLocationList, getLocationParents, createLocation, updateLocation, deleteLocation } from '@/api/location'

const PARENT_TYPE_MAP = { site: 'city', city: 'province', province: 'country' }
const PARENT_TYPE_LABEL = { city: '城市', province: '省份', country: '国家' }

const loading = ref(false)
const tableData = ref([])
const allLocations = ref([])
const searchType = ref('')

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const editId = ref(null)
const submitLoading = ref(false)
const formRef = ref(null)
const parentLoading = ref(false)
const parentOptions = ref([])

const formData = reactive({
  nameZh: '', nameEn: '', type: '', parentId: null
})
const formRules = {
  nameZh: [{ required: true, message: '请输入中文名', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  parentId: [{
    validator: (rule, value, callback) => {
      if (formData.type && formData.type !== 'country' && !value) {
        callback(new Error('非国家类型必须选择上级地点'))
      } else {
        callback()
      }
    },
    trigger: 'change'
  }]
}

const parentHint = computed(() => {
  const expectedType = PARENT_TYPE_MAP[formData.type]
  if (!expectedType) return ''
  return `提示：请选择${PARENT_TYPE_LABEL[expectedType]}类型的地点作为上级`
})

function typeLabel(type) {
  const map = { country: '国家', province: '省份', city: '城市', site: '遗址/地点' }
  return map[type] || type
}

function typeColor(type) {
  const map = { country: 'danger', province: 'warning', city: 'success', site: '' }
  return map[type] || 'info'
}

onMounted(() => {
  fetchData()
})

async function fetchData() {
  loading.value = true
  try {
    let res
    if (searchType.value) {
      // 按类型筛选：返回平级列表
      res = await getLocationList({ type: searchType.value })
      tableData.value = res.data || []
    } else {
      // 全部类型：返回平级列表（非树形）
      res = await getLocationAll()
      const data = res.data || []
      allLocations.value = data
      tableData.value = data
    }
  } catch (e) {
    console.error('加载地点数据失败:', e)
    ElMessage.error('加载数据失败: ' + (e.message || '未知错误'))
    tableData.value = []
  } finally {
    loading.value = false
  }
}

async function loadParents() {
  if (!formData.type || formData.type === 'country') {
    parentOptions.value = []
    formData.parentId = null
    return
  }
  parentLoading.value = true
  try {
    const res = await getLocationParents(formData.type)
    const data = res.data || []
    parentOptions.value = data.map(item => ({
      label: item.nameZh,
      value: item.id
    }))
    if (isEdit.value && editId.value) {
      parentOptions.value = parentOptions.value.filter(o => o.value !== editId.value)
    }
  } catch (e) {
    parentOptions.value = []
  } finally {
    parentLoading.value = false
  }
}

function onTypeChange() {
  formData.parentId = null
  loadParents()
}

function handleSearch() {
  fetchData()
}

function handleReset() {
  searchType.value = ''
  fetchData()
}

function handleAdd(parentId) {
  dialogTitle.value = '新增地点'
  isEdit.value = false
  editId.value = null
  resetForm()
  if (parentId) formData.parentId = parentId
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑地点'
  isEdit.value = true
  editId.value = row.id
  formData.nameZh = row.nameZh
  formData.nameEn = row.nameEn || ''
  formData.type = row.type
  formData.parentId = row.parentId
  loadParents()
  dialogVisible.value = true
}

function resetForm() {
  formData.nameZh = ''
  formData.nameEn = ''
  formData.type = ''
  formData.parentId = null
  parentOptions.value = []
  formRef.value?.resetFields()
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateLocation(editId.value, { ...formData })
      ElMessage.success('更新成功')
    } else {
      await createLocation({ ...formData })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    const msg = e.response?.data?.msg || e.response?.data?.message
    ElMessage.error(msg || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  try {
    await deleteLocation(row.id)
    ElMessage.success('删除成功')
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
.search-bar { margin-bottom: 16px; display: flex; align-items: center; gap: 8px; }
.parent-hint { margin-top: 6px; }
</style>