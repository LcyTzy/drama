<template>
  <div class="category-page">
    <!-- 顶部搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="分类名称">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入分类名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="分类类型">
          <el-select
            v-model="queryParams.type"
            placeholder="请选择分类类型"
            clearable
            style="width: 180px"
          >
            <el-option label="短剧分类" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
        <el-form-item class="add-btn-wrapper">
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增分类</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#303133' }"
      >
        <el-table-column label="分类名称" prop="name" min-width="160" />
        <el-table-column label="分类类型" prop="type" min-width="120" align="center">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">短剧分类</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="sort" width="100" align="center" />
        <el-table-column label="状态" prop="status" width="120" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              :loading="row._statusLoading"
              @change="(val) => handleStatusChange(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" min-width="180" align="center" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="480px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="90px"
        class="category-form"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="分类类型">
          <el-tag type="info" effect="plain">短剧分类</el-tag>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="9999" controls-position="right" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'

// 查询参数
const queryParams = reactive({
  name: '',
  type: '',
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增分类')
const submitLoading = ref(false)
const formRef = ref(null)

// 表单数据
const form = reactive({
  id: null,
  name: '',
  sort: 1
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入排序', trigger: 'blur' },
    { type: 'number', message: '排序必须为数字', trigger: 'blur' }
  ]
}

// 分页查询分类列表
const fetchList = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/category/page',
      method: 'get',
      params: queryParams
    })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('查询分类列表失败', error)
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  queryParams.page = 1
  fetchList()
}

// 重置
const handleReset = () => {
  queryParams.name = ''
  queryParams.type = ''
  queryParams.page = 1
  fetchList()
}

// 每页条数变化
const handleSizeChange = (size) => {
  queryParams.pageSize = size
  queryParams.page = 1
  fetchList()
}

// 页码变化
const handlePageChange = (page) => {
  queryParams.page = page
  fetchList()
}

// 新增分类
const handleAdd = () => {
  dialogTitle.value = '新增分类'
  Object.assign(form, { id: null, name: '', sort: 1 })
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (row) => {
  dialogTitle.value = '编辑分类'
  Object.assign(form, { id: row.id, name: row.name, sort: row.sort })
  dialogVisible.value = true
}

// 提交表单（新增/编辑）
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      const isEdit = !!form.id
      await request({
        url: '/category',
        method: isEdit ? 'put' : 'post',
        data: {
          id: form.id,
          name: form.name,
          sort: form.sort
        }
      })
      ElMessage.success(isEdit ? '修改成功' : '新增成功')
      dialogVisible.value = false
      fetchList()
    } catch (error) {
      console.error('保存分类失败', error)
    } finally {
      submitLoading.value = false
    }
  })
}

// 删除分类
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除分类「${row.name}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await request({
          url: '/category',
          method: 'delete',
          params: { id: row.id }
        })
        ElMessage.success('删除成功')
        // 删除后若当前页无数据，回退一页
        if (tableData.value.length === 1 && queryParams.page > 1) {
          queryParams.page--
        }
        fetchList()
      } catch (error) {
        console.error('删除分类失败', error)
      }
    })
    .catch(() => {})
}

// 启用/禁用状态切换
const handleStatusChange = async (row, val) => {
  row._statusLoading = true
  try {
    await request({
      url: `/category/status/${val}`,
      method: 'post',
      params: { id: row.id }
    })
    ElMessage.success(val === 1 ? '已启用' : '已禁用')
  } catch (error) {
    // 失败时回滚状态
    row.status = val === 1 ? 0 : 1
    console.error('状态切换失败', error)
  } finally {
    row._statusLoading = false
  }
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, name: '', sort: 1 })
}

// 页面加载时查询
onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.category-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 搜索栏 */
.search-card {
  border-radius: 4px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.add-btn-wrapper {
  margin-left: auto;
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
.category-form {
  padding-right: 20px;
}
</style>
