<template>
  <el-card>
    <div class="action-bar">
      <div class="search-bar">
        <el-input v-model="search.keyword" placeholder="任务名称关键词" style="width: 200px; margin-right: 8px;" />
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
        <el-select v-model="search.status" placeholder="任务状态" clearable style="width: 120px; margin-right: 8px;">
          <el-option value="PENDING" label="待开始" />
          <el-option value="IN_PROGRESS" label="进行中" />
          <el-option value="COMPLETED" label="已完成" />
          <el-option value="PAUSED" label="已暂停" />
          <el-option value="CANCELLED" label="已取消" />
        </el-select>
        <el-button type="primary" @click="fetchList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      <div class="button-bar">
        <el-button type="primary" @click="handleAdd">新增任务</el-button>
      </div>
    </div>
    
    <el-table :data="list" border stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="title" label="任务名称" width="200" align="center" />
      <el-table-column prop="description" label="任务描述"  align="center" show-overflow-tooltip />
      <el-table-column prop="priority" label="优先级" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getPriorityColor(scope.row.priority)">
            {{ getPriorityText(scope.row.priority) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getStatusColor(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="assignerName" label="指派者" width="120" align="center" />
      <el-table-column prop="assigneeName" label="负责人" width="120" align="center" />
      <el-table-column prop="deadline" label="截止日期" width="120" align="center">
        <template #default="scope">
          {{ formatDate(scope.row.deadline) }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" align="center">
        <template #default="scope">
          {{ formatDateTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right" align="center">
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
    
    <!-- 新增/编辑任务对话框 -->
    <el-dialog 
      v-model="dialog.visible" 
      :title="dialog.form.id ? '编辑任务' : '新增任务'"
      width="800px"
      @close="resetForm"
      class="task-dialog"
    >
      <el-form :model="dialog.form" :rules="rules" ref="formRef" label-width="100px" class="task-form">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="任务名称" prop="title">
              <el-input v-model="dialog.form.title" placeholder="请输入任务名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属项目" prop="projectId">
              <el-select v-model="dialog.form.projectId" placeholder="请选择项目" @change="handleProjectChange">
                <el-option 
                  v-for="project in projectList" 
                  :key="project.id" 
                  :value="project.id" 
                  :label="project.name" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="任务状态" prop="status">
              <el-select v-model="dialog.form.status" placeholder="请选择任务状态">
                <el-option value="PENDING" label="待开始" />
                <el-option value="IN_PROGRESS" label="进行中" />
                <el-option value="COMPLETED" label="已完成" />
                <el-option value="PAUSED" label="已暂停" />
                <el-option value="CANCELLED" label="已取消" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="dialog.form.priority" placeholder="请选择优先级">
                <el-option value="HIGH" label="高" />
                <el-option value="MEDIUM" label="中" />
                <el-option value="LOW" label="低" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="指派者" prop="assignerId">
              <EmployeeSelector
                v-model="selectedAssigner"
                placeholder="请选择指派者"
                @change="handleAssignerChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="assigneeId">
              <EmployeeSelector
                v-model="selectedAssignee"
                placeholder="请选择负责人"
                @change="handleAssigneeChange"
              />
            </el-form-item>
          </el-col>
        </el-row>       
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="截止日期" prop="deadline">
              <el-date-picker 
                v-model="dialog.form.deadline" 
                type="date" 
                placeholder="选择截止日期"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 空列，保持布局平衡 -->
          </el-col>
        </el-row>
        
        <el-form-item label="任务描述" prop="description">
          <el-input 
            v-model="dialog.form.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入任务描述"
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
import { projectTaskApi, projectApi } from '@/api/project'
import EmployeeSelector from '@/components/EmployeeSelector.vue'

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

// 选中的指派者和负责人
const selectedAssigner = ref(null)
const selectedAssignee = ref(null)

const page = reactive({
  current: 1,
  size: 10,
  total: 0
})

const search = reactive({
  keyword: '',
  projectId: props.projectId || null,
  status: ''
})

const dialog = reactive({
  visible: false,
  form: {}
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入任务名称', trigger: 'blur' }
  ],
  projectId: [
    { required: true, message: '请选择项目', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择任务状态', trigger: 'change' }
  ],
  assigneeId: [
    { required: true, message: '请选择负责人', trigger: 'change' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ],
  deadline: [
    { required: true, message: '请选择截止日期', trigger: 'change' }
  ]
}

// 生命周期
// 监听projectId变化
watch(() => props.projectId, (newProjectId) => {
  if (newProjectId) {
    search.projectId = newProjectId
    fetchList()
  }
}, { immediate: true })

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
    console.log('项目列表API响应:', response)
    if (response.code === 200) {
      projectList.value = response.data || []
      console.log('项目列表数据:', projectList.value)
    } else {
      console.error('获取项目列表失败:', response.message)
    }
  } catch (error) {
    console.error('获取项目列表失败:', error)
  }
}

// 获取任务列表
async function fetchList() {
  try {
    loading.value = true
    const params = {
      page: page.current,
      size: page.size,
      projectId: search.projectId,
      type: search.type,
      status: search.status,
      keyword: search.keyword
    }
    
    const response = await projectTaskApi.getPage(params)
    if (response.code === 200) {
      list.value = response.data.list || []
      page.total = response.data.total || 0
      page.current = response.data.pageNum || 1
      page.size = response.data.pageSize || 10
    } else {
      ElMessage.error(response.message || '获取任务列表失败')
    }
  } catch (error) {
    console.error('获取任务列表失败:', error)
    ElMessage.error('获取任务列表失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索
function resetSearch() {
  search.keyword = ''
  search.projectId = null
  search.status = ''
  search.type = ''
  page.current = 1
  fetchList()
}

// 新增任务
function handleAdd() {
  resetForm()
  dialog.form = {
    status: 'PENDING',
    priority: 'MEDIUM',
    estimatedHours: 0
  }
  
  // 如果有projectId，自动设置项目ID
  if (props.projectId) {
    dialog.form.projectId = props.projectId
  }
  
  selectedAssigner.value = null
  selectedAssignee.value = null
  
  // 确保项目列表已加载
  if (projectList.value.length === 0) {
    fetchProjectList()
  }
  
  dialog.visible = true
}

// 编辑任务
function handleEdit(row) {
  dialog.form = { ...row }
  
  // 设置指派者和负责人选择器
  if (row.assignerId) {
    selectedAssigner.value = {
      id: row.assignerId,
      name: row.assignerName
    }
  }
  if (row.assigneeId) {
    selectedAssignee.value = {
      id: row.assigneeId,
      name: row.assigneeName
    }
  }
  
  // 确保项目列表已加载
  if (projectList.value.length === 0) {
    fetchProjectList()
  }
  
  dialog.visible = true
}

// 删除任务
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除任务 "${row.title}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await projectTaskApi.delete(row.id)
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
      response = await projectTaskApi.update(dialog.form)
    } else {
      // 新增
      response = await projectTaskApi.create(dialog.form)
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
  
  // 如果有projectId，自动设置项目ID
  if (props.projectId) {
    dialog.form.projectId = props.projectId
  }
  
  selectedAssigner.value = null
  selectedAssignee.value = null
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

// 处理指派者选择变化
function handleAssignerChange(employee) {
  if (employee) {
    dialog.form.assignerId = employee.id
    dialog.form.assignerName = employee.name
  } else {
    dialog.form.assignerId = null
    dialog.form.assignerName = ''
  }
}

// 处理负责人选择变化
function handleAssigneeChange(employee) {
  if (employee) {
    dialog.form.assigneeId = employee.id
    dialog.form.assigneeName = employee.name
  } else {
    dialog.form.assigneeId = null
    dialog.form.assigneeName = ''
  }
}


// 获取状态颜色
// 获取状态颜色
function getStatusColor(status) {
  const statusMap = {
    'PENDING': 'info',
    'IN_PROGRESS': 'warning',
    'COMPLETED': 'success',
    'PAUSED': 'danger',
    'CANCELLED': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
function getStatusText(status) {
  const statusMap = {
    'PENDING': '待开始',
    'IN_PROGRESS': '进行中',
    'COMPLETED': '已完成',
    'PAUSED': '已暂停',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

// 获取优先级颜色
function getPriorityColor(priority) {
  const priorityMap = {
    'HIGH': 'danger',
    'MEDIUM': 'warning',
    'LOW': 'info'
  }
  return priorityMap[priority] || 'info'
}

// 获取优先级文本
function getPriorityText(priority) {
  const priorityMap = {
    'HIGH': '高',
    'MEDIUM': '中',
    'LOW': '低'
  }
  return priorityMap[priority] || priority
}

// 格式化日期
function formatDate(date) {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
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

/* 任务对话框样式 */
.task-dialog .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px 12px 0 0;
  padding: 20px 24px;
}

.task-dialog .el-dialog__title {
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.task-dialog .el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 18px;
}

.task-dialog .el-dialog__body {
  padding: 24px;
  background: #fafbfc;
}

.task-form .el-form-item {
  margin-bottom: 20px;
}

.task-form .el-input,
.task-form .el-select,
.task-form .el-date-editor,
.task-form .el-input-number {
  width: 100% !important;
}
</style>
