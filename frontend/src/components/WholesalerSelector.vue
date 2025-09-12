<template>
  <div class="wholesaler-selector">
    <el-input
      v-model="displayValue"
      :placeholder="placeholder"
      readonly
      @click="openDialog"
      style="cursor: pointer;"
    >
      <template #suffix>
        <el-icon><ArrowDown /></el-icon>
      </template>
    </el-input>
    
    <!-- 批发商选择弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="选择批发商"
      width="900px"
      :close-on-click-modal="false"
    >
      <div class="dialog-content">
        <!-- 搜索栏 -->
        <div class="search-bar">
          <el-input
            v-model="searchKeyword"
            placeholder="请输入批发商名称或编码进行搜索"
            clearable
            @input="handleSearch"
            style="width: 300px;"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        
        <!-- 批发商列表 -->
        <el-table
          :data="wholesalerList"
          border
          stripe
          style="width: 100%; margin-top: 16px;"
          @row-click="selectWholesaler"
          highlight-current-row
          :current-row="selectedWholesaler"
        >
          <el-table-column prop="dealerCode" label="批发商编码" align="center" />
          <el-table-column prop="dealerName" label="批发商名称" align="center" />
          <el-table-column prop="level" label="等级" align="center" />
          <el-table-column prop="customerManager" label="客户经理" align="center" />
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
        <el-button type="primary" @click="confirmSelect" :disabled="!selectedWholesaler">
          确认选择
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowDown, Search } from '@element-plus/icons-vue'
import { wholesalerApi } from '@/api/wholesaler'

// Props
const props = defineProps({
  modelValue: {
    type: Object,
    default: () => null
  },
  placeholder: {
    type: String,
    default: '请选择批发商'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'change'])

// 响应式数据
const dialogVisible = ref(false)
const displayValue = ref('')
const selectedWholesaler = ref(null)
const wholesalerList = ref([])
const loading = ref(false)

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索参数
const searchKeyword = ref('')

// 获取批发商列表
const fetchWholesalerList = async () => {
  try {
    loading.value = true
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined
    }
    
    const res = await wholesalerApi.getPage(params)
    if (res.code === 200) {
      wholesalerList.value = res.data.list || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取批发商列表失败')
    }
  } catch (error) {
    console.error('获取批发商列表失败:', error)
    ElMessage.error('获取批发商列表失败')
  } finally {
    loading.value = false
  }
}

// 打开弹窗
const openDialog = () => {
  if (props.disabled) return
  dialogVisible.value = true
  currentPage.value = 1
  searchKeyword.value = ''
  selectedWholesaler.value = null
  fetchWholesalerList()
}

// 选择批发商
const selectWholesaler = (row) => {
  selectedWholesaler.value = row
}

// 确认选择
const confirmSelect = () => {
  if (selectedWholesaler.value) {
    displayValue.value = selectedWholesaler.value.dealerName
    // 直接返回完整的批发商对象
    emit('update:modelValue', selectedWholesaler.value)
    emit('change', selectedWholesaler.value)
    dialogVisible.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  fetchWholesalerList()
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  fetchWholesalerList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchWholesalerList()
}

// 监听外部值变化
watch(() => props.modelValue, (newVal) => {
  if (newVal && typeof newVal === 'object') {
    // 如果传入的是批发商对象，显示批发商名称
    displayValue.value = newVal.dealerName || ''
  } else {
    displayValue.value = ''
  }
}, { immediate: true })

// 监听禁用状态
watch(() => props.disabled, (newVal) => {
  if (newVal) {
    displayValue.value = ''
    emit('update:modelValue', null)
  }
})
</script>

<style scoped>
.wholesaler-selector {
  width: 100%;
}

.dialog-content {
  min-height: 450px;
}

.search-bar {
  display: flex;
  justify-content: flex-start;
  align-items: center;
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
</style>
