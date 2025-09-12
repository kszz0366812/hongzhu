<template>
  <el-card>
    <div class="action-bar">
      <div class="search-bar">
        <EmployeeSelector
          v-model="selectedVisitor"
          placeholder="选择拜访人"
          style="width: 180px; margin-right: 8px;"
          @change="handleVisitorChange"
        />
        <TerminalSelector
          v-model="selectedTerminal"
          placeholder="选择终端"
          style="width: 180px; margin-right: 8px;"
          @change="handleTerminalChange"
        />
        <el-date-picker
          v-model="searchParams.startTime"
          type="datetime"
          placeholder="开始时间"
          style="width: 180px; margin-right: 8px;"
        />
        <el-date-picker
          v-model="searchParams.endTime"
          type="datetime"
          placeholder="结束时间"
          style="width: 180px; margin-right: 8px;"
        />
        <el-select v-model="searchParams.isDeal" placeholder="是否成交" style="width: 120px; margin-right: 8px;" clearable>
          <el-option :value="1" label="是" />
          <el-option :value="0" label="否" />
        </el-select>
        <el-button type="primary" @click="fetchList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      <div class="button-bar">
        <el-button type="primary" @click="handleAdd">新增</el-button>
        <el-button @click="handleImport">导入</el-button>
        <el-button @click="handleDownloadTemplate">下载模板</el-button>
      </div>
    </div>
    
    <el-table :data="list" border stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="visitTime" label="拜访时间"  align="center">
        <template #default="scope">
          {{ formatDate(scope.row.visitTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="visitorName" label="拜访人姓名" align="center" />
      <el-table-column prop="terminalCode" label="终端编码"  align="center" />
      <el-table-column prop="terminalName" label="终端名称" align="center" />
      <el-table-column prop="dealStatus" label="是否成交"  align="center">
        <template #default="scope">
          <el-tag :type="scope.row.dealStatus === '是' ? 'success' : 'info'">
            {{ scope.row.dealStatus }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination
      v-model:current-page="pagination.current"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="fetchList"
      @size-change="fetchList"
      style="margin-top: 16px; text-align: right;"
    />
    
    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑拜访记录' : '新增拜访记录'" width="600px">
      <el-form :model="dialog.form" :rules="dialog.rules" ref="dialogFormRef" label-width="120px">
        <el-form-item label="拜访商户" prop="terminalCode">
          <TerminalSelector
            v-model="dialog.selectedTerminal"
            placeholder="请选择拜访商户"
            @change="handleDialogTerminalChange"
          />
        </el-form-item>
        <el-form-item label="拜访人">
          <el-input v-model="dialog.form.visitorName" disabled placeholder="选择商户后自动填充" />
        </el-form-item>
        <el-form-item label="拜访时间" prop="visitTime">
          <el-date-picker
            v-model="dialog.form.visitTime"
            type="datetime"
            placeholder="请选择拜访时间"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="是否成交" prop="isDeal">
          <el-select v-model="dialog.form.isDeal" placeholder="请选择是否成交" style="width: 100%;">
            <el-option :value="1" label="是" />
            <el-option :value="0" label="否" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="dialog.submitting">保存</el-button>
      </template>
    </el-dialog>
    
    <!-- 导入对话框 -->
    <ImportDialog
      v-model="importDialog.visible"
      import-type="拜访记录导入"
      @success="handleImportSuccess"
    />
  </el-card>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ImportDialog from '@/components/ImportDialog.vue'
import { 
  getVisitRecordPage, 
  saveVisitRecord, 
  updateVisitRecord, 
  deleteVisitRecord 
} from '@/api/visit'
import EmployeeSelector from '@/components/EmployeeSelector.vue'
import TerminalSelector from '@/components/TerminalSelector.vue'
import ExcelTemplateUtil from '@/utils/excelTemplate'

// 响应式数据
const list = ref([])
const loading = ref(false)
const dialogFormRef = ref()

// 选中的组件数据
const selectedVisitor = ref(null)
const selectedTerminal = ref(null)

// 分页参数
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 搜索参数
const searchParams = reactive({
  customerManager: '',
  terminalCode: '',
  startTime: null,
  endTime: null,
  isDeal: null
})

// 对话框
const dialog = reactive({
  visible: false,
  submitting: false,
  selectedTerminal: null,
  form: {
    id: null,
    visitorCode: '',
    visitorName: '', // 拜访人姓名（只读）
    visitTime: null,
    terminalCode: '',
    isDeal: 0
  },
  rules: {
    visitTime: [
      { required: true, message: '请选择拜访时间', trigger: 'change' }
    ],
    terminalCode: [
      { required: true, message: '请选择拜访商户', trigger: 'change' }
    ],
    isDeal: [
      { required: true, message: '请选择是否成交', trigger: 'change' }
    ]
  }
})

// 导入对话框
const importDialog = reactive({
  visible: false
})

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 格式化日期（只显示日期，不显示时间）
const formatDate = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 获取列表数据
const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchParams
    }
    
    const res = await getVisitRecordPage(params)
    if (res.code === 200) {
      list.value = res.data.list || []
      pagination.total = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取拜访记录列表失败')
    }
  } catch (error) {
    ElMessage.error('获取拜访记录列表失败')
    console.error('获取拜访记录列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchParams.customerManager = ''
  searchParams.terminalCode = ''
  searchParams.startTime = null
  searchParams.endTime = null
  searchParams.isDeal = null
  selectedVisitor.value = null
  selectedTerminal.value = null
  pagination.current = 1
  fetchList()
}

// 处理拜访人选择变化
const handleVisitorChange = (employee) => {
  if (employee) {
    searchParams.customerManager = employee.name  // 使用员工姓名
  } else {
    searchParams.customerManager = ''
  }
}

// 处理终端选择变化
const handleTerminalChange = (terminal) => {
  if (terminal) {
    searchParams.terminalCode = terminal.terminalCode
  } else {
    searchParams.terminalCode = ''
  }
}

// 处理对话框中的终端选择变化
const handleDialogTerminalChange = (terminal) => {
  if (terminal) {
    dialog.form.terminalCode = terminal.terminalCode
    dialog.form.visitorName = terminal.customerManager || '' // 自动填充客户经理
  } else {
    dialog.form.terminalCode = ''
    dialog.form.visitorName = ''
  }
}

// 新增
const handleAdd = () => {
  dialog.form = {
    id: null,
    visitorCode: '',
    visitorName: '',
    visitTime: new Date(), // 默认当前时间
    terminalCode: '',
    isDeal: 0
  }
  dialog.selectedTerminal = null
  dialog.visible = true
}

// 编辑
const handleEdit = (row) => {
  dialog.form = {
    id: row.id,
    visitorCode: row.visitorCode,
    visitorName: row.visitorName, // 直接使用列表中的拜访人姓名
    visitTime: row.visitTime ? new Date(row.visitTime) : null,
    terminalCode: row.terminalCode,
    isDeal: row.isDeal
  }
  
  // 构造终端选择器对象
  if (row.terminalCode && row.terminalName) {
    dialog.selectedTerminal = {
      terminalCode: row.terminalCode,
      terminalName: row.terminalName,
      customerManager: row.visitorName // 客户经理就是拜访人
    }
  } else {
    dialog.selectedTerminal = null
  }
  
  dialog.visible = true
}

// 提交表单
const handleSubmit = async () => {
  if (!dialogFormRef.value) return
  
  try {
    await dialogFormRef.value.validate()
    dialog.submitting = true
    
    const formData = {
      ...dialog.form,
      visitTime: dialog.form.visitTime ? dialog.form.visitTime.toISOString() : null
    }
    
    const res = dialog.form.id 
      ? await updateVisitRecord(formData)
      : await saveVisitRecord(formData)
    
    if (res.code === 200) {
      ElMessage.success(dialog.form.id ? '修改成功' : '新增成功')
      dialog.visible = false
      fetchList()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    if (error !== false) { // 不是表单验证错误
      ElMessage.error('操作失败')
      console.error('提交表单失败:', error)
    }
  } finally {
    dialog.submitting = false
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除这条拜访记录吗？`,
      '提示',
      { type: 'warning' }
    )
    
    const res = await deleteVisitRecord(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error('删除拜访记录失败:', error)
    }
  }
}

// 导入
const handleImport = () => {
  importDialog.visible = true
}

// 下载模板
const handleDownloadTemplate = async () => {
  try {
    const result = await ExcelTemplateUtil.downloadVisitRecordTemplate()
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
const handleImportSuccess = (result) => {
  ElMessage.success('拜访记录导入成功！')
  fetchList()
}

// 页面加载时获取数据
onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.action-bar { display: flex; justify-content: space-between; align-items: center; gap: 8px; margin-bottom: 16px; }
.search-bar { display: flex; align-items: center; }
.button-bar { display: flex; gap: 8px; }
</style> 