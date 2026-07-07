<template>
  <div class="employee-page">
    <!-- 顶部搜索栏 -->
    <el-card shadow="never" class="search-card">
      <div class="search-bar">
        <div class="search-left">
          <span class="search-label">员工姓名：</span>
          <el-input
            v-model="searchForm.name"
            placeholder="请输入员工姓名"
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </div>
        <div class="search-right">
          <el-button type="primary" :icon="Plus" @click="openAddDialog">+ 新增员工</el-button>
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
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="sex" label="性别" width="80" align="center">
          <template #default="{ row }">
            {{ formatSex(row.sex) }}
          </template>
        </el-table-column>
        <el-table-column prop="idNumber" label="身份证号" min-width="180" />
        <el-table-column prop="status" label="账号状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="(val) => handleStatusChange(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="openEditDialog(row)">编辑</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        class="employee-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :disabled="isEdit"
            maxlength="20"
          />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="form.sex">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="身份证号" prop="idNumber">
          <el-input v-model="form.idNumber" placeholder="请输入身份证号" maxlength="18" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit } from '@element-plus/icons-vue'
import request from '@/utils/request'

// 搜索表单
const searchForm = reactive({
  name: ''
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

// 对话框相关
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)

// 表单数据
const form = reactive({
  id: null,
  username: '',
  name: '',
  phone: '',
  sex: 1,
  idNumber: ''
})

// 是否为编辑模式
const isEdit = computed(() => !!form.id)

// 对话框标题
const dialogTitle = computed(() => (isEdit.value ? '编辑员工' : '新增员工'))

// 表单校验规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3-20 个字符', trigger: 'blur' }
  ],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的 11 位手机号', trigger: 'blur' }
  ],
  sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
  idNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^\d{17}[\dXx]$/, message: '请输入正确的 18 位身份证号', trigger: 'blur' }
  ]
}

// 性别格式化：1-男 2-女
const formatSex = (sex) => {
  if (sex === 1) return '男'
  if (sex === 2) return '女'
  return '-'
}

// 时间格式化
const formatTime = (time) => {
  if (!time) return '-'
  // 已是字符串则直接返回
  if (typeof time === 'string') return time.replace('T', ' ')
  // 时间戳转 yyyy-MM-dd HH:mm:ss
  const d = new Date(time)
  if (isNaN(d.getTime())) return time
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

// 查询员工列表
const fetchList = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/employee/page',
      method: 'get',
      params: {
        name: searchForm.name,
        page: pagination.page,
        pageSize: pagination.pageSize
      }
    })
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('查询员工列表失败', error)
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  pagination.page = 1
  fetchList()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
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

// 启用/禁用状态切换
const handleStatusChange = async (row, val) => {
  const targetStatus = val
  try {
    await ElMessageBox.confirm(
      `确定要${targetStatus === 1 ? '启用' : '禁用'}员工「${row.name}」吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await request({
      url: `/employee/status/${targetStatus}`,
      method: 'post',
      params: { id: row.id }
    })
    ElMessage.success(`${targetStatus === 1 ? '启用' : '禁用'}成功`)
  } catch (error) {
    // 用户取消或请求失败，回滚开关状态
    row.status = targetStatus === 1 ? 0 : 1
  }
}

// 打开新增对话框
const openAddDialog = () => {
  resetForm()
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (row) => {
  resetForm()
  form.id = row.id
  form.username = row.username
  form.name = row.name
  form.phone = row.phone
  form.sex = row.sex
  form.idNumber = row.idNumber
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  form.id = null
  form.username = ''
  form.name = ''
  form.phone = ''
  form.sex = 1
  form.idNumber = ''
  formRef.value?.clearValidate()
}

// 提交表单（新增/编辑）
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (isEdit.value) {
        // 编辑：仅提交可修改字段
        await request({
          url: '/employee',
          method: 'put',
          data: {
            id: form.id,
            name: form.name,
            phone: form.phone,
            sex: form.sex,
            idNumber: form.idNumber
          }
        })
        ElMessage.success('编辑成功')
      } else {
        // 新增
        await request({
          url: '/employee',
          method: 'post',
          data: {
            username: form.username,
            name: form.name,
            phone: form.phone,
            sex: form.sex,
            idNumber: form.idNumber
          }
        })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      fetchList()
    } catch (error) {
      console.error('保存员工失败', error)
    } finally {
      submitting.value = false
    }
  })
}

// 初始化加载
onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.employee-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 搜索栏 */
.search-card {
  border-radius: 4px;
}

.search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}

.search-left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.search-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.search-input {
  width: 220px;
}

/* 表格卡片 */
.table-card {
  border-radius: 4px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

/* 对话框表单 */
.employee-form {
  padding-right: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
