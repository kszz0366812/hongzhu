<template>
  <el-card style="width: 100%;">
    <div style="margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center;">
      <div style="display: flex; gap: 8px; align-items: center;">
        <el-input v-model="search.keyword" placeholder="关键词（批发商名称、编码、客户经理、联系人、联系电话）" style="width: 350px;" clearable />
        <el-select v-model="search.level" placeholder="等级" style="width: 120px;" clearable>
          <el-option v-for="level in levelOptions" :key="level" :value="level" :label="level" />
        </el-select>
        <el-button type="primary" @click="fetchList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      <div style="display: flex; gap: 8px;">
        <el-button type="primary" @click="openDialog()">新增批发商</el-button>
        <el-button @click="handleImport">导入</el-button>
        <el-button @click="handleDownloadTemplate">下载模板</el-button>
      </div>
    </div>
    
    <el-table :data="list" border stripe style="width: 100%" v-loading="loading" :row-key="(row) => row.id">
      <el-table-column prop="dealerName" label="批发商名称" align="center" />
      <el-table-column prop="dealerCode" label="批发商编码" align="center" />
      <el-table-column prop="level" label="等级" align="center" />
      <el-table-column prop="contactPerson" label="联系人" align="center" />
      <el-table-column prop="contactPhone" label="联系电话" align="center" />
      <el-table-column prop="customerManager" label="客户经理" align="center" />
      <el-table-column label="操作" fixed="right" align="center">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination
      v-model:current-page="page.current"
      v-model:page-size="page.size"
      :total="page.total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="fetchList"
      @size-change="fetchList"
      style="margin-top: 16px; text-align: right;"
    />
    
    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑批发商' : '新增批发商'" width="600px">
      <el-form :model="dialog.form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="批发商名称" prop="dealerName">
          <el-input v-model="dialog.form.dealerName" placeholder="请输入批发商名称" />
        </el-form-item>
        <el-form-item label="批发商编码" prop="dealerCode">
          <el-input v-model="dialog.form.dealerCode" placeholder="请输入批发商编码" />
        </el-form-item>
        <el-form-item label="等级" prop="level">
          <el-select v-model="dialog.form.level" placeholder="请选择等级" style="width: 100%;">
            <el-option v-for="level in levelOptions" :key="level" :value="level" :label="level" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="dialog.form.contactPerson" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="dialog.form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="客户经理" prop="customerManager">
          <EmployeeSelector
            v-model="selectedCustomerManager"
            placeholder="请选择客户经理"
            @change="handleCustomerManagerChange"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submit" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>

    <ImportDialog
      v-model="importDialog.visible"
      import-type="批发商导入"
      @success="handleImportSuccess"
    />
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { wholesalerApi } from '@/api/wholesaler'
import EmployeeSelector from '@/components/EmployeeSelector.vue'
import ImportDialog from '@/components/ImportDialog.vue'
import ExcelTemplateUtil from '@/utils/excelTemplate'

// 响应式数据
const list = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const formRef = ref()
const levelOptions = reactive(['一批商','二批商'])

// 选中的客户经理
const selectedCustomerManager = ref(null)

// 导入对话框
const importDialog = reactive({
  visible: false
})

// 分页参数
const page = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 搜索条件
const search = reactive({
  keyword: '',
  level: ''
})

// 对话框数据
const dialog = reactive({
  visible: false,
  form: {
    dealerCode: '',
    dealerName: '',
    level: '',
    contactPerson: '',
    contactPhone: '',
    customerManager: ''
  }
})

// 表单验证规则
const rules = {
  dealerCode: [
    { required: true, message: '请输入批发商编码', trigger: 'blur' }
  ],
  dealerName: [
    { required: true, message: '请输入批发商名称', trigger: 'blur' }
  ],
  level: [
    { required: true, message: '请选择等级', trigger: 'change' }
  ]
}

// 获取等级选项
const fetchLevels = async () => {
  try {
    const res = await wholesalerApi.getLevels()
    if (res.code === 200) {
      levelOptions.value = res.data || []
    }
  } catch (error) {
    console.error('获取等级列表失败:', error)
  }
}

// 获取列表数据
const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      current: page.current,
      size: page.size,
      keyword: search.keyword,
      level: search.level
    }
    
    const res = await wholesalerApi.getPage(params)
    
    if (res.code === 200) {
      list.value = res.data.list
      page.total = res.data.total
    } else {
      ElMessage.error(res.message || '获取数据失败')
    }
  } catch (error) {
    console.error('获取批发商列表失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 处理客户经理选择变化
const handleCustomerManagerChange = (employee) => {
  if (employee) {
    dialog.form.customerManager = employee.name
  } else {
    dialog.form.customerManager = ''
  }
}

// 重置搜索条件
const resetSearch = () => {
  Object.assign(search, {
    keyword: '',
    level: ''
  })
  page.current = 1
  fetchList()
}

// 打开对话框
const openDialog = (row) => {
  dialog.visible = true
  if (row) {
    // 编辑模式
    Object.assign(dialog.form, row)
    // 如果有客户经理，需要设置对应的员工信息
    if (row.customerManager) {
      selectedCustomerManager.value = {
        name: row.customerManager
      }
    }
  } else {
    // 新增模式
    Object.assign(dialog.form, {
      dealerCode: '',
      dealerName: '',
      level: '',
      contactPerson: '',
      contactPhone: '',
      customerManager: ''
    })
    selectedCustomerManager.value = null
  }
}

// 提交表单
const submit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    const api = dialog.form.id ? wholesalerApi.update : wholesalerApi.save
    const res = await api(dialog.form)
    
    if (res.code === 200) {
      ElMessage.success(dialog.form.id ? '修改成功' : '新增成功')
      dialog.visible = false
      fetchList()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

// 删除批发商
const remove = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个批发商吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await wholesalerApi.delete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 导入功能
const handleImport = () => {
  importDialog.visible = true
}

// 导入成功回调
const handleImportSuccess = (result) => {
  ElMessage.success('批发商数据导入成功！')
  fetchList()
}

// 下载模板
const handleDownloadTemplate = async () => {
  try {
    const result = await ExcelTemplateUtil.downloadWholesalerTemplate()
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

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

// 页面加载时获取数据
onMounted(() => {
  fetchLevels()
  fetchList()
})
</script>

<style scoped>
.el-card {
  margin: 20px;
}
</style>