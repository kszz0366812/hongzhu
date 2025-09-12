<template>
  <el-card>
    <div style="margin-bottom: 16px;">
      <el-row :gutter="12" align="middle">
        <el-col :span="6">
          <el-input v-model="search.name" placeholder="字典名称" clearable />
        </el-col>
        <el-col :span="6">
          <el-input v-model="search.parentName" placeholder="父级名称" clearable />
        </el-col>
        <el-col :span="6">
          <el-input v-model="search.type" placeholder="字典类型" clearable />
        </el-col>
        <el-col :span="6" style="display: flex; gap: 8px;">
          <el-button type="primary" @click="fetchList">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="openDialog()">新增字典</el-button>
        </el-col>
      </el-row>
    </div>
    
    <el-table :data="list" border stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="id" align="center" label="ID" />
      <el-table-column prop="code" align="center"  label="字典编码"  />
      <el-table-column prop="name" align="center" label="字典名称" />
      <el-table-column prop="parentName" align="center" label="父级名称" />
      <el-table-column prop="parentId" align="center" label="上级字典ID"  />
      <el-table-column prop="type" align="center" label="字典类型"  />
      <el-table-column prop="sort" align="center" label="排序" />
      <el-table-column prop="status" align="center" label="状态" >
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" align="center" label="创建时间"/>
      <el-table-column align="center" label="操作" width="200" fixed="right">
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
    
    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑字典' : '新增字典'" width="600px">
      <el-form :model="dialog.form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="字典编码" prop="code">
          <el-input v-model="dialog.form.code" placeholder="请输入字典编码" />
        </el-form-item>
        <el-form-item label="字典名称" prop="name">
          <el-input v-model="dialog.form.name" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="上级字典ID" prop="parentId">
          <el-input v-model="dialog.form.parentId" placeholder="请输入上级字典ID（可选）" />
        </el-form-item>
        <el-form-item label="字典类型" prop="type">
          <el-input v-model="dialog.form.type" placeholder="请输入字典类型" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="dialog.form.sort" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dialog.form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="dialog.form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submit" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getDictionaryList, 
  addDictionary, 
  updateDictionary, 
  deleteDictionary
} from '@/api/system.js'

const list = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const formRef = ref()

const page = reactive({
  current: 1,
  size: 10,
  total: 0
})

const search = reactive({
  name: '',
  parentName: '',
  type: ''
})

const dialog = reactive({
  visible: false,
  form: {
    code: '',
    name: '',
    parentId: '',
    type: '',
    sort: 0,
    status: 1,
    remark: ''
  }
})

const rules = {
  code: [
    { required: true, message: '请输入字典编码', trigger: 'blur' },
    { max: 50, message: '字典编码长度不能超过50个字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入字典名称', trigger: 'blur' },
    { max: 100, message: '字典名称长度不能超过100个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请输入字典类型', trigger: 'blur' },
    { max: 50, message: '字典类型长度不能超过50个字符', trigger: 'blur' }
  ],
  sort: [
    { type: 'number', min: 0, max: 999, message: '排序值必须在0-999之间', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取字典列表
const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      current: page.current,
      size: page.size,
      name: search.name,
      parentName: search.parentName,
      type: search.type
    }
    const res = await getDictionaryList(params)
    if (res.code === 200) {
      list.value = res.data.list
      page.total = res.data.total
    } else {
      ElMessage.error(res.message || '获取列表失败')
    }
  } catch (error) {
    console.error('获取字典列表失败:', error)
    ElMessage.error('获取字典列表失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  search.name = ''
  search.parentName = ''
  search.type = ''
  page.current = 1
  fetchList()
}

// 打开对话框
const openDialog = (row) => {
  dialog.visible = true
  if (row) {
    // 只保留需要的字段，过滤掉时间字段和不需要的字段
    const { id, code, name, parentId, type, sort, status, remark } = row
    dialog.form = { 
      id,
      code,
      name,
      parentId: parentId ? String(parentId) : '',
      type,
      sort: sort || 0,
      status: status || 1,
      remark: remark || ''
    }
  } else {
    dialog.form = {
      code: '',
      name: '',
      parentId: '',
      type: '',
      sort: 0,
      status: 1,
      remark: ''
    }
  }
}

// 提交表单
const submit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    // 构建提交数据，只包含必要的字段
    const formData = {
      id: dialog.form.id,
      code: dialog.form.code,
      name: dialog.form.name,
      parentId: dialog.form.parentId ? Number(dialog.form.parentId) : null,
      type: dialog.form.type,
      sort: dialog.form.sort || 0,
      status: dialog.form.status || 1,
      remark: dialog.form.remark || ''
    }
    
    const api = dialog.form.id ? updateDictionary : addDictionary
    const res = await api(formData)
    
    if (res.code === 200) {
      ElMessage.success(dialog.form.id ? '修改成功' : '新增成功')
      dialog.visible = false
      fetchList()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  } finally {
    submitLoading.value = false
  }
}

// 删除字典
const remove = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个字典吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteDictionary(row.id)
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

onMounted(() => {
  fetchList()
})
</script> 