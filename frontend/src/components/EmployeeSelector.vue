<template>
  <div class="employee-selector">
    <el-input
      v-model="displayValue"
      :placeholder="placeholder"
      readonly
      @click="openDialog"
      style="cursor: pointer;"
    >
      <template #suffix>
        <el-icon v-if="hasSelection" @click.stop="clearSelection" style="cursor: pointer; margin-right: 8px;"><Close /></el-icon>
        <el-icon><ArrowDown /></el-icon>
      </template>
    </el-input>
    
    <!-- 员工选择弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="选择员工"
      width="1000px"
      :close-on-click-modal="false"
      :style="{ top: top }"
    >
      <div class="dialog-content">
        <!-- 搜索栏 -->
        <div class="search-bar">
          <el-input
            v-model="searchKeyword"
            placeholder="请输入员工姓名或工号进行搜索"
            clearable
            @input="handleSearch"
            style="width: 300px;"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          
          <!-- 多选模式下显示已选人员 -->
          <div v-if="multiple && selectedEmployees.length > 0" class="selected-employees">
            <span class="selected-label">已选择：</span>
            <el-tag
              v-for="employee in selectedEmployees"
              :key="employee.id"
              closable
              @close="removeSelectedEmployee(employee)"
              class="selected-tag"
            >
              {{ employee.name || employee.employeeName || '未知员工' }}
            </el-tag>
          </div>
        </div>
        
        <!-- 员工列表 -->
        <el-table
          :data="employeeList"
          border
          stripe
          style="width: 100%; margin-top: 16px;"
          @row-click="selectEmployee"
          :highlight-current-row="!multiple"
          :current-row="multiple ? null : selectedEmployee"
          @selection-change="handleSelectionChange"
          ref="tableRef"
          :row-class-name="getRowClassName"
        >
          <el-table-column v-if="multiple" type="selection" width="55" />
          <el-table-column prop="employeeCode" label="工号" align="center" />
          <el-table-column prop="name" label="姓名" align="center" />
          <el-table-column prop="directLeader" label="上级领导" align="center" />
          <el-table-column prop="position" label="岗位" align="center" />
          <el-table-column prop="rank" label="职级" align="center" />
          <el-table-column prop="regionLevel1" label="大区" align="center" />
          <el-table-column prop="regionLevel2" label="地市" align="center" />
          <el-table-column prop="regionLevel3" label="区域" align="center" />
         
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSelect" :disabled="!hasSelection">
          {{ multiple ? '确认选择' : '确认选择' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowDown, Search, Close } from '@element-plus/icons-vue'
import { getEmployeePage } from '@/api/system'

// Props
const props = defineProps({
  modelValue: {
    type: [Object, Array],
    default: () => null
  },
  placeholder: {
    type: String,
    default: '请选择员工'
  },
  disabled: {
    type: Boolean,
    default: false
  },
  multiple: {
    type: Boolean,
    default: false
  },
  existingMembers: {
    type: Array,
    default: () => []
  },
  top: {
    type: String,
    default: '5vh'
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'change'])

// 响应式数据
const dialogVisible = ref(false)
const displayValue = ref('')
const selectedEmployee = ref(null)
const selectedEmployees = ref([])
const employeeList = ref([])
const loading = ref(false)
const isSettingSelection = ref(false)

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索参数
const searchKeyword = ref('')

// 表格引用
const tableRef = ref(null)

// 计算属性
const hasSelection = computed(() => {
  if (props.multiple) {
    return selectedEmployees.value.length > 0
  } else {
    return selectedEmployee.value !== null
  }
})

// 获取员工列表
const fetchEmployeeList = async () => {
  try {
    loading.value = true
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined
    }
    
    const res = await getEmployeePage(params)
    if (res.code === 200) {
      employeeList.value = res.data.list || []
      total.value = res.data.total || 0
      
      // 多选模式下，设置表格的选中状态
      if (props.multiple && selectedEmployees.value.length > 0) {
        nextTick(() => {
          setTableSelection()
        })
      }
    } else {
      ElMessage.error(res.message || '获取员工列表失败')
    }
  } catch (error) {
    console.error('获取员工列表失败:', error)
    ElMessage.error('获取员工列表失败')
  } finally {
    loading.value = false
  }
}

// 设置表格选中状态
const setTableSelection = () => {
  if (!tableRef.value) {
    return
  }
  
  // 设置标志，防止重复调用
  if (isSettingSelection.value) {
    return
  }
  
  isSettingSelection.value = true
  
  try {
    // 先清除所有选中状态
    tableRef.value.clearSelection()
    
    if (selectedEmployees.value.length === 0) {
      return
    }
    
    // 延迟一点时间确保表格完全渲染
    setTimeout(() => {
      if (tableRef.value) {
        employeeList.value.forEach(row => {
          const isSelected = selectedEmployees.value.some(emp => emp.id === row.id)
          if (isSelected) {
            tableRef.value.toggleRowSelection(row, true)
          }
        })
      }
    }, 100)
  } finally {
    // 确保标志总是被重置
    setTimeout(() => {
      isSettingSelection.value = false
    }, 200)
  }
}

// 获取行样式类名
const getRowClassName = ({ row }) => {
  if (props.multiple) {
    // 多选模式下，检查当前行是否在已选列表中
    const isSelected = selectedEmployees.value.some(emp => emp.id === row.id)
    return isSelected ? 'selected-row' : ''
  }
  return ''
}

// 打开弹窗
const openDialog = () => {
  if (props.disabled) return
  dialogVisible.value = true
  currentPage.value = 1
  searchKeyword.value = ''
  
  // 每次打开弹窗都恢复到真正的初始状态
  if (props.multiple) {
    selectedEmployees.value = [...props.existingMembers]
  } else {
    selectedEmployee.value = props.modelValue
  }
  
  fetchEmployeeList()
}

// 选择员工
const selectEmployee = (row) => {
  if (props.multiple) {
    // 多选模式下，切换选中状态
    const index = selectedEmployees.value.findIndex(emp => emp.id === row.id)
    if (index > -1) {
      // 移除选中
      selectedEmployees.value.splice(index, 1)
    } else {
      // 添加选中
      selectedEmployees.value.push(row)
    }
    // 同步更新表格的选中状态
    if (tableRef.value) {
      tableRef.value.toggleRowSelection(row, index === -1)
    }
  } else {
    selectedEmployee.value = row
  }
}

// 多选变化处理
const handleSelectionChange = (selection) => {
  // 如果是正在设置选中状态，忽略选择变化
  if (isSettingSelection.value) {
    return
  }
  
  if (props.multiple) {
    // 获取当前页面的所有员工ID
    const currentPageIds = employeeList.value.map(row => row.id)
    const selectedIds = selection.map(row => row.id)
    
    // 移除当前页面中未选中的员工
    selectedEmployees.value = selectedEmployees.value.filter(emp => 
      !currentPageIds.includes(emp.id) || selectedIds.includes(emp.id)
    )
    
    // 添加当前页面中新选中的员工
    selection.forEach(row => {
      if (!selectedEmployees.value.some(emp => emp.id === row.id)) {
        selectedEmployees.value.push(row)
      }
    })
  }
}

// 移除已选员工
const removeSelectedEmployee = (employee) => {
  const index = selectedEmployees.value.findIndex(emp => emp.id === employee.id)
  if (index > -1) {
    selectedEmployees.value.splice(index, 1)
    // 同步更新表格的选中状态
    if (tableRef.value) {
      tableRef.value.toggleRowSelection(employee, false)
    }
  }
}

// 确认选择
const confirmSelect = () => {
  if (props.multiple) {
    // 多选模式下允许空选择（清除所有选择）
    emit('update:modelValue', selectedEmployees.value)
    emit('change', selectedEmployees.value)
    dialogVisible.value = false
  } else {
    if (selectedEmployee.value) {
      emit('update:modelValue', selectedEmployee.value)
      emit('change', selectedEmployee.value)
      dialogVisible.value = false
    }
  }
}

// 清除选择
const clearSelection = () => {
  selectedEmployee.value = null
  selectedEmployees.value = []
  displayValue.value = ''
  emit('update:modelValue', props.multiple ? [] : null)
  emit('change', props.multiple ? [] : null)
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  fetchEmployeeList()
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  fetchEmployeeList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchEmployeeList()
}

// 监听选中员工变化，更新显示值
watch(() => selectedEmployees.value, (newVal) => {
  if (props.multiple) {
    if (newVal && newVal.length > 0) {
      displayValue.value = `已选择 ${newVal.length} 名员工`
    } else {
      displayValue.value = ''
    }
  }
}, { immediate: true })

// 监听外部值变化
watch(() => props.modelValue, (newVal) => {
  if (!props.multiple) {
    if (newVal && typeof newVal === 'object') {
      displayValue.value = newVal.name || ''
    } else {
      displayValue.value = ''
    }
  }
}, { immediate: true })

// 监听禁用状态
watch(() => props.disabled, (newVal) => {
  if (newVal) {
    displayValue.value = ''
    emit('update:modelValue', props.multiple ? [] : null)
  }
})

// 监听弹窗状态
watch(() => dialogVisible.value, (newVal) => {
  if (newVal) {
    // 弹窗打开时，重置到初始状态
    currentPage.value = 1
    searchKeyword.value = ''
    console.log('props.existingMembers', props.existingMembers)
    console.log('props.multiple', props.multiple)
    if (props.multiple) {
      selectedEmployees.value = [...props.existingMembers]
    } else {
      selectedEmployee.value = props.modelValue
    }
    console.log('selectedEmployees.value', selectedEmployees.value)

    fetchEmployeeList()
  }
})
</script>

<style scoped>
.employee-selector {
  width: 100%;
}

.dialog-content {
  min-height: 500px;
}

.search-bar {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 16px;
}

.selected-employees {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.selected-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.selected-tag {
  margin-right: 4px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}

:deep(.el-table__row) {
  cursor: pointer;
}

:deep(.el-table__row:hover) {
  background-color: var(--el-color-primary-light-9);
}

:deep(.el-table__row.current-row) {
  background-color: var(--el-color-primary-light-8);
  color: var(--el-color-primary);
}

:deep(.el-table__row.current-row:hover) {
  background-color: var(--el-color-primary-light-7);
}

:deep(.el-table__row.current-row td) {
  background-color: transparent;
}

/* 多选模式下选中行的样式 */
:deep(.el-table__row.selected-row) {
  background-color: var(--el-color-primary-light-8) !important;
  color: var(--el-color-primary);
}

:deep(.el-table__row.selected-row:hover) {
  background-color: var(--el-color-primary-light-7) !important;
}

:deep(.el-table__row.selected-row td) {
  background-color: transparent !important;
}
</style>