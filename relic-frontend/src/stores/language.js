// stores/locale.js (或 language.js)
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// Element Plus 语言包
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import en from 'element-plus/es/locale/lang/en'
import ja from 'element-plus/es/locale/lang/ja'

export const locales = {
  'zhCn': { label: '中文', flag: '🇨🇳', elementLocale: zhCn },
  'en': { label: 'English', flag: '🇺🇸', elementLocale: en },
  'ja': { label: '日本語', flag: '🇯🇵', elementLocale: ja }
}

export const useLocaleStore = defineStore('locale', () => {
  const currentLocale = ref(localStorage.getItem('app_locale') || 'zhCn')
  
  const elementLocale = computed(() => {
    return locales[currentLocale.value]?.elementLocale || zhCn
  })
  
  function setLocale(localeKey) {
    if (locales[localeKey]) {
      currentLocale.value = localeKey
      localStorage.setItem('app_locale', localeKey)
      document.documentElement.lang = localeKey === 'zhCn' ? 'zh-CN' : localeKey
      

    }
  }
  
  return { currentLocale, elementLocale, setLocale }
})