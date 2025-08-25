<template>
  <el-card>
    <div class="action-bar">
      <div class="search-bar">
        <el-input v-model="search.name" placeholder="员工姓名" style="width: 200px; margin-right: 8px;" />
        <el-button type="primary" @click="fetchList">搜索</el-button>
      </div>
      <div class="button-bar">
        <el-button type="primary" @click="handleAdd">新增</el-button>
        <el-button @click="handleImport">导入</el-button>
        <el-button @click="handleDownloadTemplate">下载模板</el-button>
      </div>
    </div>
    <el-table :data="list" border stripe style="width: 100%">
      <el-table-column prop="employee_code" label="工号" width="120" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === 0 ? 'success' : 'info'">{{ scope.row.status === 0 ? '在职' : '离职' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="region_level1" label="大区" />
      <el-table-column prop="region_level2" label="地市" />
      <el-table-column prop="region_level3" label="区域" />
      <el-table-column prop="position" label="岗位" />
      <el-table-column prop="rank" label="职级" />
      <el-table-column prop="channel" label="渠道" />
      <el-table-column prop="join_date" label="入司日期" />
      <el-table-column label="操作" width="180">
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
    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑员工' : '新增员工'">
      <el-form :model="dialog.form" label-width="90px">
        <el-form-item label="工号"><el-input v-model="dialog.form.employeeCode" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="dialog.form.name" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="dialog.form.status">
            <el-option :value="0" label="在职" />
            <el-option :value="1" label="离职" />
          </el-select>
        </el-form-item>
        <el-form-item label="大区"><el-input v-model="dialog.form.regionLevel1" /></el-form-item>
        <el-form-item label="地市"><el-input v-model="dialog.form.regionLevel2" /></el-form-item>
        <el-form-item label="区域"><el-input v-model="dialog.form.regionLevel3" /></el-form-item>
        <el-form-item label="负责区域"><el-input v-model="dialog.form.responsibleRegions" /></el-form-item>
        <el-form-item label="直属领导ID"><el-input v-model="dialog.form.directLeaderId" type="number" /></el-form-item>
        <el-form-item label="岗位"><el-input v-model="dialog.form.position" /></el-form-item>
        <el-form-item label="职级"><el-input v-model="dialog.form.level" /></el-form-item>
        <el-form-item label="渠道"><el-input v-model="dialog.form.channel" /></el-form-item>
        <el-form-item label="入司日期"><el-date-picker v-model="dialog.form.joinDate" type="date" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="addDialogVisible" title="新增员工" width="400px">
      <el-form :model="addForm" label-width="80px">
        <el-form-item label="工号">
          <el-input v-model="addForm.employeeCode" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="addForm.name" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="addForm.status">
            <el-option :value="0" label="在职" />
            <el-option :value="1" label="离职" />
          </el-select>
        </el-form-item>
        <el-form-item label="大区">
          <el-input v-model="addForm.regionLevel1" />
        </el-form-item>
        <el-form-item label="地市">
          <el-input v-model="addForm.regionLevel2" />
        </el-form-item>
        <el-form-item label="区域">
          <el-input v-model="addForm.regionLevel3" />
        </el-form-item>
        <el-form-item label="负责区域">
          <el-input v-model="addForm.responsibleRegions" />
        </el-form-item>
        <el-form-item label="直属领导ID">
          <el-input v-model="addForm.directLeaderId" type="number" />
        </el-form-item>
        <el-form-item label="岗位">
          <el-input v-model="addForm.position" />
        </el-form-item>
        <el-form-item label="职级">
          <el-input v-model="addForm.level" />
        </el-form-item>
        <el-form-item label="渠道">
          <el-input v-model="addForm.channel" />
        </el-form-item>
        <el-form-item label="入司日期">
          <el-date-picker v-model="addForm.joinDate" type="date" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>
<script setup>
import { ref } from 'vue'
const list = ref([])
const page = ref({ pageNum: 1, pageSize: 10, total: 0 })
const search = ref({ name: '' })
const dialog = ref({ visible: false, form: {} })
const addDialogVisible = ref(false)
const addForm = ref({ name: '', phone: '' })
function fetchList() { /* TODO: 调用员工列表接口 */ }
function openDialog(row) { dialog.value.visible = true; dialog.value.form = row ? { ...row } : {} }
function submit() { /* TODO: 保存员工 */ dialog.value.visible = false }
function remove(row) { /* TODO: 删除员工 */ }
function handleImport() {
  // TODO: 实现导入逻辑
}
function handleDownloadTemplate() {
  // TODO: 实现下载模板逻辑
}
function handleAdd() {
  addForm.value = { name: '', phone: '' }
  addDialogVisible.value = true
}
function submitAdd() {
  // TODO: 调用新增员工接口
  addDialogVisible.value = false
  fetchList()
}
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