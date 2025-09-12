<template>
  <el-card>
    <div class="action-bar">
      <div class="search-bar">
        <el-input v-model="search.keyword" placeholder="项目名称或关键词" style="width: 200px; margin-right: 8px;" />
        <el-select v-model="search.status" placeholder="项目状态" clearable style="width: 120px; margin-right: 8px;">
          <el-option value="PENDING" label="待开始" />
          <el-option value="ONGOING" label="进行中" />
          <el-option value="COMPLETED" label="已完成" />
          <el-option value="OVERDUE" label="已逾期" />
        </el-select>
        <el-button type="primary" @click="fetchList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      <div class="button-bar">
        <el-button type="primary" @click="handleAdd">新增项目</el-button>     
      </div>
    </div>
    
    <el-table :data="list" border stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="name" label="项目名称" width="200" align="center" />
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getPriorityType(scope.row.priority)">
            {{ getPriorityText(scope.row.priority) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="level" label="项目级别" width="120" align="center">
        <template #default="scope">
          <el-tag :type="getLevelType(scope.row.level)">
            {{ getLevelText(scope.row.level) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="managerName" label="项目经理" width="120" align="center" />
      <el-table-column prop="startDate" label="开始日期" width="120" align="center">
        <template #default="scope">
          {{ formatDate(scope.row.startDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="endDate" label="结束日期" width="120" align="center">
        <template #default="scope">
          {{ formatDate(scope.row.endDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="progress" label="进度" width="120" align="center">
        <template #default="scope">
          <el-progress :percentage="scope.row.progress || 0" :stroke-width="8" />
        </template>
      </el-table-column>
      <el-table-column prop="members" label="成员数" width="80" align="center">
        <template #default="scope">
          {{ scope.row.members ? scope.row.members.length : 0 }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" align="center">
        <template #default="scope">
          {{ formatDateTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right" align="center">
        <template #default="scope">
          <el-button size="small" @click="handleView(scope.row)">查看</el-button>
          <el-button size="small" type="primary" @click="handleProgress(scope.row)">进度</el-button>
          <el-button size="small" type="warning" @click="handleTask(scope.row)">任务</el-button>
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
    
    <!-- 新增/编辑项目对话框 -->
    <el-dialog 
      v-model="dialog.visible" 
      :title="dialog.form.id ? '编辑项目' : '新增项目'"
      width="800px"
      @close="resetForm"
      class="project-dialog"
    >
      <el-form :model="dialog.form" :rules="rules" ref="formRef" label-width="100px" class="project-form">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="项目名称" prop="name">
              <el-input v-model="dialog.form.name" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目状态" prop="status">
              <el-select v-model="dialog.form.status" placeholder="请选择项目状态">
                <el-option value="PENDING" label="待开始" />
                <el-option value="ONGOING" label="进行中" />
                <el-option value="COMPLETED" label="已完成" />
                <el-option value="OVERDUE" label="已逾期" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="项目描述" prop="description">
          <el-input 
            v-model="dialog.form.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入项目描述"
          />
        </el-form-item>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="项目状态" prop="status">
              <el-select v-model="dialog.form.status" placeholder="请选择项目状态">
                <el-option value="PENDING" label="待开始" />
                <el-option value="ONGOING" label="进行中" />
                <el-option value="COMPLETED" label="已完成" />
                <el-option value="OVERDUE" label="已逾期" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目经理" prop="managerId">
              <EmployeeSelector
                v-model="selectedProjectManager"
                placeholder="请选择项目经理"
                @change="handleProjectManagerChange"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker 
                v-model="dialog.form.startDate" 
                type="date" 
                placeholder="选择开始日期"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker 
                v-model="dialog.form.endDate" 
                type="date" 
                placeholder="选择结束日期"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="dialog.form.priority" placeholder="请选择优先级">
                <el-option value="LOW" label="低" />
                <el-option value="MEDIUM" label="中" />
                <el-option value="HIGH" label="高" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目级别" prop="level">
              <el-select v-model="dialog.form.level" placeholder="请选择项目级别">
                <el-option value="NORMAL" label="普通" />
                <el-option value="IMPORTANT" label="重要" />
                <el-option value="URGENT" label="紧急" />
                <el-option value="LONG_TERM" label="长期" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="项目进度" prop="progress">
              <el-input-number 
                v-model="dialog.form.progress" 
                :min="0" 
                :max="100" 
                placeholder="项目进度"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否置顶" prop="topUp">
              <el-select v-model="dialog.form.topUp" placeholder="请选择是否置顶">
                <el-option :value="0" label="否" />
                <el-option :value="1" label="是" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>            
      </el-form>
      
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>
    
    <!-- 项目详情对话框 -->
    <el-dialog 
      v-model="detailDialog.visible" 
      :title="detailDialog.isEdit ? '编辑项目' : '项目详情'"
      top="5vh"
      width="1000px"
      class="project-detail-dialog"
      :close-on-click-modal="false"
    >
      <div v-if="detailDialog.data && detailDialog.data.projectInfo" class="project-detail">
        <!-- 项目基本信息卡片 -->
        <el-card class="project-info-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">项目信息</span>
              <div class="card-actions">
                <el-button 
                  v-if="!detailDialog.isEdit" 
                  size="small" 
                  type="primary" 
                  @click="handleDetailEdit"
                >
                  编辑
                </el-button>
                <template v-else>
                  <el-button size="small" @click="handleDetailCancel">取消</el-button>
                  <el-button size="small" type="primary" @click="handleDetailSave" :loading="submitLoading">保存</el-button>
                </template>
              </div>
            </div>
          </template>
          <el-row :gutter="24">
            <el-col :span="12">
              <div class="detail-item">
                <label>项目名称：</label>
                <span v-if="!detailDialog.isEdit" class="project-name">{{ detailDialog.data.projectInfo.name }}</span>
                <el-input v-else v-model="detailDialog.editForm.name" placeholder="请输入项目名称" />
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <label>项目状态：</label>
                <el-tag v-if="!detailDialog.isEdit" :type="getStatusType(detailDialog.data.projectInfo.status)" size="large">
                  {{ getStatusText(detailDialog.data.projectInfo.status) }}
                </el-tag>
                <el-select v-else v-model="detailDialog.editForm.status" placeholder="请选择状态">
                  <el-option value="PENDING" label="待开始" />
                  <el-option value="ONGOING" label="进行中" />
                  <el-option value="COMPLETED" label="已完成" />
                  <el-option value="OVERDUE" label="已逾期" />
                </el-select>
              </div>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="12">
              <div class="detail-item">
                <label>项目经理：</label>
                <span v-if="!detailDialog.isEdit">{{ detailDialog.data.projectInfo.managerName || '未指定' }}</span>
                <EmployeeSelector 
                  v-else 
                  v-model="detailDialog.editForm.manager" 
                  placeholder="请选择项目经理"
                  @change="handleManagerChange"
                />
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <label>项目级别：</label>
                <el-tag v-if="!detailDialog.isEdit" :type="getLevelType(detailDialog.data.projectInfo.level)" size="large">
                  {{ getLevelText(detailDialog.data.projectInfo.level) }}
                </el-tag>
                <el-select v-else v-model="detailDialog.editForm.level" placeholder="请选择项目级别">
                  <el-option value="SHORT_TERM" label="短期" />
                  <el-option value="MEDIUM_TERM" label="中期" />
                  <el-option value="LONG_TERM" label="长期" />
                </el-select>
              </div>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="12">
              <div class="detail-item">
                <label>项目进度：</label>
                <div class="progress-container">
                  <el-progress 
                    :percentage="detailDialog.data.projectInfo.progress || 0" 
                    :stroke-width="12"
                    :show-text="true"
                    :format="(percentage) => `${percentage}%`"
                  />
                </div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <label>优先级：</label>
                <el-tag v-if="!detailDialog.isEdit" :type="getPriorityType(detailDialog.data.projectInfo.priority)" size="large">
                  {{ getPriorityText(detailDialog.data.projectInfo.priority) }}
                </el-tag>
                <el-select v-else v-model="detailDialog.editForm.priority" placeholder="请选择优先级">
                  <el-option value="LOW" label="低" />
                  <el-option value="MEDIUM" label="中" />
                  <el-option value="HIGH" label="高" />
                  <el-option value="URGENT" label="紧急" />
                </el-select>
              </div>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="12">
              <div class="detail-item">
                <label>开始日期：</label>
                <span v-if="!detailDialog.isEdit">{{ formatDate(detailDialog.data.projectInfo.startDate) }}</span>
                <el-date-picker v-else v-model="detailDialog.editForm.startDate" type="date" placeholder="选择开始日期" />
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <label>结束日期：</label>
                <span v-if="!detailDialog.isEdit">{{ formatDate(detailDialog.data.projectInfo.endDate) }}</span>
                <el-date-picker v-else v-model="detailDialog.editForm.endDate" type="date" placeholder="选择结束日期" />
              </div>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="12">
              <div class="detail-item">
                <label>创建时间：</label>
                <span>{{ formatDateTime(detailDialog.data.projectInfo.createTime) }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <!-- 空列保持布局平衡 -->
            </el-col>
          </el-row>
        </el-card>
        
        <!-- 项目描述卡片 -->
        <el-card class="project-description-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">项目描述</span>
            </div>
          </template>
          <div v-if="!detailDialog.isEdit" class="description-content">
            {{ detailDialog.data.projectInfo.description || '暂无描述' }}
          </div>
          <el-input 
            v-else 
            v-model="detailDialog.editForm.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入项目描述"
            class="description-input"
          />
        </el-card>
        
        <!-- 项目成员卡片 -->
        <el-card class="project-members-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">项目成员</span>
              <div class="card-actions">
                <el-tag type="info" size="small">{{ (detailDialog.data.projectMembers || []).length }} 人</el-tag>
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="handleInviteMember"
                  class="invite-button-header"
                >
                  <el-icon><Plus /></el-icon>
                  邀请员工
                </el-button>
              </div>
            </div>
          </template>
          <div class="members-list">
            <div 
              v-for="member in detailDialog.data.projectMembers || []" 
              :key="member.id" 
              class="member-item"
            >
              <div class="member-avatar">
                <el-avatar
                  :size="30"
                  :src="member.avatar || '/src/static/images/avatar/default-avatar.png'"
                  :alt="member.employeeName"
                >
                  <img src="/src/static/images/avatar/default-avatar.png" alt="默认头像" />
                </el-avatar>
              </div>
              <div class="member-name">{{ member.employeeName }}</div>
              <div class="member-role">
                <el-tag :type="member.role === 'MANAGER' ? 'success' : 'info'" size="small">
                  {{ member.role === 'MANAGER' ? '项目经理' : '成员' }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </el-dialog>
    
    <!-- 邀请员工选择器 -->
    <EmployeeSelector
      v-model="inviteDialog.selectedEmployees"
      placeholder="邀请员工"
      multiple 
      :existing-members="employeeObject"
      @change="handleInviteEmployeesChange"
      ref="inviteEmployeeSelector"
    />
    
    <!-- 导入项目对话框 -->
    <ImportDialog
      v-model="importDialog.visible"
      title="导入项目信息"
      import-type="项目导入"
      @success="handleImportSuccess"
    />
    
    <!-- 项目进度管理对话框 -->
    <el-dialog
      v-model="progressDialog.visible"
      :title="`项目进度管理 - ${progressDialog.projectName}`"
      width="90%"
      :close-on-click-modal="false"
      class="project-progress-dialog"
    >
      <div v-if="progressDialog.projectId" class="progress-container">
        <ProjectProgress :project-id="progressDialog.projectId" />
      </div>
    </el-dialog>
    
    <!-- 项目任务管理对话框 -->
    <el-dialog
      v-model="taskDialog.visible"
      :title="`项目任务管理 - ${taskDialog.projectName}`"
      width="90%"
      :close-on-click-modal="false"
      class="project-task-dialog"
    >
      <div v-if="taskDialog.projectId" class="task-container">
        <ProjectTask :project-id="taskDialog.projectId" />
      </div>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { projectApi } from '@/api/project'
import EmployeeSelector from '@/components/EmployeeSelector.vue'
import ImportDialog from '@/components/ImportDialog.vue'
import ProjectProgress from './Progress.vue'
import ProjectTask from './Task.vue'
import ExcelTemplateUtil from '@/utils/excelTemplate'

// 响应式数据
const list = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const formRef = ref()
const router = useRouter()

// 选中的项目经理
const selectedProjectManager = ref(null)

//员工对象
const employeeObject = ref([])


const page = reactive({
  current: 1,
  size: 10,
  total: 0
})

const search = reactive({
  keyword: '',
  status: ''
})

const dialog = reactive({
  visible: false,
  form: {}
})

const detailDialog = reactive({
  visible: false,
  data: null,
  isEdit: false,
  editForm: {
    id: null,
    name: '',
    description: '',
    status: '',
    managerId: null,
    manager: null,
    level: '',
    priority: '',
    startDate: null,
    endDate: null
  }
})

const importDialog = reactive({
  visible: false
})

const inviteDialog = reactive({
  selectedEmployees: []
})

// 项目进度管理对话框
const progressDialog = reactive({
  visible: false,
  projectId: null,
  projectName: ''
})

// 项目任务管理对话框
const taskDialog = reactive({
  visible: false,
  projectId: null,
  projectName: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入项目名称', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择项目状态', trigger: 'change' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' }
  ],
  managerId: [
    { required: true, message: '请选择项目经理', trigger: 'change' }
  ]
}

// 生命周期
onMounted(() => {
  fetchList()
})

// 获取项目列表
async function fetchList() {
  try {
    loading.value = true
    const params = {
      current: page.current,
      size: page.size,
      keyword: search.keyword,
      status: search.status
    }
    
    const response = await projectApi.getPage(params)
    if (response.code === 200) {
      list.value = response.data.list || []
      page.total = response.data.total || 0
      page.current = response.data.pageNum || 1
      page.size = response.data.pageSize || 10
    } else {
      ElMessage.error(response.message || '获取项目列表失败')
    }
  } catch (error) {
    console.error('获取项目列表失败:', error)
    ElMessage.error('获取项目列表失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索
function resetSearch() {
  search.keyword = ''
  search.status = ''
  page.current = 1
  fetchList()
}

// 新增项目
function handleAdd() {
  resetForm()
  dialog.form = {
    status: 'PENDING',
    priority: 'MEDIUM',
    level: 'NORMAL',
    progress: 0,
    topUp: 0
  }
  selectedProjectManager.value = null
  dialog.visible = true
}

// 查看项目详情
async function handleView(row) {
  try {
    const response = await projectApi.getDetail(row.id)
    if (response.code === 200) {
      detailDialog.data = response.data
      console.log(detailDialog.data)
      // 清空现有员工列表，避免重复添加
      employeeObject.value = []
      response.data.projectMembers.forEach(member => {
        const employee = {
          id: member.employeeId,
          name: member.employeeName,
          avatar: member.avatar
        }
        employeeObject.value.push(employee)
      })
      detailDialog.isEdit = false
      detailDialog.visible = true
    } else {
      ElMessage.error(response.message || '获取项目详情失败')
    }
  } catch (error) {
    console.error('获取项目详情失败:', error)
    ElMessage.error('获取项目详情失败')
  }
}

// 详情页面编辑
function handleDetailEdit() {
  if (detailDialog.data && detailDialog.data.projectInfo) {
    const projectInfo = detailDialog.data.projectInfo
    detailDialog.editForm = {
      id: projectInfo.id,
      name: projectInfo.name || '',
      description: projectInfo.description || '',
      status: projectInfo.status || '',
      managerId: projectInfo.managerId || null,
      manager: projectInfo.managerId ? { id: projectInfo.managerId, name: projectInfo.managerName } : null,
      level: projectInfo.level || '',
      priority: projectInfo.priority || '',
      startDate: projectInfo.startDate || null,
      endDate: projectInfo.endDate || null
    }
    detailDialog.isEdit = true
  }
}

// 详情页面取消编辑
function handleDetailCancel() {
  detailDialog.isEdit = false
}

// 详情页面保存编辑
async function handleDetailSave() {
  try {
    submitLoading.value = true
    
    const response = await projectApi.update(detailDialog.editForm)
    if (response.code === 200) {
      ElMessage.success('项目更新成功')
      detailDialog.isEdit = false
      // 重新获取项目详情
      await handleView({ id: detailDialog.editForm.id })
      // 刷新列表
      fetchList()
    } else {
      ElMessage.error(response.message || '项目更新失败')
    }
  } catch (error) {
    console.error('项目更新失败:', error)
    ElMessage.error('项目更新失败')
  } finally {
    submitLoading.value = false
  }
}

// 项目经理变化处理
function handleManagerChange(employee) {
  if (employee) {
    detailDialog.editForm.managerId = employee.id
  } else {
    detailDialog.editForm.managerId = null
  }
}

// 邀请员工
function handleInviteMember() {
  // 直接触发员工选择组件的点击事件
  const selector = document.querySelector('.employee-selector input')
  if (selector) {
    selector.click()
  }
}

// 邀请员工变化处理
async function handleInviteEmployeesChange(employees) {
  try {
    // 使用项目update接口更新成员
    const response = await projectApi.update({
      id: detailDialog.data.projectInfo.id,
      invitedMembers: employees ? employees.map(emp => emp.id) : []
    })
    if (response.code === 200) {
      const memberCount = employees ? employees.length : 0
      ElMessage.success(`成功更新项目成员，共 ${memberCount} 人`)
      // 重新获取项目详情以刷新弹窗数据
      await handleView({ id: detailDialog.data.projectInfo.id })
    } else {
      ElMessage.error(response.message || '更新项目成员失败')
    }
  } catch (error) {
    console.error('更新项目成员失败:', error)
    ElMessage.error('更新项目成员失败')
  }
}

// 编辑项目
async function handleEdit(row) {
  try {
    const response = await projectApi.getDetail(row.id)
    if (response.code === 200) {
      dialog.form = { ...response.data }
      
      // 设置项目经理选择器
      if (response.data.projectManagerId) {
        selectedProjectManager.value = {
          id: response.data.projectManagerId,
          name: response.data.projectManager
        }
      }
      
      dialog.visible = true
    } else {
      ElMessage.error(response.message || '获取项目信息失败')
    }
  } catch (error) {
    console.error('获取项目信息失败:', error)
    ElMessage.error('获取项目信息失败')
  }
}

// 删除项目
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除项目 "${row.projectName}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await projectApi.delete(row.id)
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

// 打开项目进度管理对话框
function handleProgress(row) {
  progressDialog.projectId = row.id
  progressDialog.projectName = row.name
  progressDialog.visible = true
}

// 打开项目任务管理对话框
function handleTask(row) {
  taskDialog.projectId = row.id
  taskDialog.projectName = row.name
  taskDialog.visible = true
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
      response = await projectApi.update(dialog.form)
    } else {
      // 新增
      response = await projectApi.create(dialog.form)
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
  selectedProjectManager.value = null
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

// 处理项目经理选择变化
function handleProjectManagerChange(employee) {
  if (employee) {
    dialog.form.managerId = employee.id
    dialog.form.managerName = employee.name
  } else {
    dialog.form.managerId = null
    dialog.form.managerName = ''
  }
}

// 获取状态类型
function getStatusType(status) {
  const statusMap = {
    'PENDING': 'info',
    'ONGOING': 'success',
    'COMPLETED': 'success',
    'OVERDUE': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
function getStatusText(status) {
  const statusMap = {
    'PENDING': '待开始',
    'ONGOING': '进行中',
    'COMPLETED': '已完成',
    'OVERDUE': '已逾期'
  }
  return statusMap[status] || status
}

// 获取优先级类型
function getPriorityType(priority) {
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

// 获取项目级别类型
function getLevelType(level) {
  const levelMap = {
    'URGENT': 'danger',
    'IMPORTANT': 'warning',
    'NORMAL': 'info',
    'LONG_TERM': 'success'
  }
  return levelMap[level] || 'info'
}

// 获取项目级别文本
function getLevelText(level) {
  const levelMap = {
    'URGENT': '紧急',
    'IMPORTANT': '重要',
    'NORMAL': '普通',
    'LONG_TERM': '长期'
  }
  return levelMap[level] || level
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

// 导入功能
function handleImport() {
  importDialog.visible = true
}

// 下载模板
async function handleDownloadTemplate() {
  try {
    const result = await ExcelTemplateUtil.downloadTemplate('项目导入')
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

// 导入成功回调
function handleImportSuccess(result) {
  ElMessage.success('项目数据导入成功！')
  fetchList()
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

/* 项目对话框样式 */
.project-dialog .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px 12px 0 0;
  padding: 20px 24px;
}

.project-dialog .el-dialog__title {
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.project-dialog .el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 18px;
}

.project-dialog .el-dialog__body {
  padding: 24px;
  background: #fafbfc;
}

.project-form .el-form-item {
  margin-bottom: 20px;
}

.project-form .el-input,
.project-form .el-select,
.project-form .el-date-editor,
.project-form .el-input-number {
  width: 100% !important;
}

/* 项目详情对话框样式 */
.project-detail-dialog {
  --el-dialog-margin-top: 5vh;
  --el-card-padding: 10px;
}

.project-detail-dialog .el-dialog__body {
  padding: 0;
  background: #f5f7fa;
}

.project-detail {
  max-height: 70vh;
  overflow-y: auto;
  padding: 16px 20px;
}

/* 卡片样式 */
.project-info-card,
.project-description-card,
.project-members-card {
  margin-bottom: 16px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.project-info-card .el-card__body {
  padding: 16px;
}

.project-description-card .el-card__body,
.project-members-card .el-card__body {
  padding: 16px;
}

/* 卡片头部样式 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 详情项样式 */
.detail-item {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  padding: 1px 0;
}

/* 项目信息卡片内的详情项更紧凑 */
.project-info-card .detail-item {
  margin-bottom: 8px;
}

.detail-item label {
  font-weight: 600;
  color: #606266;
  min-width: 100px;
  margin-right: 16px;
  font-size: 14px;
}

.detail-item span {
  color: #303133;
  font-size: 14px;
  flex: 1;
}

.project-name {
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
}

/* 进度条容器 */
.progress-container {
  flex: 1;
  padding-right: 20px;
}

/* 项目描述样式 */
.description-content {
  background: #f8f9fa;
  padding: 12px;
  border-radius: 6px;
  border-left: 4px solid #409eff;
  line-height: 1.5;
  color: #606266;
  font-size: 14px;
  min-height: 40px;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 成员列表样式 */
.members-list {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 8px;
}

.member-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0;
  background: none;
  border-radius: 0;
  border: none;
  transition: none;
  min-width: 70px;
  text-align: center;
}

.member-item:hover {
  background: none;
  border-color: transparent;
}

.member-avatar {
  margin-bottom: 4px;
}

.member-name {
  font-size: 12px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 3px;
  text-align: center;
  word-break: break-all;
}

.member-role {
  text-align: center;
}


.invite-button-header {
  margin-left: 8px;
}

/* 响应式优化 */
@media (max-width: 1200px) {
  .project-detail-dialog .el-dialog {
    width: 90% !important;
    margin: 0 auto;
  }
}


</style>
