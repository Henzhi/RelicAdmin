<template>
  <div class="page-container">
    <el-page-header
      v-if="title"
      class="page-container__header"
      :title="title"
      :content="description"
    />
    <div class="page-container__body">
      <slot />
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: '' },
  description: { type: String, default: '' }
})
</script>
