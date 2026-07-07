<template>
  <div class="order-page">
    <!-- 顶部统计卡片 -->
    <el-card shadow="never" class="stats-card">
      <div class="stats-header">
        <span class="stats-title">收入统计</span>
        <div class="stats-filter">
          <el-date-picker
            v-model="revenueRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            class="stats-date-picker"
          />
          <el-button type="primary" :icon="Search" @click="handleRevenueSearch">查询</el-button>
        </div>
      </div>
      <div class="stats-cards">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon stat-icon-green"><Money /></el-icon>
            <div class="stat-info">
              <div class="stat-label">总收入</div>
              <div class="stat-value stat-green">¥{{ formatMoney(revenueData.totalRevenue) }}</div>
            </div>
          </div>
        </el-card>
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon stat-icon-red"><Coin /></el-icon>
            <div class="stat-info">
              <div class="stat-label">今日收入</div>
              <div class="stat-value stat-red">¥{{ formatMoney(revenueData.todayRevenue) }}</div>
            </div>
          </div>
        </el-card>
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon stat-icon-blue"><Tickets /></el-icon>
            <div class="stat-info">
              <div class="stat-label">总订单数</div>
              <div class="stat-value stat-blue">{{ revenueData.orderCount }}单</div>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>

    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <div class="search-bar">
        <div class="search-left">
          <span class="search-label">订单号：</span>
          <el-input
            v-model="searchForm.orderNo"
            placeholder="请输入订单号"
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <span class="search-label">用户手机号：</span>
          <el-input
            v-model="searchForm.userPhone"
            placeholder="请输入用户手机号"
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <span class="search-label">订单状态：</span>
          <el-select
            v-model="searchForm.status"
            placeholder="请选择订单状态"
            clearable
            class="search-select"
          >
            <el-option label="待支付" :value="1" />
            <el-option label="已支付" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
          <span class="search-label">下单时间：</span>
          <el-date-picker
            v-model="searchForm.timeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            class="search-date-picker"
          />
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </div>
      </div>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#303133', fontWeight: 600 }"
      >
        <el-table-column prop="orderNo" label="订单号" min-width="200" />
        <el-table-column prop="userName" label="用户名" min-width="120" />
        <el-table-column prop="totalAmount" label="金额" min-width="120" align="right">
          <template #default="{ row }">
            <span class="amount-text">¥{{ formatMoney(row.totalAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">
              {{ formatStatus(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="payTime" label="支付时间" min-width="170">
          <template #default="{ row }">
            {{ formatTime(row.payTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          background
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, View, Money, Coin, Tickets } from '@element-plus/icons-vue'
import request from '@/utils/request'

// 收入统计日期范围
const revenueRange = ref([])

// 收入统计数据
const revenueData = reactive({
  totalRevenue: 0,
  todayRevenue: 0,
  orderCount: 0
})

// 搜索表单
const searchForm = reactive({
  orderNo: '',
  userPhone: '',
  status: null,
  timeRange: []
})

// 分页参数
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 金额格式化：保留两位小数
const formatMoney = (value) => {
  if (value === null || value === undefined || value === '') return '0.00'
  const num = Number(value)
  if (isNaN(num)) return '0.00'
  return num.toFixed(2)
}

// 订单状态格式化：1-待支付 2-已支付 3-已取消
const formatStatus = (status) => {
  const map = { 1: '待支付', 2: '已支付', 3: '已取消' }
  return map[status] || '-'
}

// 订单状态对应 tag 类型：1-黄色 2-绿色 3-红色
const statusTagType = (status) => {
  const map = { 1: 'warning', 2: 'success', 3: 'danger' }
  return map[status] || 'info'
}

// 时间格式化
const formatTime = (time) => {
  if (!time) return '-'
  // 已是字符串则替换 T 为空格
  if (typeof time === 'string') return time.replace('T', ' ')
  // 时间戳转 yyyy-MM-dd HH:mm:ss
  const d = new Date(time)
  if (isNaN(d.getTime())) return time
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

// 查询收入统计
const fetchRevenue = async () => {
  try {
    const params = {}
    if (revenueRange.value && revenueRange.value.length === 2) {
      params.begin = revenueRange.value[0]
      params.end = revenueRange.value[1]
    }
    const res = await request({
      url: '/order/revenue',
      method: 'get',
      params
    })
    revenueData.totalRevenue = res.data?.totalRevenue ?? 0
    revenueData.todayRevenue = res.data?.todayRevenue ?? 0
    revenueData.orderCount = res.data?.orderCount ?? 0
  } catch (error) {
    console.error('查询收入统计失败', error)
  }
}

// 查询订单列表
const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    if (searchForm.orderNo) params.orderNo = searchForm.orderNo
    if (searchForm.userPhone) params.userPhone = searchForm.userPhone
    if (searchForm.status !== null && searchForm.status !== undefined && searchForm.status !== '') {
      params.status = searchForm.status
    }
    if (searchForm.timeRange && searchForm.timeRange.length === 2) {
      params.beginTime = searchForm.timeRange[0]
      params.endTime = searchForm.timeRange[1]
    }
    const res = await request({
      url: '/order/conditionSearch',
      method: 'get',
      params
    })
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('查询订单列表失败', error)
  } finally {
    loading.value = false
  }
}

// 收入统计查询
const handleRevenueSearch = () => {
  fetchRevenue()
}

// 订单查询
const handleSearch = () => {
  pagination.page = 1
  fetchList()
}

// 重置搜索条件
const handleReset = () => {
  searchForm.orderNo = ''
  searchForm.userPhone = ''
  searchForm.status = null
  searchForm.timeRange = []
  pagination.page = 1
  fetchList()
}

// 每页条数变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.page = 1
  fetchList()
}

// 页码变化
const handlePageChange = (page) => {
  pagination.page = page
  fetchList()
}

// 查看详情（功能预留）
const handleView = (row) => {
  ElMessage.info('功能开发中')
}

// 初始化加载
onMounted(() => {
  fetchRevenue()
  fetchList()
})
</script>

<style scoped>
.order-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 统计卡片 */
.stats-card {
  border-radius: 4px;
}

.stats-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.stats-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.stats-filter {
  display: flex;
  align-items: center;
  gap: 10px;
}

.stats-date-picker {
  width: 320px;
}

.stats-cards {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.stat-card {
  flex: 1;
  min-width: 220px;
  border-radius: 8px;
  border: none;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  font-size: 48px;
  padding: 12px;
  border-radius: 8px;
}

.stat-icon-green {
  color: #67c23a;
  background: #f0f9eb;
}

.stat-icon-red {
  color: #f56c6c;
  background: #fef0f0;
}

.stat-icon-blue {
  color: #409eff;
  background: #ecf5ff;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 6px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
}

.stat-green {
  color: #67c23a;
}

.stat-red {
  color: #f56c6c;
}

.stat-blue {
  color: #409eff;
}

/* 搜索栏 */
.search-card {
  border-radius: 4px;
}

.search-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.search-left {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.search-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.search-input {
  width: 180px;
}

.search-select {
  width: 160px;
}

.search-date-picker {
  width: 280px;
}

/* 表格卡片 */
.table-card {
  border-radius: 4px;
}

.amount-text {
  color: #f56c6c;
  font-weight: 600;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
