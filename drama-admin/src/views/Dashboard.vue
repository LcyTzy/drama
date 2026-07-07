<template>
  <div class="dashboard">
    <el-card class="welcome-card" shadow="never">
      <div class="welcome-content">
        <el-icon class="welcome-icon"><Odometer /></el-icon>
        <h1 class="welcome-title">欢迎使用短剧管理后台</h1>
        <p class="welcome-desc">
          这里是管理后台首页，您可以在此管理短剧内容、分类、剧集和订单等信息。
        </p>
        <div class="quick-stats">
          <div class="stat-item">
            <el-icon class="stat-icon"><Film /></el-icon>
            <div class="stat-text">
              <div class="stat-label">短剧管理</div>
              <div class="stat-desc">管理所有短剧内容</div>
            </div>
          </div>
          <div class="stat-item">
            <el-icon class="stat-icon"><VideoPlay /></el-icon>
            <div class="stat-text">
              <div class="stat-label">剧集管理</div>
              <div class="stat-desc">管理短剧剧集资源</div>
            </div>
          </div>
          <div class="stat-item">
            <el-icon class="stat-icon"><List /></el-icon>
            <div class="stat-text">
              <div class="stat-label">订单管理</div>
              <div class="stat-desc">查看用户订单数据</div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { Odometer, Film, VideoPlay, List } from '@element-plus/icons-vue'
</script>

<style scoped>
.dashboard {
  height: 100%;
}

.welcome-card {
  height: 100%;
  border: none;
}

.welcome-content {
  text-align: center;
  padding: 60px 20px;
}

.welcome-icon {
  font-size: 72px;
  color: #409eff;
  margin-bottom: 20px;
}

.welcome-title {
  font-size: 28px;
  color: #303133;
  margin-bottom: 12px;
  font-weight: 600;
}

.welcome-desc {
  font-size: 14px;
  color: #909399;
  margin-bottom: 50px;
}

.quick-stats {
  display: flex;
  justify-content: center;
  gap: 30px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 20px 30px;
  background: #f5f7fa;
  border-radius: 8px;
  min-width: 220px;
  transition: all 0.3s;
}

.stat-item:hover {
  background: #ecf5ff;
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  font-size: 40px;
  color: #409eff;
  margin-right: 16px;
}

.stat-label {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-desc {
  font-size: 12px;
  color: #909399;
}
</style>
