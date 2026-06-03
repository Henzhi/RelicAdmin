<!-- components/LanguageDialog.vue -->
<template>
  <el-dialog
    v-model="visible"
    title="切换语言 / Switch Language / 言語切替"
    width="420px"
  >
    <div class="language-list">
      <div
        v-for="(config, key) in locales"
        :key="key"
        class="language-item"
        :class="{ 'is-active': selectedLang === key }"
        @click="selectedLang = key"
      >
        <span class="language-flag">{{ config.flag }}</span>
        <span class="language-label">{{ config.label }}</span>
        <el-icon v-if="selectedLang === key" color="#409eff">
          <Check />
        </el-icon>
      </div>
    </div>
    
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { Check } from '@element-plus/icons-vue'
import { useLocaleStore, locales } from '../stores/language'
import { useI18n } from 'vue-i18n'  // ✅ 只在 setup 顶层调用
import { ElMessage } from 'element-plus'

const localeStore = useLocaleStore()
const { locale, t } = useI18n()  // ✅ 在 setup 顶层调用，这是合法的

const visible = ref(false)
const selectedLang = ref(localeStore.currentLocale)

function open() {
  selectedLang.value = localeStore.currentLocale
  visible.value = true
}

function handleConfirm() {
  // 1. 先更新 store
  localeStore.setLocale(selectedLang.value)
  
  // 2. 再同步 i18n（在组件中调用，合法！）
  locale.value = selectedLang.value
  
  // 3. 提示
  visible.value = false
  ElMessage.success(t('message.switchLangSuccess'))
}

defineExpose({ open })
</script>

<style scoped>
.language-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.language-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.language-item:hover {
  border-color: #409eff;
  background-color: #f5f7fa;
}

.language-item.is-active {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.language-flag {
  font-size: 28px;
  margin-right: 16px;
}

.language-label {
  flex: 1;
  font-size: 15px;
  font-weight: 500;
}
</style>