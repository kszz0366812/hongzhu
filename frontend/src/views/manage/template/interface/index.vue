<template>
  <div class="interface-config">
    <el-card>
      <!-- 搜索区域 -->
      <div class="search-area-flex">
        <el-form :inline="true" :model="searchForm" class="search-form-flex">
          <el-form-item label="接口名称">
            <el-input v-model="searchForm.tepName" placeholder="请输入接口名称" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button style="margin-left: 8px" @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" class="add-btn" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增
        </el-button>
      </div>

      <!-- 表格区域 -->
      <el-table
        :data="tableData"
        :loading="loading"
        v-loading="loading"
        border
      >
        <el-table-column prop="tepName" label="接口名称" align="center" />
        <el-table-column prop="repType" label="接口类型" align="center">
          <template #default="{ row }">
            {{ row.repType === 1 ? '查询接口' : row.repType === 2 ? '新增接口' : row.repType === 3 ? '修改接口' : row.repType === 4 ? '删除接口' : '' }}
          </template>
        </el-table-column>
        <el-table-column v-if="tableData && tableData.length > 0 && tableData.some(row => row.repType === 1)" prop="isPage" label="是否分页" align="center">
          <template #default="{ row }">
            <span v-if="row.repType === 1">{{ row.isPage === 1 ? '是' : '否' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" align="center">
          <template #default="{ row }">
            {{ row.createTime ? dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss') : '' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-space>
              <el-button link type="primary" @click="handleTest(row)">测试</el-button>
              <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
              <el-divider direction="vertical" />
              <el-popconfirm
                title="确定要删除这条记录吗？"
                @confirm="handleDelete(row)"
              >
                <template #reference>
                  <el-button link type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </el-space>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-area">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <interfaceEdit
      :visible="modalVisible"
      :title="modalTitle"
      :formData="formData"
      :rules="rules"
      width="1000px"
      @confirm="handleModalOk"
      @cancel="handleModalCancel"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import {
  Search,
  Refresh,
  Plus
} from '@element-plus/icons-vue';
import { 
  getTemplateListPage,
  createTemplate, 
  updateTemplate, 
  deleteTemplate,
  callCustomInterface,
  getCustomInterfaceParams
} from '@/api/template';
import interfaceEdit from './interfaceEdit.vue';
import dayjs from 'dayjs';

// 搜索表单
const searchForm = reactive({
  tepName: ''
});

// 表格数据
const tableData = ref([]);
const loading = ref(false);
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
});

// 弹窗相关
const modalVisible = ref(false);
const modalTitle = ref('新增接口');
const formRef = ref();
const formData = reactive({
  id: undefined,
  tepName: '',
  resourceSql: '',
  repType: undefined,
  isPage: 0
});

// 表单验证规则
const rules = {
  tepName: [{ required: true, message: '请输入接口名称', trigger: 'blur' }],
  resourceSql: [{ required: true, message: '请输入数据源SQL', trigger: 'blur' }],
  repType: [{ required: true, message: '请选择接口类型', trigger: 'change' }]
};

// 获取表格数据
const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getTemplateListPage(pagination.current, pagination.size, {tepName: searchForm.tepName});
    tableData.value = res.data.list;
    pagination.total = res.data.total;
  } catch (error) {
    ElMessage.error('获取数据失败');
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  pagination.current = 1;
  fetchData();
};

// 重置
const handleReset = () => {
  searchForm.tepName = '';
  handleSearch();
};

// 分页大小变化
const handleSizeChange = (val) => {
  pagination.size = val;
  fetchData();
};

// 页码变化
const handleCurrentChange = (val) => {
  pagination.current = val;
  fetchData();
};

// 新增
const handleAdd = () => {
  modalTitle.value = '新增接口';
  formData.id = undefined;
  formData.tepName = '';
  formData.resourceSql = '';
  formData.repType = undefined;
  formData.isPage = 0;
  modalVisible.value = true;
};

// 编辑
const handleEdit = (record) => {
  modalTitle.value = '编辑接口';
  Object.assign(formData, record);
  modalVisible.value = true;
};
//测试
const handleTest = (row) => {
  callCustomInterface(row.id,{}).then(res => {
    if(res.code===200){
      ElMessage.success('接口正常')
    }else{
      ElMessage.error(res.data)
    }
  });
};
// 删除
const handleDelete = async (record) => {
  try {
    await deleteTemplate(record.id);
    ElMessage.success('删除成功');
    fetchData();
  } catch (error) {
    ElMessage.error('删除失败');
  }
};

// 弹窗确认
const handleModalOk = async () => {
  try {
    if (formData.id) {
      await updateTemplate(formData);
      ElMessage.success('更新成功');
    } else {
      await createTemplate(formData);
      ElMessage.success('创建成功');
    }
    modalVisible.value = false;
    fetchData();
  } catch (error) {
    ElMessage.error(formData.id ? '更新失败' : '创建失败');
  }
};

// 弹窗取消
const handleModalCancel = () => {
  modalVisible.value = false;
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.interface-config {
  padding: 24px;
}

.search-area-flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.search-form-flex {
  display: flex;
  align-items: center;
  margin-bottom: 0;
}

.add-btn {
  margin-left: 16px;
}

.pagination-area {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style> 