<template>
  <el-select
    v-model="selectedValue"
    :placeholder="placeholder"
    clearable
    filterable
    :loading="loading"
    @change="handleChange"
    @focus="handleFocus"
    style="width: 100%"
  >
    <el-option
      v-for="employee in employeeOptions"
      :key="employee.id"
      :label="employee.name"
      :value="employee.id"
    >
      <div class="employee-option">
        <span class="employee-name">{{ employee.name }}</span>
        <span class="employee-level">({{ employee.levels || '无职级' }})</span>
      </div>
    </el-option>
  </el-select>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getEmployeeList } from '@/api/system'

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  placeholder: {
    type: String,
    default: '请选择员工'
  },
  status: {
    type: Number,
    default: 0 // 默认只显示在职员工
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

const selectedValue = ref('')
const employeeOptions = ref([])
const loading = ref(false)

// 监听外部值变化
watch(() => props.modelValue, (newVal) => {
  selectedValue.value = newVal
}, { immediate: true })

// 监听内部值变化
watch(selectedValue, (newVal) => {
  emit('update:modelValue', newVal)
})

// 获取员工列表
async function fetchEmployeeOptions() {
  try {
    loading.value = true
    const response = await getEmployeeList({ status: props.status })
    if (response.code === 200) {
      employeeOptions.value = response.data || []
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

// 处理选择变化
function handleChange(value) {
  const selectedEmployee = employeeOptions.value.find(emp => emp.id === value)
  emit('change', {
    id: value,
    employee: selectedEmployee
  })
}

// 处理焦点事件
function handleFocus() {
  if (employeeOptions.value.length === 0) {
    fetchEmployeeOptions()
  }
}

// 根据ID获取员工信息
function getEmployeeById(id) {
  return employeeOptions.value.find(emp => emp.id === id)
}

// 根据名称匹配员工
function matchEmployeeByName(name) {
  if (!name) return null
  return employeeOptions.value.find(emp => emp.name === name)
}

// 暴露方法给父组件
defineExpose({
  fetchEmployeeOptions,
  getEmployeeById,
  matchEmployeeByName
})

// 组件挂载时加载员工列表
onMounted(() => {
  fetchEmployeeOptions()
})
</script>

<style scoped>
.employee-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.employee-name {
  font-weight: 500;
}

.employee-level {
  color: #909399;
  font-size: 12px;
}
</style> 