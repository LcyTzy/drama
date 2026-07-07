<template>
  <div class="login-container">
    <div class="login-box">
      <!-- 左侧展示区 -->
      <div class="login-left">
        <div class="left-content">
          <div class="logo-row">
            <el-icon class="logo-icon"><Film /></el-icon>
            <h1 class="title">短剧管理后台</h1>
          </div>
          <p class="subtitle">Short Drama Management System</p>
          <div class="decoration">
            <el-icon class="icon-big"><VideoPlay /></el-icon>
            <el-icon class="icon-small"><Film /></el-icon>
            <el-icon class="icon-big"><VideoCamera /></el-icon>
          </div>
          <p class="slogan">专注短剧内容运营的高效管理平台</p>
        </div>
      </div>

      <!-- 右侧表单区 -->
      <div class="login-right">
        <div class="login-form-wrapper">
          <h2 class="form-title">账号登录</h2>
          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="rules"
            class="login-form"
            @keyup.enter="handleLogin"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                :prefix-icon="User"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                size="large"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="login-btn"
                :loading="loading"
                @click="handleLogin"
              >
                登 录
              </el-button>
            </el-form-item>
          </el-form>
          <div class="tips">默认账号：admin / 密码：admin</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User,
  Lock,
  Film,
  VideoPlay,
  VideoCamera
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

// 登录表单数据，默认 admin / admin
const loginForm = reactive({
  username: 'admin',
  password: 'admin'
})

// 表单校验规则
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await request({
        url: '/login',
        method: 'post',
        data: loginForm
      })
      // 存储 token 和用户信息
      userStore.setToken(res.data.token)
      userStore.setUserInfo({
        id: res.data.id,
        name: res.data.name
      })
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } catch (error) {
      console.error('登录失败', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  display: flex;
  width: 900px;
  height: 500px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

/* 左侧展示区 */
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #2c3e50 0%, #1a1a2e 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.left-content {
  text-align: center;
  color: #fff;
  z-index: 1;
  padding: 0 30px;
}

.logo-row {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
}

.logo-icon {
  font-size: 42px;
  color: #409eff;
  margin-right: 12px;
}

.title {
  font-size: 30px;
  font-weight: 700;
  letter-spacing: 2px;
}

.subtitle {
  font-size: 13px;
  opacity: 0.7;
  letter-spacing: 1px;
  margin-bottom: 50px;
}

.decoration {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  opacity: 0.4;
  margin-bottom: 40px;
}

.decoration .el-icon {
  color: #fff;
}

.icon-big {
  font-size: 56px;
}

.icon-small {
  font-size: 40px;
}

.slogan {
  font-size: 14px;
  opacity: 0.6;
  letter-spacing: 1px;
}

/* 右侧表单区 */
.login-right {
  flex: 1;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-form-wrapper {
  width: 320px;
  padding: 0 20px;
}

.form-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 36px;
  text-align: center;
  font-weight: 600;
}

.login-form {
  width: 100%;
}

.login-btn {
  width: 100%;
  background: #4e73df;
  border-color: #4e73df;
  font-size: 16px;
  letter-spacing: 4px;
}

.login-btn:hover,
.login-btn:focus {
  background: #4060d0;
  border-color: #4060d0;
}

.tips {
  margin-top: 12px;
  text-align: center;
  font-size: 12px;
  color: #909399;
}
</style>
