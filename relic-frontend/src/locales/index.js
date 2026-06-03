// locales/index.js
import { createI18n } from 'vue-i18n'
import zhCn from './zhCn'
import en from './en'
import ja from './ja'

// 获取初始语言（从 localStorage 读取）
const savedLocale = localStorage.getItem('app_locale') || 'zhCn'

const i18n = createI18n({
  legacy: false,           // 使用 Composition API 模式
  locale: savedLocale,     // 默认语言
  fallbackLocale: 'zhCn',  // 找不到翻译时的备用语言
  globalInjection: true,   // 全局注入 $t 函数
  messages: {
    zhCn,
    en,
    ja
  }
})

export default i18n