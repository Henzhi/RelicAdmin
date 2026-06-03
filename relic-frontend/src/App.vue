<template>
  <!-- el-config-provider 全局注入 Element Plus 语言 -->
  <el-config-provider :locale="localeStore.elementLocale">
    <router-view />
  </el-config-provider>
</template>

<script setup>
import { useLocaleStore } from './stores/language';
import { ref, computed, watch } from 'vue'  // ← 导入 ref
import { useI18n } from 'vue-i18n'

// 现在可以正常使用 ref 了
const localeStore = useLocaleStore()

// ✅ 使用 try-catch 防止 i18n 未初始化
let t, locale
try {
  const i18n = useI18n()
  t = i18n.t
  locale = i18n.locale
} catch (error) {
  console.warn('i18n 未初始化，使用默认值')
  t = (key) => key  // 降级处理
  locale = ref('zhCn')
}
</script>

<style>
html, body, #app {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}
</style>