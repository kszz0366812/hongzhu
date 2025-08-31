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
      <el-table-column prop="visitor_id" label="拜访人ID" width="120" />
      <el-table-column prop="visit_time" label="拜访时间" width="160" />
      <el-table-column prop="terminal_id" label="终端ID" />
      <el-table-column prop="is_deal" label="是否成交">
        <template #default="scope">
          <el-tag :type="scope.row.is_deal === 1 ? 'success' : 'info'">{{ scope.row.is_deal === 1 ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
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
    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑拜访' : '新增拜访'">
      <el-form :model="dialog.form" label-width="90px">
        <el-form-item label="拜访人ID"><el-input v-model="dialog.form.visitorId" /></el-form-item>
        <el-form-item label="拜访时间"><el-date-picker v-model="dialog.form.visitTime" type="datetime" /></el-form-item>
        <el-form-item label="终端ID"><el-input v-model="dialog.form.terminalId" /></el-form-item>
        <el-form-item label="是否成交">
          <el-select v-model="dialog.form.isDeal">
            <el-option :value="1" label="是" />
            <el-option :value="0" label="否" />
          </el-select>
        </el-form-item>
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
const addForm = ref({ visitorId: '', visitTime: '', terminalId: '', isDeal: 0 })
function fetchList() { /* TODO: 调用拜访列表接口 */ }
function openDialog(row) { dialog.value.visible = true; dialog.value.form = row ? { ...row } : {} }
function submit() { /* TODO: 保存拜访 */ dialog.value.visible = false }
function remove(row) { /* TODO: 删除拜访 */ }
function handleAdd() {
  addForm.value = { visitorId: '', visitTime: '', terminalId: '', isDeal: 0 }
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