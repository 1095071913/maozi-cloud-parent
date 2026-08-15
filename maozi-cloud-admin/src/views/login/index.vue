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
    <!-- 左侧品牌区：微服务全景图（参考 Spring Cloud Alibaba 官网架构图） -->
    <div class="login-panel">
      <ArchitectureDiagram />
    </div>

    <!-- 右侧登录表单 -->
    <div class="login-card">
      <div class="login-head">
        <div class="login-title">{{ loginTitle }}</div>
        <div class="login-subtitle">欢迎回来，请登录您的账号</div>
      </div>
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
  gap: 32px;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  width: 100%;
  height: 100vh;
  padding: 32px;
  overflow: auto;

  /** 深邃夜紫底 + 双侧光晕，衬托玻璃面板 */
  background:
    radial-gradient(1200px 800px at 12% 18%, rgb(99 102 241 / 38%), transparent 62%),
    radial-gradient(1000px 720px at 88% 82%, rgb(168 85 247 / 32%), transparent 62%),
    linear-gradient(135deg, #1b1f3b 0%, #2a1e4f 55%, #241a40 100%);
}

/** 左侧品牌区：近透明玻璃底 + 描边 + 多层投影，呈现悬浮立体感 */
.login-panel {
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  width: min(2200px, calc(100vw - 500px));
  min-width: 640px;
  padding: 24px 40px;
  background: rgba(255, 255, 255, 0.07);
  border: 1px solid rgba(255, 255, 255, 0.22);
  border-radius: 20px;
  box-shadow:
    0 24px 60px rgb(0 0 0 / 35%),
    0 4px 16px rgb(0 0 0 / 22%),
    inset 0 1px 0 rgb(255 255 255 / 45%),
    inset 0 -1px 0 rgb(255 255 255 / 12%);
  backdrop-filter: blur(18px);
}

/** 右侧登录卡片：与左侧同款多层投影，呈现悬浮立体感 */
.login-card {
  flex-shrink: 0;
  width: 400px;
  padding: 40px;
  background: #fff;
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 20px;
  box-shadow:
    0 24px 60px rgb(0 0 0 / 35%),
    0 4px 16px rgb(0 0 0 / 22%),
    inset 0 1px 0 rgb(255 255 255 / 90%),
    inset 0 -2px 6px rgb(31 45 61 / 6%);
}

/** 窄屏隐藏左侧品牌区，登录表单回退居中 */
@media (max-width: 1280px) {
  .login-panel {
    display: none;
  }

  .login-container {
    padding: 32px;
  }
}

/** 登录卡片头部：居中标题 + 副标题 */
.login-head {
  margin-bottom: 24px;
  text-align: center;
}

.login-title {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 1px;
  color: #1f2d3d;
}

.login-subtitle {
  margin-top: 8px;
  font-size: 13px;
  color: #909399;
}

.login-badges {
  display: flex;
  flex-wrap: wrap;
  row-gap: 12px;
  gap: 8px;
  align-items: center;
  justify-content: center;
  margin-bottom: 28px;
  padding-bottom: 20px;
  border-bottom: 1px dashed #ebeef5;
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
  color: rgba(255, 255, 255, 0.55);
  text-align: center;
  letter-spacing: 1px;
  line-height: 20px;
}

/** 输入框与登录按钮统一圆角 */
.login-card :deep(.el-input__wrapper) {
  border-radius: 10px;
}

.login-btn {
  width: 100%;
  border-radius: 10px;
  letter-spacing: 6px;

  /** 抵消末字后多出的字距，保证文字真正居中 */
  text-indent: 6px;
}
</style>
