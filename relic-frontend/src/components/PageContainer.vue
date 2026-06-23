<template>
  <div class="page-container">
    <el-page-header
      v-if="title"
      class="page-container__header"
      :title="title"
      :content="description"
      @back="goBack"
    />
    <div class="page-container__body">
      <slot />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

defineProps({
  title: { type: String, default: '' },
  description: { type: String, default: '' }
})

function goBack() {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/dashboard')
  }
}
</script>
