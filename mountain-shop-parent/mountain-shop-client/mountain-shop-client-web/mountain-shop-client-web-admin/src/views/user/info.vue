<template>
  <div class="app-container">
    <el-form
      ref="form"
      v-loading="formLoading"
      :data="form"
      element-loading-text="加载中..."
      :model="form"
      label-width="120px"
    >
      <el-form-item label="头像">
        <img :src="form.icon" width="60" height="60">
      </el-form-item>
      <el-form-item label="账号">
        <el-input v-model="form.username" :disabled="true" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="form.email" />
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="form.nickName" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.note" />
      </el-form-item>
      <el-form-item label="创建时间">
        <el-input v-model="form.createTime" :disabled="true" />
      </el-form-item>
      <el-form-item label="最后登录">
        <el-input v-model="form.loginTime" :disabled="true" />
      </el-form-item>
      <el-form-item label="是否启用">
        <el-radio-group v-model="form.status">
          <el-radio :label="0">禁用</el-radio>
          <el-radio :label="1">启用</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import { info } from '@/api/user'
export default {
  name: 'UserInfo',
  data() {
    return {
      formLoading: true,
      form: {
        icon: '',
        username: ''
        // email: '',
        // nickName: '',
        // note: '',
        // createTime: '',
        // loginTime: '',
        // status: ''
      }
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {  
      info(this.$store.getters.name).then(response => {
        const data = response
        this.form = data.databeans[0].data
        this.formLoading = false
      })
    },
    onSubmit() {
      this.$message('submit!')
    }
  }
}
</script>
<style scoped>
</style>