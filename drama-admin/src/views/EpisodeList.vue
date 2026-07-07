<template>
  <div class="episode-page">
    <!-- 顶部搜索栏 -->
    <el-card shadow="never" class="search-card">
      <div class="search-bar">
        <div class="search-left">
          <span class="search-label">短剧ID：</span>
          <el-input
            v-model="dramaId"
            placeholder="请输入短剧ID"
            clearable
            class="search-input"
            @keyup.enter="handleQuery"
          />
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
        </div>
        <div class="search-right">
          <el-button type="primary" :icon="Plus" @click="handleAdd">+ 上传剧集</el-button>
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
        <el-table-column prop="episodeNum" label="集数" width="100" align="center" />
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.title || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长" width="130" align="center">
          <template #default="{ row }">
            {{ formatDuration(row.duration) }}
          </template>
        </el-table-column>
        <el-table-column prop="isFree" label="是否免费" width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isFree === 1" type="success" effect="dark">免费</el-tag>
            <el-tag v-else type="danger" effect="dark">付费</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180" align="center" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 批量上传剧集对话框 -->
    <el-dialog
      v-model="addDialogVisible"
      title="上传剧集"
      width="560px"
      :close-on-click-modal="false"
      @closed="resetAddForm"
    >
      <el-form
        ref="addFormRef"
        :model="addForm"
        :rules="addRules"
        label-width="90px"
        class="episode-form"
      >
        <el-form-item label="短剧ID" prop="dramaId">
          <el-input v-model="addForm.dramaId" placeholder="请输入短剧ID" />
        </el-form-item>
        <el-form-item label="起始集数" prop="startEpisodeNum">
          <el-input-number
            v-model="addForm.startEpisodeNum"
            :min="1"
            :max="9999"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="视频文件" prop="videoFiles" required>
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :file-list="fileList"
            multiple
            accept="video/*"
          >
            <el-button type="primary" :icon="UploadFilled">选择视频</el-button>
            <template #tip>
              <div class="el-upload__tip">支持 MP4、AVI 等视频格式，可多选批量上传</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="是否免费" prop="isFree">
          <el-switch
            v-model="addForm.isFree"
            :active-value="1"
            :inactive-value="0"
            active-text="免费"
            inactive-text="付费"
          />
        </el-form-item>
      </el-form>
      <!-- 上传进度 -->
      <div v-if="uploadStatus" style="margin-bottom: 16px">
        <el-progress
          :percentage="uploadProgress"
          :status="uploadStatus === 'done' ? 'success' : uploadStatus === 'error' ? 'exception' : undefined"
          :stroke-width="18"
        />
        <div style="text-align: center; margin-top: 8px; color: #606266; font-size: 13px">
          {{ uploadStatus }}
        </div>
      </div>
      <template #footer>
        <el-button @click="addDialogVisible = false" :disabled="submitLoading">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleAddSubmit">确 定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑剧集对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑剧集"
      width="480px"
      :close-on-click-modal="false"
      @closed="resetEditForm"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="90px"
        class="episode-form"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="editForm.title"
            placeholder="请输入标题"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="是否免费" prop="isFree">
          <el-switch
            v-model="editForm.isFree"
            :active-value="1"
            :inactive-value="0"
            active-text="免费"
            inactive-text="付费"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleEditSubmit">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, UploadFilled } from '@element-plus/icons-vue'
import request from '@/utils/request'

const route = useRoute()

// 顶部短剧ID（优先从路由 query 读取）
const dramaId = ref(route.query.dramaId || '')

// 表格数据
const tableData = ref([])
const loading = ref(false)

// ===== 新增剧集对话框 =====
const addDialogVisible = ref(false)
const addFormRef = ref(null)
const uploadRef = ref(null)
const submitLoading = ref(false)
const fileList = ref([])

const addForm = reactive({
  dramaId: '',
  startEpisodeNum: 1,
  videoFiles: [],
  isFree: 0
})

// 视频文件自定义校验
const validateVideoFiles = (rule, value, callback) => {
  if (fileList.value.length === 0) {
    callback(new Error('请选择视频文件'))
  } else {
    callback()
  }
}

const addRules = {
  dramaId: [
    { required: true, message: '请输入短剧ID', trigger: 'blur' }
  ],
  startEpisodeNum: [
    { required: true, message: '请输入起始集数', trigger: 'blur' }
  ],
  videoFiles: [
    { validator: validateVideoFiles, trigger: 'change' }
  ]
}

// ===== 编辑剧集对话框 =====
const editDialogVisible = ref(false)
const editFormRef = ref(null)

const editForm = reactive({
  id: null,
  title: '',
  isFree: 0
})

const editRules = {
  title: [
    { max: 50, message: '标题长度不能超过50个字符', trigger: 'blur' }
  ]
}

// 时长格式化：秒 → X分Y秒
const formatDuration = (seconds) => {
  if (seconds === null || seconds === undefined || seconds === '') return '-'
  const sec = Number(seconds)
  if (isNaN(sec) || sec < 0) return '-'
  const minutes = Math.floor(sec / 60)
  const remainSec = sec % 60
  if (minutes === 0) {
    return `${remainSec}秒`
  }
  return `${minutes}分${remainSec.toString().padStart(2, '0')}秒`
}

// 查询剧集列表
const fetchList = async () => {
  if (!dramaId.value) {
    ElMessage.warning('请输入短剧ID')
    return
  }
  loading.value = true
  try {
    const res = await request({
      url: '/episode/list',
      method: 'get',
      params: { dramaId: dramaId.value }
    })
    tableData.value = res.data || []
  } catch (error) {
    console.error('查询剧集列表失败', error)
  } finally {
    loading.value = false
  }
}

// 顶部查询按钮
const handleQuery = () => {
  fetchList()
}

// 文件选择变化
const handleFileChange = (file, files) => {
  fileList.value = files
  addForm.videoFiles = files
  addFormRef.value?.validateField('videoFiles')
}

// 文件移除
const handleFileRemove = (file, files) => {
  fileList.value = files
  addForm.videoFiles = files
  addFormRef.value?.validateField('videoFiles')
}

// 打开新增对话框
const handleAdd = () => {
  fileList.value = []
  Object.assign(addForm, {
    dramaId: dramaId.value,
    startEpisodeNum: 1,
    videoFiles: [],
    isFree: 0
  })
  addDialogVisible.value = true
}

// 上传进度
const uploadProgress = ref(0)
const uploadStatus = ref('') // 'uploading' | 'saving' | 'done' | 'error'

// 直传 OSS
const uploadToOss = (file, signature) => {
  return new Promise((resolve, reject) => {
    const extension = file.name.substring(file.name.lastIndexOf('.'))
    const objectName = signature.dir + UUID() + extension

    const formData = new FormData()
    formData.append('key', objectName)
    formData.append('policy', signature.policy)
    formData.append('OSSAccessKeyId', signature.accessKeyId)
    formData.append('signature', signature.signature)
    formData.append('success_action_status', '200')
    formData.append('file', file)

    const xhr = new XMLHttpRequest()
    xhr.open('POST', signature.host, true)

    xhr.upload.onprogress = (e) => {
      if (e.lengthComputable) {
        uploadProgress.value = Math.round((e.loaded / e.total) * 100)
      }
    }

    xhr.onload = () => {
      if (xhr.status === 200 || xhr.status === 204) {
        resolve(signature.host + '/' + objectName)
      } else {
        reject(new Error(`OSS上传失败: ${xhr.status}`))
      }
    }

    xhr.onerror = () => reject(new Error('OSS上传网络错误'))
    xhr.send(formData)
  })
}

// 生成简易 UUID
const UUID = () => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = Math.random() * 16 | 0
    return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16)
  })
}

// 获取视频时长
const getVideoDuration = (file) => {
  return new Promise((resolve) => {
    const video = document.createElement('video')
    video.preload = 'metadata'
    video.onloadedmetadata = () => {
      window.URL.revokeObjectURL(video.src)
      resolve(Math.ceil(video.duration))
    }
    video.onerror = () => resolve(0)
    video.src = URL.createObjectURL(file)
  })
}

// 批量提交上传（直传 OSS）
const handleAddSubmit = async () => {
  if (!addFormRef.value) return
  await addFormRef.value.validate(async (valid) => {
    if (!valid) return
    if (fileList.value.length === 0) {
      ElMessage.error('请选择视频文件')
      return
    }
    submitLoading.value = true
    uploadProgress.value = 0
    uploadStatus.value = 'uploading'
    let successCount = 0
    let failCount = 0
    try {
      // 1. 获取 OSS 签名
      const sigRes = await request({ url: '/common/oss-signature', method: 'get' })
      const signature = sigRes.data

      // 2. 逐个直传 OSS 并保存
      for (let i = 0; i < fileList.value.length; i++) {
        const file = fileList.value[i]
        const episodeNum = addForm.startEpisodeNum + i
        uploadStatus.value = `上传第${episodeNum}集...`
        uploadProgress.value = 0

        try {
          // 获取视频时长
          const duration = await getVideoDuration(file.raw)

          // 直传 OSS
          const videoUrl = await uploadToOss(file.raw, signature)

          // 保存剧集信息
          uploadStatus.value = `保存第${episodeNum}集信息...`
          await request({
            url: '/episode',
            method: 'post',
            data: {
              dramaId: addForm.dramaId,
              episodeNum: episodeNum,
              title: file.name.replace(/\.[^.]+$/, ''),
              videoUrl: videoUrl,
              duration: duration,
              fileSize: file.raw.size,
              isFree: addForm.isFree
            }
          })
          successCount++
        } catch (error) {
          console.error(`第${episodeNum}集上传失败`, error)
          failCount++
        }
      }

      uploadStatus.value = 'done'
      if (failCount === 0) {
        ElMessage.success(`成功上传 ${successCount} 集`)
      } else {
        ElMessage.warning(`成功 ${successCount} 集，失败 ${failCount} 集`)
      }
      addDialogVisible.value = false
      dramaId.value = addForm.dramaId
      fetchList()
    } catch (error) {
      uploadStatus.value = 'error'
      ElMessage.error('获取上传签名失败')
      console.error('获取签名失败', error)
    } finally {
      submitLoading.value = false
      uploadProgress.value = 0
      uploadStatus.value = ''
    }
  })
}

// 重置新增表单
const resetAddForm = () => {
  addFormRef.value?.resetFields()
  fileList.value = []
  addForm.videoFiles = []
  uploadRef.value?.clearFiles()
}

// 打开编辑对话框
const handleEdit = (row) => {
  Object.assign(editForm, {
    id: row.id,
    title: row.title || '',
    isFree: row.isFree
  })
  editDialogVisible.value = true
}

// 提交编辑
const handleEditSubmit = async () => {
  if (!editFormRef.value) return
  await editFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      await request({
        url: '/episode',
        method: 'put',
        data: {
          id: editForm.id,
          title: editForm.title,
          isFree: editForm.isFree
        }
      })
      ElMessage.success('修改成功')
      editDialogVisible.value = false
      fetchList()
    } catch (error) {
      console.error('修改剧集失败', error)
    } finally {
      submitLoading.value = false
    }
  })
}

// 重置编辑表单
const resetEditForm = () => {
  editFormRef.value?.resetFields()
  Object.assign(editForm, { id: null, title: '', isFree: 0 })
}

// 删除剧集
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除第「${row.episodeNum}」集吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await request({
          url: '/episode',
          method: 'delete',
          params: { id: row.id }
        })
        ElMessage.success('删除成功')
        fetchList()
      } catch (error) {
        console.error('删除剧集失败', error)
      }
    })
    .catch(() => {})
}

// 页面加载时，若路由带有 dramaId 则自动查询
onMounted(() => {
  if (dramaId.value) {
    fetchList()
  }
})
</script>

<style scoped>
.episode-page {
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
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.search-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-label {
  font-size: 14px;
  color: #303133;
  white-space: nowrap;
}

.search-input {
  width: 240px;
}

/* 表格卡片 */
.table-card {
  border-radius: 4px;
}

/* 对话框表单 */
.episode-form {
  padding-right: 20px;
}
</style>
