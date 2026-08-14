<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import { useAppStore } from '@/store/modules/app'
import { environmentInfo } from '@/utils/system'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const appStore = useAppStore()

/** 登录页标题：优先使用系统详情中的项目名称（未登录时由路由守卫提前拉取） */
const loginTitle = computed(() =>
  appStore.systemInfo?.projectName
    ? `${appStore.systemInfo.projectName} 后台管理`
    : 'maozi-cloud 后台管理'
)

/** 环境标识：与布局顶栏一致，按环境名映射标签与颜色 */
const envInfo = computed(() =>
  environmentInfo(appStore.systemInfo?.environment || '')
)

const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules: FormRules<typeof loginForm> = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin(formEl?: FormInstance) {
  if (!formEl) return
  await formEl.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await userStore.login(loginForm.username, loginForm.password)
      ElMessage.success('登录成功')
      const redirect = (route.query.redirect as string) || '/'
      router.push(redirect)
    } catch {
      // 错误提示已在拦截器中处理
    } finally {
      loading.value = false
    }
  })
}
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-title">{{ loginTitle }}</div>
      <div class="login-badges">
        <el-tag
          v-if="envInfo"
          :type="envInfo.type"
          effect="dark"
          size="large"
          class="login-env"
        >
          {{ envInfo.label }}
        </el-tag>
        <GrayRelease />
        <TempRequest />
      </div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        size="large"
        @keyup.enter="handleLogin(loginFormRef)"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入账号"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            class="login-btn"
            :loading="loading"
            @click="handleLogin(loginFormRef)"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <div
      v-if="appStore.systemInfo?.corporationName || appStore.systemInfo?.copyright"
      class="login-footer"
    >
      <div v-if="appStore.systemInfo?.corporationName">
        {{ appStore.systemInfo.corporationName }}
      </div>
      <div v-if="appStore.systemInfo?.copyright">
        {{ appStore.systemInfo.copyright }}
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.login-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.login-title {
  margin-bottom: 12px;
  font-size: 22px;
  font-weight: 600;
  text-align: center;
  color: #303133;
}

.login-badges {
  display: flex;
  flex-wrap: wrap;
  row-gap: 12px;
  gap: 8px;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}

/** 登录卡片内容区仅 320px，覆写顶栏的 132px 统一宽度，标签与按钮统一为 112px */
.login-card .login-badges :deep(.el-tag),
.login-card .login-badges :deep(.el-button) {
  width: auto;
  min-width: 112px;
}

.login-env {
  /** 与 large 按钮同高（el-tag large 默认 32px） */
  height: 40px;
  font-weight: 600;
  letter-spacing: 1px;
}

.login-footer {
  position: absolute;
  bottom: 16px;
  left: 0;
  right: 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.75);
  text-align: center;
  line-height: 20px;
}

.login-btn {
  width: 100%;
}
</style>
