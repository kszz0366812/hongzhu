<template>
  <el-card>
    <div class="action-bar">
      <div class="search-bar">
        <el-input v-model="search.keyword" placeholder="员工姓名或工号" style="width: 200px; margin-right: 8px;" />
        <el-select v-model="search.status" placeholder="状态" clearable style="width: 120px; margin-right: 8px;">
          <el-option :value="0" label="在职" />
          <el-option :value="1" label="离职" />
        </el-select>
        <el-button type="primary" @click="searchEmployeeList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      <div class="button-bar">
        <el-button type="primary" @click="handleAdd">新增员工</el-button>
        <el-button @click="handleImport">导入</el-button>
        <el-button @click="handleDownloadTemplate">下载模板</el-button>
      </div>
    </div>
    
    <el-table :data="list" border stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="employeeCode" label="工号" width="120" align="center" />
      <el-table-column prop="name" label="姓名" width="120" align="center" />
      <el-table-column prop="directLeader" label="直属领导" width="120" align="center" />
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status === 0 ? 'success' : 'info'">
            {{ scope.row.status === 0 ? '在职' : '离职' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="regionLevel1" label="大区" align="center" />
      <el-table-column prop="regionLevel2" label="地市" align="center" />
      <el-table-column prop="regionLevel3" label="区域" width="200" align="center" />
      <el-table-column prop="position" label="岗位" align="center" />
      <el-table-column prop="levels" label="职级" align="center" />
      <el-table-column prop="channel" label="渠道" align="center" />
      <el-table-column prop="joinDate" label="入司日期" width="120" align="center">
        <template #default="scope">
          {{ formatDate(scope.row.joinDate) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right" align="center">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination
      v-model:current-page="page.pageNum"
      v-model:page-size="page.pageSize"
      :total="page.total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
      style="margin-top: 16px; text-align: right;"
    />
    
    <!-- 新增/编辑员工对话框 -->
    <el-dialog 
      v-model="dialog.visible" 
      :title="dialog.form.id ? '编辑员工' : '新增员工'"
      width="800px"
      @close="resetForm"
      class="employee-dialog"
    >
      <el-form :model="dialog.form" :rules="rules" ref="formRef" label-width="100px" class="employee-form">
        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="工号" prop="employeeCode">
              <el-input v-model="dialog.form.employeeCode" placeholder="请输入工号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="dialog.form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-select v-model="dialog.form.status" placeholder="请选择状态">
                <el-option :value="0" label="在职" />
                <el-option :value="1" label="离职" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="入司日期" prop="joinDate">
              <el-date-picker 
                v-model="dialog.form.joinDate" 
                type="date" 
                placeholder="选择入司日期"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="大区" prop="regionLevel1">
              <el-input v-model="dialog.form.regionLevel1" placeholder="请输入大区" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="地市" prop="regionLevel2">
              <el-input v-model="dialog.form.regionLevel2" placeholder="请输入地市" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="区域" prop="regionLevel3">
              <el-input v-model="dialog.form.regionLevel3" placeholder="请输入区域" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="负责区域" prop="responsibleRegions">
              <el-input v-model="dialog.form.responsibleRegions" placeholder="请输入负责区域" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="岗位" prop="position">
              <el-input v-model="dialog.form.position" placeholder="请输入岗位" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="直属领导" prop="directLeaderId">
              <EmployeeSelector
                v-model="selectedDirectLeader"
                placeholder="请选择直属领导"
                @change="handleDirectLeaderChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="职级" prop="levels">
              <el-input v-model="dialog.form.levels" placeholder="请输入职级" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="渠道" prop="channel">
              <el-input v-model="dialog.form.channel" placeholder="请输入渠道" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>
    
    <!-- 导入员工对话框 -->
    <ImportDialog
      v-model="importDialog.visible"
      title="导入员工信息"
      import-type="员工导入"
      @success="handleImportSuccess"
    />
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getEmployeeList, 
  getEmployeePage,
  getEmployeeById, 
  addEmployee, 
  updateEmployee, 
  deleteEmployee,
  searchEmployees
} from '@/api/system.js'
import EmployeeSelector from '@/components/EmployeeSelector.vue'
import ImportDialog from '@/components/ImportDialog.vue'
import ExcelTemplateUtil from '@/utils/excelTemplate'

// 响应式数据
const list = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const formRef = ref()

// 选中的直属领导
const selectedDirectLeader = ref(null)

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const search = reactive({
  keyword: '',
  status: undefined // 新增状态搜索参数
})

const dialog = reactive({
  visible: false,
  form: {}
})

const importDialog = reactive({
  visible: false
})

// 表单验证规则
const rules = {
  employeeCode: [
    { required: true, message: '请输入工号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  directLeaderId: [
    { required: false, message: '请选择直属领导', trigger: 'change' }
  ]
}

// 生命周期
onMounted(() => {
  fetchList()
})

// 获取员工列表
async function fetchList() {
  try {
    loading.value = true
    const params = {
      page: page.pageNum,
      size: page.pageSize
    }
    if (search.keyword) {
      params.keyword = search.keyword
    }
    if (search.status !== undefined && search.status !== '') {
      params.status = search.status
    }
    const response = await getEmployeePage(params)
    if (response.code === 200) {
      // 适配新的PageInfo返回结构
      list.value = response.data.list || []
      page.total = response.data.total || 0
      page.pageNum = response.data.pageNum || 1
      page.pageSize = response.data.pageSize || 10
      
      // 为每个员工加载直属领导姓名
      await loadDirectLeaderNames()
    } else {
      ElMessage.error(response.message || '获取员工列表失败')
    }
  } catch (error) {
    console.error('获取员工列表失败:', error)
    ElMessage.error('获取员工列表失败')
  } finally {
    loading.value = false
  }
}

// 加载直属领导姓名
async function loadDirectLeaderNames() {
  const leaderIds = list.value
    .map(employee => employee.directLeaderId)
    .filter(id => id && id !== 0)
    .filter((id, index, arr) => arr.indexOf(id) === index) // 去重
  
  if (leaderIds.length > 0) {
    try {
      // 批量查询直属领导信息
      for (const employee of list.value) {
        if (employee.directLeaderId && employee.directLeaderId !== 0) {
          try {
            const leaderResponse = await getEmployeeById(employee.directLeaderId)
            if (leaderResponse.code === 200) {
              employee.directLeaderName = leaderResponse.data.name
            }
          } catch (error) {
            console.warn(`加载员工 ${employee.name} 的直属领导信息失败:`, error)
          }
        }
      }
    } catch (error) {
      console.warn('批量加载直属领导信息失败:', error)
    }
  }
}

// 搜索员工
function searchEmployeeList() {
  page.pageNum = 1
  fetchList()
}

// 重置搜索
function resetSearch() {
  search.keyword = ''
  search.status = undefined // 重置状态搜索参数
  page.pageNum = 1
  fetchList()
}

// 新增员工
function handleAdd() {
  resetForm()
  dialog.form = {
    status: 0
  }
  selectedDirectLeader.value = null
  dialog.visible = true
}

// 编辑员工
async function handleEdit(row) {
  try {
    const response = await getEmployeeById(row.id)
    if (response.code === 200) {
      dialog.form = { ...response.data }
      
      // 如果有直属领导，需要设置对应的员工信息
      if (response.data.directLeaderId && response.data.directLeader) {
        selectedDirectLeader.value = {
          id: response.data.directLeaderId,
          name: response.data.directLeader
        }
      }
      
      // 预加载一些在职员工作为直属领导选项
      // await loadInitialLeaderOptions() // 移除这个调用
    } else {
      ElMessage.error(response.message || '获取员工信息失败')
    }
  } catch (error) {
    console.error('获取员工信息失败:', error)
    ElMessage.error('获取员工信息失败')
  }
}

// 删除员工
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除员工 "${row.name}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await deleteEmployee(row.id)
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
      response = await updateEmployee(dialog.form)
    } else {
      // 新增
      response = await addEmployee(dialog.form)
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
  // 清空直属领导选项
}

// 分页处理
function handleCurrentChange(currentPage) {
  page.pageNum = currentPage
  fetchList()
}

function handleSizeChange(size) {
  page.pageSize = size
  page.pageNum = 1
  fetchList()
}

// 格式化日期
function formatDate(date) {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

// 导入功能
function handleImport() {
  importDialog.visible = true
}

// 下载模板
async function handleDownloadTemplate() {
  try {
    const result = await ExcelTemplateUtil.downloadEmployeeTemplate()
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
  ElMessage.success('员工数据导入成功！')
  fetchList()
}

// 预加载一些在职员工作为直属领导选项
// async function loadInitialLeaderOptions() {
//   try {
//     const response = await getEmployeeList({
//       status: 0
//     })
//     if (response.code === 200) {
//       const inServiceEmployees = response.data.filter(emp => emp.status === 0)
//       // 将新选项添加到现有选项中，避免重复
//       // leaderOptions.value = [...leaderOptions.value, ...inServiceEmployees] // This line is removed
//     }
//   } catch (error) {
//     console.warn('预加载直属领导选项失败:', error)
//   }
// }

// 处理EmployeeSelector的change事件
function handleDirectLeaderChange(employee) {
  if (employee) {
    dialog.form.directLeaderId = employee.id
    dialog.form.directLeader = employee.name
  } else {
    dialog.form.directLeaderId = null
    dialog.form.directLeader = ''
  }
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

.el-dialog .el-form {
  max-height: 60vh;
  overflow-y: auto;
}

/* 导入对话框样式 */
.import-content {
  padding: 20px 0;
}

/* 员工对话框美化样式 */
.employee-dialog {
  border-radius: 12px;
}

.employee-dialog .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px 12px 0 0;
  padding: 20px 24px;
}

.employee-dialog .el-dialog__title {
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.employee-dialog .el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 18px;
}

.employee-dialog .el-dialog__body {
  padding: 24px;
  background: #fafbfc;
  overflow: hidden;
  max-width: 100%;
}

/* 表单样式 - 确保宽度不超出 */
.employee-form {
  width: 100%;
  max-width: 100%;
  overflow: hidden;
}

.employee-form .el-row {
  margin: 0 !important;
  width: 100%;
}

.employee-form .el-col {
  padding: 0 12px;
}

.employee-form .el-form-item {
  margin-bottom: 20px;
  width: 100%;
}

.employee-form .el-form-item__label {
  font-weight: 500;
  color: #606266;
}

.employee-form .el-input,
.employee-form .el-select,
.employee-form .el-date-editor {
  width: 100% !important;
}

.employee-form .el-input__wrapper,
.employee-form .el-select .el-input__wrapper,
.employee-form .el-date-editor {
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s ease;
  width: 100% !important;
  max-width: 100% !important;
}

.employee-form .el-input__wrapper:hover,
.employee-form .el-select .el-input__wrapper:hover,
.employee-form .el-date-editor:hover {
  border-color: #c0c4cc;
}

.employee-form .el-input__wrapper:focus-within,
.employee-form .el-select .el-input__wrapper:focus-within,
.employee-form .el-date-editor:focus-within {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.employee-dialog .el-button {
  border-radius: 8px;
  font-weight: 500;
  padding: 12px 24px;
  transition: all 0.3s ease;
}

.employee-dialog .el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.employee-dialog .el-button--primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* 字段说明样式 */
.field-description {
  margin-bottom: 24px;
  padding: 0 20px;
}

.field-description .el-alert {
  border-radius: 6px;
  border: none;
  background: #f5f7fa;
  box-shadow: none;
}

.field-description .el-alert__icon {
  margin-top: 2px;
  align-self: flex-start;
}

.description-lines {
  line-height: 1.6;
  text-align: left;
}

.description-line {
  margin-bottom: 6px;
  font-size: 13px;
  color: #606266;
}

.description-line:last-child {
  margin-bottom: 0;
}



.upload-area {
  padding: 0 20px;
}

.upload-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  text-align: center;
}

/* 大尺寸上传控件 */
.large-upload .el-upload-dragger {
  width: 100%;
  height: 400px;
  border: 3px dashed #d9d9d9;
  border-radius: 12px;
  background: #fafafa;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 100px 50px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
  margin: 0 auto;
  text-align: center;
}

.large-upload .el-upload-dragger:hover {
  border-color: #409eff;
  background: #f0f9ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.large-upload .el-icon--upload {
  font-size: 72px;
  color: #c0c4cc;
  margin-bottom: 28px;
  transition: all 0.3s ease;
}

.large-upload .el-upload-dragger:hover .el-icon--upload {
  color: #409eff;
  transform: scale(1.05);
}

.large-upload .el-upload__text {
  color: #606266;
  font-size: 16px;
  line-height: 1.6;
  text-align: center;
  width: 100%;
}

.large-upload .el-upload__text em {
  color: #409eff;
  font-style: normal;
  font-weight: 600;
  text-decoration: underline;
}

/* 上传进度条样式 */
.upload-progress {
  margin-top: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.progress-text {
  margin: 10px 0 0 0;
  text-align: center;
  color: #606266;
  font-size: 14px;
}

/* 对话框底部按钮 */
.dialog-footer {
  text-align: right;
}

.dialog-footer .el-button {
  min-width: 100px;
}
</style> 