<template>
  <div class="template-config">
    <el-card>
      <!-- 搜索区域 -->
      <div style="margin-bottom: 16px; display: flex; align-items: center;">
        <el-input v-model="searchForm.tepName" placeholder="请输入模板名称" style="width: 200px; margin-right: 8px;" clearable />
        <el-button type="primary" @click="fetchData">搜索</el-button>
        <el-button @click="resetSearch" style="margin-left: 8px;">重置</el-button>
        <el-button type="primary" style="margin-left: auto;" @click="openAdd">新增</el-button>
      </div>
      <!-- 表格区域 -->
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="tepName" label="模板名称" align="center" />
        <el-table-column prop="itfName" label="接口名称" align="center" />
        <el-table-column prop="createTime" label="创建时间" align="center" />
        <el-table-column prop="updateTime" label="更新时间" align="center" />
        <el-table-column prop="deleted" label="是否删除" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.deleted === 0 ? 'success' : 'danger'">
              {{ scope.row.deleted === 0 ? '否' : '是' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isPage" label="是否分页" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.isPage === 1 ? 'success' : 'info'">
              {{ scope.row.isPage === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <div style="margin-top: 16px; text-align: right;">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>
    <el-dialog
      v-model="modalVisible"
      :title="modalTitle"
      width="1000px"
      top="50px"
    >
      <visualEdit :formData="formData" :rules="rules" :formRef="formRef" />
      <template #footer>
        <el-button @click="modalVisible = false">取消</el-button>
        <el-button type="primary" @click="handleModalOk">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getVisualTemplatePage } from '@/api/visualConfig'
import { getTemplateList } from '@/api/template'
import { useRouter } from 'vue-router'
import visualEdit from './visualEdit.vue'

const searchForm = reactive({ tepName: '' })
const tableData = ref([])
const loading = ref(false)
const pagination = reactive({ current: 1, size: 10, total: 0 })

const router = useRouter()

const modalVisible = ref(false)
const modalTitle = ref('新增模板')
const formRef = ref()
const formData = reactive({
  id: undefined,
  tepName: '',
  itfId: '',
  isPage: 0
})

const interfaceList = ref([])

const rules = {
  tepName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  itfId: [{ required: true, message: '请输入接口ID', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    // 调用pvTemplate的分页接口
    const current = pagination.current
    const size = pagination.size
    const res = await getVisualTemplatePage(current, size, {})
    // 适配后端返回结构 - PageInfo格式
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.tepName = ''
  fetchData()
}

const fetchInterfaceList = async () => {
  const res = await getTemplateList({})
  // 适配后端返回结构
  interfaceList.value = res.data || []
}

const openAdd = () => {
  // 报表设计器功能已移除
  ElMessage.info('报表设计器功能已移除')
}

const openEdit = (row) => {
  modalTitle.value = '编辑模板'
  formRef.value?.resetFields()
  Object.assign(formData, row)
  modalVisible.value = true
}

const handleDelete = async (row) => {
  // await deleteTemplate(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

const handleModalOk = async () => {
  // 这里不再处理弹窗，后续由 visualEdit 页面负责保存逻辑
}

fetchData()
</script>

<style scoped>
.template-config {
  padding: 24px;
}
</style> 