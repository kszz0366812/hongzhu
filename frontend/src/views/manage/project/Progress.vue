<template>
  <el-card>
    <div class="action-bar">
      <div class="search-bar">
        <el-input v-model="search.keyword" placeholder="进度描述关键词" style="width: 200px; margin-right: 8px;" />
        <el-select 
          v-if="!props.projectId"
          v-model="search.projectId" 
          placeholder="选择项目" 
          clearable 
          style="width: 200px; margin-right: 8px;"
        >
          <el-option 
            v-for="project in projectList" 
            :key="project.id" 
            :value="project.id" 
            :label="project.name" 
          />
        </el-select>
        <el-button type="primary" @click="fetchList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      <div class="button-bar">
        <el-button type="primary" @click="handleAdd">新增进度</el-button>
      </div>
    </div>
    
    <el-table :data="list" border stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="projectName" label="项目名称" width="200" align="center" />
      <el-table-column prop="progressContent" label="进度描述" align="center" />
      <el-table-column prop="progressPercentage" label="完成百分比" width="120" align="center">
        <template #default="scope">
          <el-progress :percentage="scope.row.progressPercentage || 0" :stroke-width="8" />
        </template>
      </el-table-column>
      <el-table-column prop="creatorName" label="创建人" width="120" align="center" />
      <el-table-column prop="createTime" label="创建时间" width="160" align="center">
        <template #default="scope">
          {{ formatDateTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="160" align="center">
        <template #default="scope">
          {{ formatDateTime(scope.row.updateTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right" align="center">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination
      v-model:current-page="page.current"
      v-model:page-size="page.size"
      :total="page.total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
      style="margin-top: 16px; text-align: right;"
    />
    
    <!-- 新增/编辑进度对话框 -->
    <el-dialog 
      v-model="dialog.visible" 
      :title="dialog.form.id ? '编辑进度' : '新增进度'"
      width="600px"
      @close="resetForm"
      class="progress-dialog"
    >
      <el-form :model="dialog.form" :rules="rules" ref="formRef" label-width="100px" class="progress-form">
        <el-form-item label="选择项目" prop="projectId">
          <el-select v-model="dialog.form.projectId" placeholder="请选择项目" @change="handleProjectChange">
            <el-option 
              v-for="project in projectList" 
              :key="project.id" 
              :value="project.id" 
              :label="project.projectName" 
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="进度描述" prop="progressContent">
          <el-input 
            v-model="dialog.form.progressContent" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入进度描述"
          />
        </el-form-item>
        
        <el-form-item label="完成百分比" prop="progressPercentage">
          <el-input-number 
            v-model="dialog.form.progressPercentage" 
            :min="0" 
            :max="100" 
            placeholder="完成百分比"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="备注" prop="remarks">
          <el-input 
            v-model="dialog.form.remarks" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { projectProgressApi, projectApi } from '@/api/project'

// 定义props
const props = defineProps({
  projectId: {
    type: [Number, String],
    default: null
  }
})

// 响应式数据
const list = ref([])
const projectList = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const formRef = ref()

const page = reactive({
  current: 1,
  size: 10,
  total: 0
})

const search = reactive({
  keyword: '',
  projectId: props.projectId || null
})

const dialog = reactive({
  visible: false,
  form: {}
})

// 表单验证规则
const rules = {
  projectId: [
    { required: true, message: '请选择项目', trigger: 'change' }
  ],
  progressContent: [
    { required: true, message: '请输入进度描述', trigger: 'blur' }
  ],
  progressPercentage: [
    { required: true, message: '请输入完成百分比', trigger: 'blur' }
  ]
}

// 监听projectId变化
watch(() => props.projectId, (newProjectId) => {
  if (newProjectId) {
    search.projectId = newProjectId
    fetchList()
  }
}, { immediate: true })

// 生命周期
onMounted(() => {
  if (!props.projectId) {
    fetchProjectList()
  }
  fetchList()
})

// 获取项目列表
async function fetchProjectList() {
  try {
    const response = await projectApi.getAll()
    if (response.code === 200) {
      projectList.value = response.data || []
    }
  } catch (error) {
    console.error('获取项目列表失败:', error)
  }
}

// 获取进度列表
async function fetchList() {
  try {
    loading.value = true
    const params = {
      page: page.current,
      size: page.size,
      projectId: search.projectId,
      keyword: search.keyword
    }
    
    const response = await projectProgressApi.getPage(params)
    if (response.code === 200) {
      list.value = response.data.records || []
      page.total = response.data.total || 0
      page.current = response.data.current || 1
      page.size = response.data.size || 10
    } else {
      ElMessage.error(response.message || '获取进度列表失败')
    }
  } catch (error) {
    console.error('获取进度列表失败:', error)
    ElMessage.error('获取进度列表失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索
function resetSearch() {
  search.keyword = ''
  search.projectId = null
  page.current = 1
  fetchList()
}

// 新增进度
function handleAdd() {
  resetForm()
  dialog.form = {
    progressPercentage: 0
  }
  dialog.visible = true
}

// 编辑进度
function handleEdit(row) {
  dialog.form = { ...row }
  dialog.visible = true
}

// 删除进度
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除这条进度记录吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await projectProgressApi.delete(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    let response
    if (dialog.form.id) {
      // 编辑
      response = await projectProgressApi.update(dialog.form)
    } else {
      // 新增
      response = await projectProgressApi.create(dialog.form)
    }
    
    if (response.code === 200) {
      ElMessage.success(dialog.form.id ? '更新成功' : '新增成功')
      dialog.visible = false
      fetchList()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交失败:', error)
      ElMessage.error('提交失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
function resetForm() {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  dialog.form = {}
}

// 分页处理
function handleCurrentChange(currentPage) {
  page.current = currentPage
  fetchList()
}

function handleSizeChange(size) {
  page.size = size
  page.current = 1
  fetchList()
}

// 处理项目选择变化
function handleProjectChange(projectId) {
  const project = projectList.value.find(p => p.id === projectId)
  if (project) {
    dialog.form.projectName = project.projectName
  }
}

// 格式化日期时间
function formatDateTime(date) {
  if (!date) return '-'
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}
</script>

<style scoped>
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.search-bar {
  display: flex;
  align-items: center;
}

.button-bar {
  display: flex;
  gap: 8px;
}

.el-table {
  margin-bottom: 16px;
}

/* 进度对话框样式 */
.progress-dialog .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px 12px 0 0;
  padding: 20px 24px;
}

.progress-dialog .el-dialog__title {
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.progress-dialog .el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 18px;
}

.progress-dialog .el-dialog__body {
  padding: 24px;
  background: #fafbfc;
}

.progress-form .el-form-item {
  margin-bottom: 20px;
}

.progress-form .el-input,
.progress-form .el-select,
.progress-form .el-input-number {
  width: 100% !important;
}
</style>
