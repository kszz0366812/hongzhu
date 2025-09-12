<template>
  <el-dialog 
    v-model="visible" 
    :title="title"
    width="600px"
    @close="handleClose"
    :close-on-click-modal="!loading"
    :close-on-press-escape="!loading"
  >
    <div class="import-content">
      <!-- 字段说明和文件要求 -->
      <div class="field-description">
        <el-alert
          title=""
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            <div class="description-lines">
              <div class="description-line">• 只支持xls、xlsx两种格式</div>
              <div class="description-line">• 大小不超过100M，超出大小请联系管理员</div>
              <div class="description-line">• 表格数据格式请下载导入模版</div>
            </div>
          </template>
        </el-alert>
      </div>
      
      <!-- 文件上传区域 -->
      <div class="upload-area">
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="file">
            <div class="upload-container">
              <el-upload
                ref="uploadRef"
                :auto-upload="false"
                :limit="1"
                :on-change="handleFileChange"
                :on-remove="handleFileRemove"
                :before-upload="beforeFileUpload"
                accept=".xlsx,.xls"
                drag
                class="large-upload"
                style="width: 100%"
                @click="handleUploadClick"
                :disabled="loading"
              >
                <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                <div class="el-upload__text">
                  <span v-if="loading">导入中，请稍候...</span>
                  <span v-else>将文件拖到此处，或<em>点击上传</em></span>
                </div>
              </el-upload>
            </div>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 上传进度条 -->
      <div v-if="loading" class="upload-progress">
        <el-progress 
          :percentage="progress" 
          :stroke-width="8"
          :show-text="false"
        />
        <p class="progress-text">{{ progressText }}</p>
        <p class="progress-time" v-if="startTime">
          开始时间：{{ startTime }} | 已用时：{{ elapsedTime }}
        </p>
      </div>
    </div>
    
    <!-- 右下角上传按钮 -->
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleDownloadTemplate" :disabled="loading">
          下载模板
        </el-button>
        <el-button 
          type="primary" 
          @click="handleImportSubmit" 
          :loading="loading" 
          :disabled="loading"
          size="large"
        >
          {{ loading ? '导入中...' : '开始导入' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { importExcelData } from '@/api/system'
import ExcelTemplateUtil from '@/utils/excelTemplate'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '导入数据'
  },
  importType: {
    type: String,
    required: true
  },
  maxFileSize: {
    type: Number,
    default: 100 // 默认100MB
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(false)
const formRef = ref()
const uploadRef = ref()
const selectedFile = ref(null)
const loading = ref(false)
const progress = ref(0)
const progressText = ref('')
const startTime = ref('')
const elapsedTime = ref('')
let timeInterval = null

const form = reactive({
  file: null
})

const rules = {
  file: [
    { required: true, message: '请选择要导入的文件', trigger: 'change' }
  ]
}

// 监听外部visible变化
watch(() => props.modelValue, (newVal) => {
  visible.value = newVal
  if (newVal) {
    resetForm()
  }
})

// 监听内部visible变化
watch(visible, (newVal) => {
  emit('update:modelValue', newVal)
  if (!newVal) {
    resetForm()
  }
})

// 格式化时间
function formatTime(seconds) {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}分${remainingSeconds}秒`
}

// 重置表单
function resetForm() {
  console.log('=== resetForm 被调用 ===')
  console.log('重置前的状态:')
  console.log('form.file:', form.file)
  console.log('selectedFile:', selectedFile.value)
  
  form.file = null
  selectedFile.value = null
  loading.value = false
  progress.value = 0
  progressText.value = ''
  startTime.value = ''
  elapsedTime.value = ''
  
  // 清除定时器
  if (timeInterval) {
    clearInterval(timeInterval)
    timeInterval = null
  }
  
  console.log('重置后的状态:')
  console.log('form.file:', form.file)
  console.log('selectedFile:', selectedFile.value)
  
  if (uploadRef.value) {
    console.log('清空上传组件的文件列表')
    uploadRef.value.clearFiles()
  }
  if (formRef.value) {
    console.log('重置表单字段')
    formRef.value.resetFields()
  }
  console.log('=== resetForm 结束 ===')
}

// 关闭对话框
function handleClose() {
  if (!loading.value) {
    visible.value = false
  }
}

// 文件上传前处理
function beforeFileUpload(rawFile) {
  console.log('=== beforeFileUpload 被调用 ===')
  console.log('传入的文件:', rawFile.name)
  
  // 检查文件大小
  const maxSizeMB = props.maxFileSize
  const isLtMaxSize = rawFile.size / 1024 / 1024 < maxSizeMB
  if (!isLtMaxSize) {
    console.log('文件大小检查失败')
    ElMessage.error(`文件大小不能超过 ${maxSizeMB}MB!`)
    return false
  }
  
  // 检查文件类型
  const allowedTypes = ['application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/vnd.ms-excel']
  if (!allowedTypes.includes(rawFile.type)) {
    console.log('文件类型检查失败')
    ElMessage.error('只能上传Excel文件(.xlsx, .xls)')
    return false
  }
  
  console.log('文件验证通过')
  console.log('=== beforeFileUpload 结束 ===')
  return true
}

// 文件上传成功处理
function handleFileChange(file) {
  console.log('=== handleFileChange 被调用 ===')
  console.log('传入的文件对象:', file)
  console.log('文件名称:', file.name)
  console.log('文件大小:', file.size)
  console.log('文件类型:', file.type)
  
  // 更新选中的文件
  selectedFile.value = file.raw
  form.file = file.raw
  
  console.log('更新后的 selectedFile:', selectedFile.value)
  console.log('更新后的 form.file:', form.file)
  console.log('=== handleFileChange 结束 ===')
}

// 文件移除处理
function handleFileRemove(file) {
  console.log('=== handleFileRemove 被调用 ===')
  console.log('移除的文件:', file)
  selectedFile.value = null
  form.file = null
  console.log('文件移除完成')
  console.log('=== handleFileRemove 结束 ===')
}

// 上传区域点击处理
function handleUploadClick() {
  console.log('=== handleUploadClick 被调用 ===')
  // 强制清空上传组件的文件列表，确保下次选择文件时能正确触发change事件
  if (uploadRef.value) {
    console.log('强制清空上传组件的文件列表')
    uploadRef.value.clearFiles()
    // 同时清空我们的状态
    selectedFile.value = null
    form.file = null
  }
  console.log('=== handleUploadClick 结束 ===')
}

// 下载模板
async function handleDownloadTemplate() {
  try {
    const result = await ExcelTemplateUtil.downloadTemplate(props.importType)
    if (result.success) {
      ElMessage.success('模版下载成功')
    } else {
      ElMessage.error(result.message)
    }
  } catch (error) {
    console.error('下载模版失败:', error)
    ElMessage.error('下载模版失败')
  }
}

// 导入提交
async function handleImportSubmit() {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (!selectedFile.value) {
      ElMessage.warning('请选择要导入的Excel文件')
      return
    }
    
    // 开始导入，设置loading状态
    loading.value = true
    progress.value = 10
    progressText.value = '开始导入...'
    
    // 记录开始时间
    const now = new Date()
    startTime.value = now.toLocaleTimeString()
    let elapsedSeconds = 0
    
    // 启动时间计时器
    timeInterval = setInterval(() => {
      elapsedSeconds++
      elapsedTime.value = formatTime(elapsedSeconds)
    }, 1000)
    
    // 模拟进度条
    const progressInterval = setInterval(() => {
      if (progress.value < 90) {
        progress.value += Math.random() * 10
        progressText.value = '正在处理数据...'
      }
    }, 200)
    
    const response = await importExcelData(selectedFile.value, props.importType)
    
    clearInterval(progressInterval)
    progress.value = 100
    progressText.value = '导入完成'
    
    if (response.code === 200) {
      ElMessage.success('数据导入成功！')
      emit('success', response.data)
    } else {
      // 服务端返回错误时，显示错误信息
      ElMessage.error(response.message || '导入失败')
    }
    
    // 无论成功还是失败，都关闭对话框
    visible.value = false
  } catch (error) {
    console.error('导入失败:', error)
    
    // 清除进度计时器
    if (timeInterval) {
      clearInterval(timeInterval)
      timeInterval = null
    }
    
    // 根据错误类型显示不同的提示
    if (error.message === '请求超时') {
      progressText.value = '导入超时，请检查网络连接或稍后重试'
      ElMessage.error('导入超时，请检查网络连接或稍后重试')
    } else if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
      progressText.value = '导入超时，请检查网络连接或稍后重试'
      ElMessage.error('导入超时，请检查网络连接或稍后重试')
    } else if (error.response && error.response.status === 413) {
      progressText.value = '文件过大，请压缩文件或分批导入'
      ElMessage.error('文件过大，请压缩文件或分批导入')
    } else {
      progressText.value = '导入失败，请稍后重试'
      ElMessage.error('导入失败，请稍后重试')
    }
    
    // 异常情况下也关闭对话框
    visible.value = false
  } finally {
    // 清除时间计时器
    if (timeInterval) {
      clearInterval(timeInterval)
      timeInterval = null
    }
    loading.value = false
  }
}

// 组件卸载时清理定时器
onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
})
</script>

<style scoped>
.import-content {
  padding: 20px 0;
}

.field-description {
  margin-bottom: 20px;
}

.description-lines {
  line-height: 1.6;
}

.description-line {
  margin-bottom: 4px;
}

.upload-area {
  margin-bottom: 20px;
}

.upload-container {
  width: 100%;
}

.large-upload {
  width: 100%;
}

.upload-progress {
  margin-top: 20px;
  text-align: center;
}

.progress-text {
  margin-top: 10px;
  color: #666;
  font-size: 14px;
}

.progress-time {
  margin-top: 8px;
  color: #999;
  font-size: 12px;
}

.dialog-footer {
  text-align: right;
}

.dialog-footer .el-button {
  margin-left: 10px;
}

/* 禁用状态下的上传区域样式 */
.large-upload.is-disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.large-upload.is-disabled .el-upload__text {
  color: #999;
}
</style> 