<template>
  <el-card>
    <div class="action-bar">
      <div class="search-bar">
        <el-input v-model="search.keyword" placeholder="搜索执行人、任务名称" style="width: 250px; margin-right: 8px;" />
        <el-button type="primary" @click="handleQuery">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      <div class="button-bar">
        <el-button type="primary" @click="handleAdd">新增</el-button>
        <el-button @click="handleImport">导入</el-button>
        <el-button @click="handleDownloadTemplate">下载模板</el-button>
      </div>
    </div>
    <el-table :data="list" border stripe style="width: 100%">
      <el-table-column prop="executor" label="执行人" width="120" align="center" />
      <el-table-column prop="startTime" label="开始时间" width="160" align="center">
        <template #default="scope">
          {{ formatDateTime(scope.row.startTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="endTime" label="结束时间" width="160" align="center">
        <template #default="scope">
          {{ formatDateTime(scope.row.endTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="taskName" label="任务名称" align="center" />
      <el-table-column prop="targetAmount" label="目标金额" width="120" align="center">
        <template #default="scope">
          ¥{{ formatAmount(scope.row.targetAmount) }}
        </template>
      </el-table-column>
      <el-table-column prop="achievedAmount" label="已达成金额" width="120" align="center">
        <template #default="scope">
          ¥{{ formatAmount(scope.row.achievedAmount) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center">
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
      layout="total, prev, pager, next, sizes"
      @current-change="fetchList"
      @size-change="handleSizeChange"
      style="margin-top: 16px; text-align: right;"
    />
    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑任务' : '新增任务'" width="600px">
      <el-form :model="dialog.form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="执行人" prop="executorId">
          <EmployeeSelector 
            v-model="selectedExecutor"
            placeholder="请选择执行人"
            @change="handleEmployeeChange"
          />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker 
            v-model="dialog.form.startTime" 
            type="datetime" 
            placeholder="选择开始时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker 
            v-model="dialog.form.endTime" 
            type="datetime" 
            placeholder="选择结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="dialog.form.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="目标金额" prop="targetAmount">
          <el-input v-model="dialog.form.targetAmount" placeholder="请输入目标金额" type="number" />
        </el-form-item>
        <el-form-item label="已达成金额" prop="achievedAmount">
          <el-input v-model="dialog.form.achievedAmount" placeholder="请输入已达成金额" type="number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
    
    <!-- 导入对话框 -->
    <ImportDialog
      v-model="importDialog.visible"
      title="导入任务目标"
      import-type="任务目标导入"
      @success="handleImportSuccess"
    />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getTaskTargetPage, 
  getTaskTargetById, 
  saveTaskTarget, 
  updateTaskTarget, 
  deleteTaskTarget 
} from '@/api/system'
import EmployeeSelector from '@/components/EmployeeSelector.vue'
import ImportDialog from '@/components/ImportDialog.vue'
import ExcelTemplateUtil from '@/utils/excelTemplate'

const list = ref([])
const page = ref({ pageNum: 1, pageSize: 10, total: 0 })
const search = ref({ keyword: '' })
const dialog = ref({ visible: false, form: {} })
const formRef = ref()
const employeeSelectorRef = ref()

// 选中的执行人
const selectedExecutor = ref(null)

// 导入对话框
const importDialog = ref({
  visible: false
})

// 表单验证规则
const rules = {
  executorId: [
    { required: true, message: '请选择执行人', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  taskName: [
    { required: true, message: '请输入任务名称', trigger: 'blur' }
  ],
  targetAmount: [
    { required: true, message: '请输入目标金额', trigger: 'blur' }
  ]
}

// 获取任务列表
async function fetchList() {
  try {
    const params = {
      page: page.value.pageNum,
      size: page.value.pageSize,
      keyword: search.value.keyword || null
    }
    const response = await getTaskTargetPage(params)
    if (response.code === 200) {
      list.value = response.data.list || []
      page.value.total = response.data.total || 0
      page.value.pageNum = response.data.pageNum || 1
      page.value.pageSize = response.data.pageSize || 10
    } else {
      ElMessage.error(response.message || '获取任务列表失败')
    }
  } catch (error) {
    console.error('获取任务列表失败:', error)
    ElMessage.error('获取任务列表失败')
  }
}

// 重置搜索
function resetSearch() {
  search.value = { keyword: '' }
  page.value.pageNum = 1
  fetchList()
}

// 处理分页变化
function handleSizeChange() {
  page.value.pageNum = 1
  fetchList()
}

// 处理查询
function handleQuery() {
  page.value.pageNum = 1
  fetchList()
}

// 处理员工选择变化
function handleEmployeeChange(employee) {
  if (employee) {
    dialog.value.form.executorId = employee.id
    dialog.value.form.executor = employee.name
  } else {
    dialog.value.form.executorId = null
    dialog.value.form.executor = ''
  }
}

// 新增任务
function handleAdd() {
  dialog.value.form = {
    executorId: '',
    executor: '',
    startTime: '',
    endTime: '',
    taskName: '',
    targetAmount: '',
    achievedAmount: '0'
  }
  selectedExecutor.value = null
  dialog.value.visible = true
}

// 编辑任务
async function handleEdit(row) {
  try {
    const response = await getTaskTargetById(row.id)
    if (response.code === 200) {
      dialog.value.form = { ...response.data }
      // 如果有执行人，需要设置对应的员工信息
      if (response.data.executorId && response.data.executor) {
        selectedExecutor.value = {
          id: response.data.executorId,
          name: response.data.executor
        }
      }
      dialog.value.visible = true
    } else {
      ElMessage.error(response.message || '获取任务详情失败')
    }
  } catch (error) {
    console.error('获取任务详情失败:', error)
    ElMessage.error('获取任务详情失败')
  }
}

// 删除任务
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除这个任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await deleteTaskTarget(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除任务失败:', error)
      ElMessage.error('删除任务失败')
    }
  }
}

// 提交表单
async function handleSubmit() {
  try {
    await formRef.value.validate()
    
    const isEdit = !!dialog.value.form.id
    const api = isEdit ? updateTaskTarget : saveTaskTarget
    
    const response = await api(dialog.value.form)
    if (response.code === 200) {
      ElMessage.success(isEdit ? '更新成功' : '新增成功')
      dialog.value.visible = false
      fetchList()
    } else {
      ElMessage.error(response.message || (isEdit ? '更新失败' : '新增失败'))
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  }
}

// 导入功能
function handleImport() {
  importDialog.value.visible = true
}

// 下载模板
async function handleDownloadTemplate() {
  try {
    const result = await ExcelTemplateUtil.downloadTaskTargetTemplate()
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
  fetchList() // 刷新列表
}

// 格式化日期时间
function formatDateTime(dateTime) {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化金额
function formatAmount(amount) {
  if (!amount) return '0.00'
  return parseFloat(amount).toFixed(2)
}

// 页面加载时获取数据
onMounted(() => {
  fetchList()
})
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
</style> 