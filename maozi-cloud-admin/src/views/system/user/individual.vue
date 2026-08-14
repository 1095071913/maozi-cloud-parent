<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { updateUserInfo } from '@/api/user'
import { useUserStore } from '@/store/modules/user'
import type { UserIndividualUpdateParam } from '@/types/api'

const userStore = useUserStore()

const formRef = ref<FormInstance>()
const submitting = ref(false)

const defaultForm = (): UserIndividualUpdateParam => ({
  name: '',
  icon: '',
  password: '',
  newPassword: ''
})

const form = reactive(defaultForm())

/** 旧密码与新密码必须成对填写：填了其中一个，另一个也必填 */
const pairedPasswordValidator = (field: 'password' | 'newPassword') => (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  const other = field === 'password' ? form.newPassword : form.password
  if (value && !other) {
    callback(new Error(field === 'password' ? '请同时填写新密码' : '请同时填写旧密码'))
  } else {
    callback()
  }
}

const rules = computed<FormRules<typeof form>>(() => ({
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  password: [{ validator: pairedPasswordValidator('password'), trigger: 'blur' }],
  newPassword: [{ validator: pairedPasswordValidator('newPassword'), trigger: 'blur' }]
}))

/** 用当前登录用户信息回显表单 */
function fillForm() {
  Object.assign(form, defaultForm(), {
    name: userStore.name,
    icon: userStore.icon
  })
}

function handleReset() {
  fillForm()
  formRef.value?.clearValidate()
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      // 密码成对留空则不提交密码字段，表示不修改密码
      const changePassword = !!(form.password && form.newPassword)
      await updateUserInfo({
        name: form.name,
        icon: form.icon,
        password: changePassword ? form.password : undefined,
        newPassword: changePassword ? form.newPassword : undefined
      })
      ElMessage.success('保存成功')
      form.password = ''
      form.newPassword = ''
      formRef.value?.clearValidate()
      // 刷新顶栏展示的名称与头像
      await userStore.fetchUserInfo()
    } finally {
      submitting.value = false
    }
  })
}

onMounted(fillForm)
</script>

<template>
  <div class="individual-page">
    <el-card shadow="never">
      <template #header>个人信息</template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="individual-form">
        <el-form-item label="头像">
          <div class="icon-row">
            <el-avatar :size="64" :src="form.icon" />
            <el-input v-model="form.icon" placeholder="请输入头像图片地址" clearable />
          </div>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="旧密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            autocomplete="off"
            placeholder="不修改密码请留空"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            show-password
            autocomplete="off"
            placeholder="不修改密码请留空"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.individual-page {
  display: flex;
  justify-content: center;
}

.individual-form {
  width: 460px;
  max-width: 100%;
}

.icon-row {
  display: flex;
  gap: 12px;
  width: 100%;
  align-items: center;
}
</style>
