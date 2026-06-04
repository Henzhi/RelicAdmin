<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">文物图片管理 — 文物ID: {{ artifactId }}</span>
          <div>
            <el-button :icon="ArrowLeft" @click="goBack">返回文物列表</el-button>
            <el-button type="primary" :icon="Plus" @click="handleAdd">添加图片</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe row-key="id" style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="预览" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.imageUrl"
              :src="row.imageUrl"
              style="width: 60px; height: 60px"
              fit="cover"
              :preview-src-list="[row.imageUrl]"
              preview-teleported
              :z-index="3200"
            />
          </template>
        </el-table-column>
        <el-table-column prop="imageUrl" label="图片URL" min-width="250" show-overflow-tooltip />
        <el-table-column prop="imagePath" label="本地路径" min-width="200" show-overflow-tooltip />
        <el-table-column label="主图" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isPrimary === 1 ? 'success' : 'info'" size="small">{{ row.isPrimary === 1 ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="70" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.isPrimary !== 1" type="warning" size="small" link @click="handleSetPrimary(row)">设为主图</el-button>
            <el-button type="primary" size="small" :icon="Edit" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除该图片？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button type="danger" size="small" :icon="Delete" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
        <el-form-item label="上传图片" prop="imageUrl">
          <div v-if="formData.imageUrl" class="upload-preview">
            <el-image :src="formData.imageUrl" style="width: 200px; height: 150px" fit="cover" />
            <el-button type="danger" size="small" circle :icon="Close" class="remove-btn" @click="formData.imageUrl = ''" />
          </div>
          <el-upload
            v-else
            ref="uploadRef"
            :action="uploadAction"
            :headers="uploadHeaders"
            :accept="uploadAccept"
            :limit="1"
            :on-progress="onUploadProgress"
            :on-success="onUploadSuccess"
            :on-error="onUploadError"
            :auto-upload="true"
            :show-file-list="false"
          >
            <el-button type="primary" :icon="Upload" :loading="uploadLoading">上传图片</el-button>
            <template #tip>
              <div class="el-upload__tip">支持 jpg/png/webp 格式，单文件最大 10MB</div>
            </template>
          </el-upload>
          <el-progress v-if="uploadPercent > 0 && uploadPercent < 100" :percentage="uploadPercent" />
        </el-form-item>
        <el-form-item label="是否主图" prop="isPrimary">
          <el-switch v-model="formData.isPrimary" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="999" style="width: 100%" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, ArrowLeft, Edit, Delete, Upload, Close } from '@element-plus/icons-vue'
import { getArtifactImages, createArtifactImage, updateArtifactImage, deleteArtifactImage, setPrimaryImage } from '@/api/artifactImage'

const route = useRoute()
const router = useRouter()
const artifactId = ref(Number(route.params.id))
const loading = ref(false)
const tableData = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const editId = ref(null)
const submitLoading = ref(false)
const formRef = ref(null)
const formData = reactive({
  imageUrl: '', imagePath: '', isPrimary: 0, sortOrder: 0
})
const uploadRef = ref(null)
const uploadLoading = ref(false)
const uploadPercent = ref(0)
const uploadAction = computed(() => '/admin/artifact/' + artifactId.value + '/images/upload')
const uploadHeaders = computed(() => ({ token: localStorage.getItem('admin_token') || '' }))
const uploadAccept = '.jpg,.jpeg,.png,.webp,.gif'

const formRules = {
  imageUrl: [{ required: true, message: '请上传图片', trigger: 'change' }]
}

function onUploadProgress(event) {
  uploadLoading.value = true
  uploadPercent.value = Math.round((event.loaded / event.total) * 100)
}

function onUploadSuccess(response) {
  uploadLoading.value = false
  uploadPercent.value = 0
  if (response.code === 200) {
    formData.imageUrl = response.data.imageUrl
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

function onUploadError(error, file) {
  uploadLoading.value = false
  uploadPercent.value = 0
  let msg = '图片上传失败'
  if (error && error.status === 413) {
    msg = '文件大小超出限制，请确保文件小于 10MB'
  } else if (error && error.status === 404) {
    msg = '上传接口不存在，请检查服务是否正常'
  } else if (error && error.message) {
    msg = '上传失败: ' + error.message
  }
  ElMessage.error(msg)
}

onMounted(() => { fetchData() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getArtifactImages(artifactId.value)
    tableData.value = res.data
  } catch (e) {
    ElMessage.error('加载图片失败')
  } finally {
    loading.value = false
  }
}

function goBack() { router.push('/artifacts') }

function handleAdd() {
  dialogTitle.value = '添加图片'
  isEdit.value = false
  editId.value = null
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑图片'
  isEdit.value = true
  editId.value = row.id
  formData.imageUrl = row.imageUrl
  formData.imagePath = row.imagePath || ''
  formData.isPrimary = row.isPrimary
  formData.sortOrder = row.sortOrder
  dialogVisible.value = true
}

function resetForm() {
  formData.imageUrl = ''
  formData.imagePath = ''
  formData.isPrimary = 0
  formData.sortOrder = 0
  formRef.value?.resetFields()
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateArtifactImage(artifactId.value, editId.value, { ...formData })
      ElMessage.success('更新成功')
    } else {
      await createArtifactImage(artifactId.value, { ...formData })
      ElMessage.success('添加成功')
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
    await deleteArtifactImage(artifactId.value, row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '删除失败')
  }
}

async function handleSetPrimary(row) {
  try {
    await setPrimaryImage(artifactId.value, row.id)
    ElMessage.success('主图设置成功')
    fetchData()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  }
}
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 18px; font-weight: 600; }
.upload-preview { position: relative; display: inline-block; }
.upload-preview .remove-btn { position: absolute; top: -8px; right: -8px; }
</style>