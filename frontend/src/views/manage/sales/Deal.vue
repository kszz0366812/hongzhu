<template>
  <el-card>
    <div class="action-bar">
      <div class="search-bar">
        <el-input v-model="search.keyword" placeholder="关键词（销售单号、客户名称、商品名称、客户编码）" style="width: 300px; margin-right: 8px;" clearable />
        <el-date-picker v-model="search.salesDateTime" type="datetime" placeholder="销售时间" style="width: 180px; margin-right: 8px;" clearable />
        <el-input v-model="search.customerManager" placeholder="客户经理" style="width: 120px; margin-right: 8px;" clearable />
        <el-button type="primary" @click="fetchList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      <div class="button-bar">
        <el-button type="primary" @click="openDialog()">新增成交记录</el-button>
        <el-button @click="handleImport">导入</el-button>
        <el-button @click="handleDownloadTemplate">下载模板</el-button>
      </div>
    </div>
    <el-table :data="list" border stripe style="width: 100%">
      <el-table-column prop="salesOrderNo" label="销售单号" width="160"  align="center" />
      <el-table-column prop="salesDateTime" label="销售时间" width="160" align="center">
        <template #default="scope">
          {{ formatDateTime(scope.row.salesDateTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="customerName" label="客户名称" width="170"  align="center" />
      <el-table-column prop="distributor" label="配送商" width="160" align="center" />
      <el-table-column prop="salesperson" label="业务员" width="100" align="center" />
      <el-table-column prop="productName" label="商品名称" width="230" align="center" />
      <el-table-column prop="specification" label="规格属性" width="120" align="center" />
      <el-table-column prop="salesQuantity" label="销售数量" width="100" align="center" />
      <el-table-column prop="isGift" label="是否赠品" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.isGift === 1 ? 'success' : 'info'">
            {{ scope.row.isGift === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="salesUnit" label="销售单位" width="100" align="center" />
      <el-table-column prop="customerCategory" label="客户分类" width="100" align="center" />
      <el-table-column prop="conversionUnit" label="换算单位/行" width="120" align="center" />
      <el-table-column prop="productSeries" label="商品系列" width="150" align="center" />
      <el-table-column fixed="right" label="操作" width="180" align="center">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model="page"
      :total="page.total"
      layout="total, prev, pager, next, sizes"
      @current-change="fetchList"
      @size-change="fetchList"
      style="margin-top: 16px; text-align: right;"
    />
    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑成交记录' : '新增成交记录'" width="800px">
      <el-form :model="dialog.form" label-width="120px" :rules="rules" ref="formRef">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="销售单号" prop="salesOrderNo">
              <el-input v-model="dialog.form.salesOrderNo" placeholder="请输入销售单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="销售时间" prop="salesDateTime">
              <el-date-picker v-model="dialog.form.salesDateTime" type="datetime" placeholder="请选择销售时间" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户名称" prop="customerName">
              <TerminalSelector
                v-model="selectedTerminal"
                placeholder="请选择终端客户"
                @change="handleTerminalChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户编码" prop="customerCode">
              <el-input v-model="dialog.form.customerCode" placeholder="选择客户名称后自动填充" readonly />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="配送商" prop="distributor">
              <WholesalerSelector
                v-model="selectedWholesaler"
                placeholder="请选择批发商"
                @change="handleWholesalerChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="配送商类" prop="distributorType">
              <el-input v-model="dialog.form.distributorType" placeholder="选择批发商后自动填充" readonly />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户经理" prop="customerManager">
              <el-input v-model="dialog.form.customerManager" placeholder="选择终端客户后自动填充" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业务员" prop="salesperson">
              <EmployeeSelector
                v-model="selectedEmployee"
                placeholder="请选择业务员"
                @change="handleEmployeeChange"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="productName">
              <el-input v-model="dialog.form.productName" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格属性" prop="specification">
              <el-input v-model="dialog.form.specification" placeholder="请输入规格属性" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="销售数量" prop="salesQuantity">
              <el-input-number v-model="dialog.form.salesQuantity" :precision="2" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="销售单位" prop="salesUnit">
              <el-select v-model="dialog.form.salesUnit" placeholder="请选择销售单位" style="width: 100%">
                <el-option label="箱" value="箱" />
                <el-option label="瓶" value="瓶" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户类型" prop="customerType">
              <el-input v-model="dialog.form.customerType" placeholder="请输入客户类型" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否赠品" prop="isGift">
              <el-select v-model="dialog.form.isGift" placeholder="请选择是否赠品" style="width: 100%">
                <el-option label="否" :value="0" />
                <el-option label="是" :value="1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="业务员上级" prop="salespersonSupervisor">
              <el-input v-model="dialog.form.salespersonSupervisor" placeholder="选择业务员后自动填充" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户分类" prop="customerCategory">
              <el-select v-model="dialog.form.customerCategory" placeholder="请选择客户类型" style="width: 100%">
                <el-option label="终端" value="终端" />
                <el-option label="批发商" value="批发商" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="换算单位/行" prop="conversionUnit">
              <el-input v-model="dialog.form.conversionUnit" placeholder="请输入换算单位/行" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品系列" prop="productSeries">
              <el-input v-model="dialog.form.productSeries" placeholder="请输入商品系列" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submit" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>
    
    <!-- 导入对话框 -->
    <ImportDialog
      v-model="importDialog.visible"
      import-type="成交记录导入"
      @success="handleImportSuccess"
    />
  </el-card>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { dealRecordApi } from '@/api/dealRecord'
import TerminalSelector from '@/components/TerminalSelector.vue'
import WholesalerSelector from '@/components/WholesalerSelector.vue'
import EmployeeSelector from '@/components/EmployeeSelector.vue'
import ImportDialog from '@/components/ImportDialog.vue'

// 响应式数据
const list = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const formRef = ref()

// 选中的终端信息
const selectedTerminal = ref(null)

// 选中的批发商信息
const selectedWholesaler = ref(null)

// 选中的员工信息
const selectedEmployee = ref(null)

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
  salesDateTime: '',
  customerManager: ''
})

// 对话框数据
const dialog = reactive({
  visible: false,
  form: {
    salesOrderNo: '',
    salesDateTime: '',
    customerName: '',
    distributor: '',
    distributorType: '',
    customerManager: '',
    salesperson: '',
    productName: '',
    specification: '',
    salesQuantity: 0,
    customerCode: '',
    customerType: '',
    isGift: 0,
    salesUnit: '',
    salespersonSupervisor: '',
    customerCategory: '',
    conversionUnit: '',
    productSeries: ''
  }
})

// 表单验证规则
const rules = {
  salesOrderNo: [
    { required: true, message: '请输入销售单号', trigger: 'blur' }
  ],
  salesDateTime: [
    { required: true, message: '请选择销售时间', trigger: 'change' }
  ],
  customerName: [
    { required: true, message: '请选择客户名称', trigger: 'change' }
  ],
  productName: [
    { required: true, message: '请输入商品名称', trigger: 'blur' }
  ],
  salesQuantity: [
    { required: true, message: '请输入销售数量', trigger: 'blur' }
  ]
}

// 获取列表数据
const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      current: page.current,
      size: page.size,
      keyword: search.keyword,
      salesDateTime: search.salesDateTime,
      customerManager: search.customerManager
    }
    
    const res = await dealRecordApi.getPage(params)
    
    if (res.code === 200) {
      list.value = res.data.list
      page.total = res.data.total
    } else {
      ElMessage.error(res.message || '获取数据失败')
    }
  } catch (error) {
    console.error('获取成交记录列表失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索条件
const resetSearch = () => {
  Object.assign(search, {
    keyword: '',
    salesDateTime: '',
    customerManager: ''
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
    // 如果有客户名称，需要设置对应的终端信息
    if (row.customerName && row.customerCode) {
      selectedTerminal.value = {
        terminalName: row.customerName,
        terminalCode: row.customerCode,
        customerManager: row.customerManager
      }
    }
    
    // 如果有配送商，需要设置对应的批发商信息
    if (row.distributor) {
      selectedWholesaler.value = {
        dealerName: row.distributor,
        level: row.distributorType ? (row.distributorType === '一批商' ? '一级' : '二级') : ''
      }
    }
    
    // 如果有业务员，需要设置对应的员工信息
    if (row.salesperson) {
      selectedEmployee.value = {
        name: row.salesperson,
        directLeader: row.salespersonSupervisor
      }
    }
  } else {
    // 新增模式
    Object.assign(dialog.form, {
      salesOrderNo: '',
      salesDateTime: new Date(), // 默认为当前时间
      customerName: '',
      distributor: '',
      distributorType: '',
      customerManager: '',
      salesperson: '',
      productName: '',
      specification: '',
      salesQuantity: 0,
      customerCode: '', // 新增时清空客户编码
      customerManager: '', // 新增时清空客户经理
      customerType: '',
      isGift: 0,
      salesUnit: '',
      salespersonSupervisor: '',
      customerCategory: '',
      conversionUnit: '',
      productSeries: '',
      customerManagerName: ''
    })
    selectedTerminal.value = null
    selectedWholesaler.value = null
    selectedEmployee.value = null
  }
}

// 提交表单
const submit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    const api = dialog.form.id ? dealRecordApi.update : dealRecordApi.save
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

// 删除成交记录
const remove = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条成交记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await dealRecordApi.delete(row.id)
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
  ElMessage.success('成交记录导入成功！')
  fetchList()
}

// 下载模板
const handleDownloadTemplate = () => {
  // 使用导入组件的下载模板功能
  importDialog.visible = true
}

// 终端选择变化处理
const handleTerminalChange = (terminal) => {
  if (terminal) {
    dialog.form.customerName = terminal.terminalName
    dialog.form.customerCode = terminal.terminalCode
    dialog.form.customerManager = terminal.customerManager
  } else {
    dialog.form.customerName = ''
    dialog.form.customerCode = ''
    dialog.form.customerManager = ''
  }
}

// 批发商选择变化处理
const handleWholesalerChange = (wholesaler) => {
  if (wholesaler) {
    dialog.form.distributor = wholesaler.dealerName
    // 根据批发商等级自动设置配送商类
    if (wholesaler.level) {
      // 假设等级为"一级"对应"一批商"，"二级"对应"二批商"
      if (wholesaler.level.includes('一级') || wholesaler.level.includes('1')) {
        dialog.form.distributorType = '一批商'
      } else if (wholesaler.level.includes('二级') || wholesaler.level.includes('2')) {
        dialog.form.distributorType = '二批商'
      } else {
        // 默认设置为一批商
        dialog.form.distributorType = '一批商'
      }
    }
  } else {
    dialog.form.distributor = ''
    dialog.form.distributorType = ''
  }
}

// 员工选择变化处理
const handleEmployeeChange = async (employee) => {
  console.log('选择的员工信息:', employee)
  if (employee) {
    dialog.form.salesperson = employee.name
    // 直接使用directLeader字段作为业务员上级
    if (employee.directLeader) {
      dialog.form.salespersonSupervisor = employee.directLeader
      console.log('设置业务员上级为:', employee.directLeader)
    } else {
      dialog.form.salespersonSupervisor = ''
      console.log('员工没有直属领导')
    }
  } else {
    dialog.form.salesperson = ''
    dialog.form.salespersonSupervisor = ''
  }
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const d = new Date(dateTime)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`
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