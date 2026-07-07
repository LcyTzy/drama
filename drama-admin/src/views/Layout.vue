<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="logo">
        <el-icon class="logo-icon"><Film /></el-icon>
        <span class="logo-text">短剧管理后台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="#343744"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/employee">
          <el-icon><User /></el-icon>
          <span>员工管理</span>
        </el-menu-item>
        <el-menu-item index="/category">
          <el-icon><Menu /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/drama">
          <el-icon><Film /></el-icon>
          <span>短剧管理</span>
        </el-menu-item>
        <el-menu-item index="/episode">
          <el-icon><VideoPlay /></el-icon>
          <span>剧集管理</span>
        </el-menu-item>
        <el-menu-item index="/order">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 右侧主区域 -->
    <div class="main-container">
      <!-- 顶部栏 -->
      <div class="header">
        <div class="header-title">{{ currentTitle }}</div>
        <div class="header-right">
          <el-icon class="user-icon"><UserFilled /></el-icon>
          <span class="username">{{ username }}</span>
          <el-divider direction="vertical" />
          <el-button
            type="danger"
            size="small"
            :icon="SwitchButton"
            @click="handleLogout"
          >
            退出
          </el-button>
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  Film,
  Odometer,
  User,
  Menu,
  VideoPlay,
  List,
  SwitchButton,
  UserFilled
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 当前激活的菜单项
const activeMenu = computed(() => route.path)

// 当前页面标题
const currentTitle = computed(() => route.meta.title || '首页')

// 用户名
const username = computed(() => userStore.userInfo?.name || '管理员')

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确认退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      userStore.clearUserInfo()
      ElMessage.success('已退出登录')
      router.push('/login')
    })
    .catch(() => {})
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  width: 100vw;
}

/* 侧边栏 */
.sidebar {
  width: 200px;
  background-color: #343744;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background-color: #2c2f3b;
  border-bottom: 1px solid #1f221b;
}

.logo-icon {
  font-size: 22px;
  margin-right: 8px;
  color: #409eff;
}

.logo-text {
  font-size: 15px;
  font-weight: 600;
  white-space: nowrap;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
}

.sidebar-menu :deep(.el-menu-item) {
  border-left: 3px solid transparent;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #1f2d3d !important;
  border-left-color: #409eff;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: #263445 !important;
}

/* 主区域 */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 60px;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 10;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-icon {
  font-size: 18px;
  color: #409eff;
}

.username {
  color: #606266;
  font-size: 14px;
}

.content {
  flex: 1;
  padding: 20px;
  background-color: #f0f2f5;
  overflow-y: auto;
}
</style>
