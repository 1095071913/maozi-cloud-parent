<script setup lang="ts">
import { useRoute } from 'vue-router'
const route = useRoute()
</script>

<template>
  <el-card shadow="never">
    <el-result icon="info" :title="String(route.meta.title || '页面')" sub-title="该菜单尚未绑定页面组件，请在 src/router/modules.ts 中为对应 mark 注册组件。">
    </el-result>
  </el-card>
</template>
