<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { useAppStore } from '@/store/modules/app'

const userStore = useUserStore()
const appStore = useAppStore()

const project = computed(() => appStore.systemInfo)
</script>

<template>
  <div class="dashboard">
    <el-card v-if="project" class="project-card">
      <div class="project-block">
        <el-avatar :size="56" :src="project.icon" shape="square" />
        <div class="project-meta">
          <div class="project-title">
            <h2>{{ project.projectName }}</h2>
          </div>
          <p v-if="project.description" class="project-desc">{{ project.description }}</p>
        </div>
      </div>
    </el-card>

    <h2 class="welcome">欢迎回来，{{ userStore.name }}</h2>
  </div>
</template>

<style scoped lang="scss">
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 100%;
}

.project-card {
  .project-block {
    display: flex;
    gap: 16px;
    align-items: flex-start;
  }

  .project-meta {
    flex: 1;
    min-width: 0;
  }

  .project-title {
    h2 {
      margin: 0;
      font-size: 18px;
    }
  }

  .project-desc {
    margin: 6px 0 0;
    color: #606266;
    font-size: 13px;
  }
}

.welcome {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0;
  font-size: 36px;
  opacity: 0.55;
}
</style>
