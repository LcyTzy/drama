<template>
  <div class="drama-page">
    <!-- 顶部搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="剧名">
          <el-input
            v-model="queryParams.title"
            placeholder="请输入剧名"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select
            v-model="queryParams.categoryId"
            placeholder="请选择分类"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="item in categoryOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            style="width: 140px"
          >
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
        <el-form-item class="add-btn-wrapper">
          <el-button type="primary" :icon="Plus" @click="handleAdd">+ 新增短剧</el-button>
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
        :header-cell-style="{ background: '#f5f7fa', color: '#303133', fontWeight: 600 }"
      >
        <el-table-column label="封面" width="110" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.coverUrl"
              :src="row.coverUrl"
              :preview-src-list="[row.coverUrl]"
              preview-teleported
              fit="cover"
              class="cover-img"
            />
            <span v-else class="empty-cover">暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="剧名" min-width="160" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" min-width="120" align="center">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">{{ row.categoryName || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalEpisodes" label="总集数" width="90" align="center" />
        <el-table-column prop="views" label="播放量" width="110" align="center">
          <template #default="{ row }">
            {{ formatViews(row.views) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
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
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link :icon="VideoPlay" @click="handleManageEpisode(row)">管理剧集</el-button>
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
      width="560px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="drama-form"
      >
        <template v-if="!isEdit">
          <!-- 新增表单 -->
          <el-form-item label="分类" prop="categoryId">
            <el-select
              v-model="form.categoryId"
              placeholder="请选择分类"
              style="width: 100%"
            >
              <el-option
                v-for="item in categoryOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="剧名" prop="title">
            <el-input v-model="form.title" placeholder="请输入剧名" maxlength="50" show-word-limit />
          </el-form-item>
          <el-form-item label="封面图" prop="coverUrl">
            <el-upload
              class="cover-uploader"
              :action="uploadAction"
              :headers="uploadHeaders"
              name="file"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              accept="image/*"
            >
              <el-image
                v-if="form.coverUrl"
                :src="form.coverUrl"
                fit="cover"
                class="cover-preview"
              />
              <div v-else class="cover-placeholder">
                <el-icon class="cover-icon"><Plus /></el-icon>
                <span class="cover-text">点击上传</span>
              </div>
            </el-upload>
            <el-input v-model="form.coverUrl" placeholder="封面图地址" style="margin-top: 8px" clearable />
          </el-form-item>
          <el-form-item label="简介" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="3"
              placeholder="请输入简介"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="导演" prop="director">
            <el-input v-model="form.director" placeholder="请输入导演" maxlength="50" />
          </el-form-item>
          <el-form-item label="主演" prop="actor">
            <el-input v-model="form.actor" placeholder="请输入主演" maxlength="100" />
          </el-form-item>
          <el-form-item label="价格类型" prop="priceType">
            <el-radio-group v-model="form.priceType">
              <el-radio :value="1">按集解锁</el-radio>
              <el-radio :value="2">整部买断</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="form.priceType === 1" label="单集价格" prop="pricePerEpisode">
            <el-input-number
              v-model="form.pricePerEpisode"
              :min="0"
              :precision="2"
              :step="0.5"
              controls-position="right"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item v-if="form.priceType === 2" label="整部价格" prop="packagePrice">
            <el-input-number
              v-model="form.packagePrice"
              :min="0"
              :precision="2"
              :step="1"
              controls-position="right"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="免费试看集" prop="freeEpisodes">
            <el-input-number
              v-model="form.freeEpisodes"
              :min="0"
              :max="99"
              controls-position="right"
              style="width: 100%"
            />
          </el-form-item>
        </template>

        <template v-else>
          <!-- 编辑表单 -->
          <el-form-item label="分类" prop="categoryId">
            <el-select
              v-model="form.categoryId"
              placeholder="请选择分类"
              style="width: 100%"
            >
              <el-option
                v-for="item in categoryOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="剧名" prop="title">
            <el-input v-model="form.title" placeholder="请输入剧名" maxlength="50" show-word-limit />
          </el-form-item>
          <el-form-item label="封面图" prop="coverUrl">
            <el-upload
              class="cover-uploader"
              :action="uploadAction"
              :headers="uploadHeaders"
              name="file"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              accept="image/*"
            >
              <el-image
                v-if="form.coverUrl"
                :src="form.coverUrl"
                fit="cover"
                class="cover-preview"
              />
              <div v-else class="cover-placeholder">
                <el-icon class="cover-icon"><Plus /></el-icon>
                <span class="cover-text">点击上传</span>
              </div>
            </el-upload>
            <el-input v-model="form.coverUrl" placeholder="封面图地址" style="margin-top: 8px" clearable />
          </el-form-item>
          <el-form-item label="简介" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="3"
              placeholder="请输入简介"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="导演" prop="director">
            <el-input v-model="form.director" placeholder="请输入导演" maxlength="50" />
          </el-form-item>
          <el-form-item label="主演" prop="actor">
            <el-input v-model="form.actor" placeholder="请输入主演" maxlength="100" />
          </el-form-item>
          <el-form-item label="价格类型" prop="priceType">
            <el-radio-group v-model="form.priceType">
              <el-radio :value="1">按集解锁</el-radio>
              <el-radio :value="2">整部买断</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="form.priceType === 1" label="单集价格" prop="pricePerEpisode">
            <el-input-number
              v-model="form.pricePerEpisode"
              :min="0"
              :precision="2"
              :step="0.5"
              controls-position="right"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item v-if="form.priceType === 2" label="整部价格" prop="packagePrice">
            <el-input-number
              v-model="form.packagePrice"
              :min="0"
              :precision="2"
              :step="1"
              controls-position="right"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="免费试看集" prop="freeEpisodes">
            <el-input-number
              v-model="form.freeEpisodes"
              :min="0"
              :max="99"
              controls-position="right"
              style="width: 100%"
            />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, VideoPlay, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  title: '',
  categoryId: '',
  status: '',
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

// 分类下拉数据
const categoryOptions = ref([])

// 对话框相关
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

// 表单数据
const form = reactive({
  id: null,
  categoryId: '',
  title: '',
  coverUrl: '',
  description: '',
  director: '',
  actor: '',
  priceType: 1,
  pricePerEpisode: 1,
  packagePrice: 9.9,
  freeEpisodes: 3
})

// 是否为编辑模式
const isEdit = computed(() => !!form.id)

// 对话框标题
const dialogTitle = computed(() => (isEdit.value ? '编辑短剧' : '新增短剧'))

// 上传地址与请求头（携带 token）
const uploadAction = '/api/common/upload'
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { token } : {}
})

// 表单校验规则
const rules = {
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  title: [{ required: true, message: '请输入剧名', trigger: 'blur' }],
  priceType: [{ required: true, message: '请选择价格类型', trigger: 'change' }],
  pricePerEpisode: [{ required: true, message: '请输入单集价格', trigger: 'blur' }],
  packagePrice: [{ required: true, message: '请输入整部价格', trigger: 'blur' }]
}

// 播放量格式化：超过 10000 显示万
const formatViews = (views) => {
  if (views == null) return '0'
  if (views >= 10000) {
    return (views / 10000).toFixed(1) + '万'
  }
  return String(views)
}

// 分页查询短剧列表
const fetchList = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/drama/page',
      method: 'get',
      params: queryParams
    })
    tableData.value = (res.data?.records || []).map((item) => ({
      ...item,
      _statusLoading: false
    }))
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('查询短剧列表失败', error)
  } finally {
    loading.value = false
  }
}

// 加载分类下拉数据
const fetchCategoryOptions = async () => {
  try {
    const res = await request({
      url: '/category/page',
      method: 'get',
      params: { page: 1, pageSize: 100 }
    })
    categoryOptions.value = res.data?.records || []
  } catch (error) {
    console.error('查询分类列表失败', error)
  }
}

// 查询
const handleQuery = () => {
  queryParams.page = 1
  fetchList()
}

// 重置
const handleReset = () => {
  queryParams.title = ''
  queryParams.categoryId = ''
  queryParams.status = ''
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

// 新增短剧
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑短剧
const handleEdit = (row) => {
  resetForm()
  form.id = row.id
  form.categoryId = row.categoryId || ''
  form.title = row.title
  form.coverUrl = row.coverUrl || ''
  form.description = row.description || ''
  form.director = row.director || ''
  form.actor = row.actor || ''
  form.priceType = row.priceType || 1
  form.pricePerEpisode = row.pricePerEpisode || 1
  form.packagePrice = row.packagePrice || 9.9
  form.freeEpisodes = row.freeEpisodes || 3
  dialogVisible.value = true
}

// 跳转到剧集管理
const handleManageEpisode = (row) => {
  router.push({ path: '/episode', query: { dramaId: row.id } })
}

// 删除短剧
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除短剧「${row.title}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await request({
          url: '/drama',
          method: 'delete',
          params: { id: row.id }
        })
        ElMessage.success('删除成功')
        fetchList()
      } catch (error) {
        console.error('删除短剧失败', error)
      }
    })
    .catch(() => {})
}

// 上下架状态切换
const handleStatusChange = async (row, val) => {
  row._statusLoading = true
  try {
    await request({
      url: `/drama/status/${val}`,
      method: 'post',
      params: { id: row.id }
    })
    ElMessage.success(val === 1 ? '已上架' : '已下架')
  } catch (error) {
    // 失败时回滚状态
    row.status = val === 1 ? 0 : 1
    console.error('状态切换失败', error)
  } finally {
    row._statusLoading = false
  }
}

// 上传前校验：限制图片类型与大小（2MB）
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('封面图仅支持图片格式')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('封面图大小不能超过 2MB')
    return false
  }
  return true
}

// 上传成功回调
const handleUploadSuccess = (response) => {
  // 后端返回 {code, data, msg}，request 拦截器已处理 code === 1
  if (response && response.code === 1) {
    form.coverUrl = response.data
    ElMessage.success('封面图上传成功')
  } else {
    ElMessage.error(response?.msg || '封面图上传失败')
  }
}

// 上传失败回调
const handleUploadError = () => {
  ElMessage.error('封面图上传失败')
}

// 提交表单（新增/编辑）
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value) {
        await request({
          url: '/drama',
          method: 'put',
          data: {
            id: form.id,
            categoryId: form.categoryId,
            title: form.title,
            coverUrl: form.coverUrl,
            description: form.description,
            director: form.director,
            actor: form.actor,
            priceType: form.priceType,
            pricePerEpisode: form.priceType === 1 ? form.pricePerEpisode : undefined,
            packagePrice: form.priceType === 2 ? form.packagePrice : undefined,
            freeEpisodes: form.freeEpisodes
          }
        })
        ElMessage.success('编辑成功')
      } else {
        // 新增
        await request({
          url: '/drama',
          method: 'post',
          data: {
            categoryId: form.categoryId,
            title: form.title,
            coverUrl: form.coverUrl,
            description: form.description,
            director: form.director,
            actor: form.actor,
            priceType: form.priceType,
            pricePerEpisode: form.priceType === 1 ? form.pricePerEpisode : undefined,
            packagePrice: form.priceType === 2 ? form.packagePrice : undefined,
            freeEpisodes: form.freeEpisodes
          }
        })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      fetchList()
    } catch (error) {
      console.error('保存短剧失败', error)
    } finally {
      submitLoading.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  form.id = null
  form.categoryId = ''
  form.title = ''
  form.coverUrl = ''
  form.description = ''
  form.director = ''
  form.actor = ''
  form.priceType = 1
  form.pricePerEpisode = 1
  form.packagePrice = 9.9
  form.freeEpisodes = 3
  formRef.value?.clearValidate()
}

// 页面加载时初始化
onMounted(() => {
  fetchList()
  fetchCategoryOptions()
})
</script>

<style scoped>
.drama-page {
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

/* 封面缩略图 */
.cover-img {
  width: 80px;
  height: 100px;
  border-radius: 4px;
  display: block;
  margin: 0 auto;
  cursor: pointer;
}

.empty-cover {
  color: #c0c4cc;
  font-size: 12px;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

/* 对话框表单 */
.drama-form {
  padding-right: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 封面上传组件 */
.cover-uploader {
  width: 100%;
}

.cover-uploader :deep(.el-upload) {
  width: 120px;
  height: 150px;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  transition: border-color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}

.cover-preview {
  width: 120px;
  height: 150px;
  display: block;
}

.cover-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
  gap: 6px;
}

.cover-icon {
  font-size: 28px;
}

.cover-text {
  font-size: 12px;
}
</style>
