<template>
  <el-card>
    <div class="action-bar">
      <div class="search-bar">
        <el-input v-model="search.keyword" placeholder="搜索" style="width: 200px; margin-right: 8px;" />
        <el-button type="primary" @click="fetchList">搜索</el-button>
      </div>
      <div class="button-bar">
        <el-button type="primary" @click="handleAdd">新增</el-button>
        <el-button @click="handleImport">导入</el-button>
        <el-button @click="handleDownloadTemplate">下载模板</el-button>
      </div>
    </div>
    <el-table :data="list" border stripe style="width: 100%">
      <el-table-column prop="executor_id" label="执行人ID" width="120" />
      <el-table-column prop="start_time" label="开始时间" width="160" />
      <el-table-column prop="end_time" label="结束时间" width="160" />
      <el-table-column prop="task_name" label="任务名称" />
      <el-table-column prop="target_amount" label="目标金额" />
      <el-table-column prop="achieved_amount" label="已达成金额" />
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
    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑任务' : '新增任务'">
      <el-form :model="dialog.form" label-width="90px">
        <el-form-item label="执行人ID"><el-input v-model="dialog.form.executorId" /></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="dialog.form.startTime" type="datetime" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="dialog.form.endTime" type="datetime" /></el-form-item>
        <el-form-item label="任务名称"><el-input v-model="dialog.form.taskName" /></el-form-item>
        <el-form-item label="目标金额"><el-input v-model="dialog.form.targetAmount" /></el-form-item>
        <el-form-item label="已达成金额"><el-input v-model="dialog.form.achievedAmount" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>
<script setup>
import { ref } from 'vue'
const list = ref([])
const page = ref({ pageNum: 1, pageSize: 10, total: 0 })
const search = ref({ keyword: '' })
const dialog = ref({ visible: false, form: {} })
const addDialogVisible = ref(false)
const addForm = ref({ executorId: '', startTime: '', endTime: '', taskName: '', targetAmount: '', achievedAmount: '' })
function fetchList() { /* TODO: 调用任务列表接口 */ }
function openDialog(row) { dialog.value.visible = true; dialog.value.form = row ? { ...row } : {} }
function submit() { /* TODO: 保存任务 */ dialog.value.visible = false }
function remove(row) { /* TODO: 删除任务 */ }
function handleAdd() {
  addForm.value = { executorId: '', startTime: '', endTime: '', taskName: '', targetAmount: '', achievedAmount: '' }
  dialog.value.visible = true
  dialog.value.form = { ...addForm.value }
}
function handleImport() {
  // 实现导入功能，这里仅弹窗或打印
  window.$message ? window.$message.info('导入功能开发中') : console.log('导入功能开发中');
}
function handleDownloadTemplate() {
  // 实现下载模板功能，这里仅弹窗或打印
  window.$message ? window.$message.info('下载模板功能开发中') : console.log('下载模板功能开发中');
}
</script>

<style scoped>
.action-bar { display: flex; justify-content: space-between; align-items: center; gap: 8px; margin-bottom: 16px; }
.search-bar { display: flex; align-items: center; }
.button-bar { display: flex; gap: 8px; }
</style> 