<template>
  <el-card>
    <div style="margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center;">
      <div>
        <el-input v-model="search.terminal_name" placeholder="终端名称" style="width: 200px; margin-right: 8px;" />
        <el-button type="primary" @click="fetchList">搜索</el-button>
      </div>
      <div style="display: flex; gap: 8px;">
        <el-button type="primary" @click="openDialog()">新增</el-button>
        <el-button @click="handleImport">导入</el-button>
        <el-button @click="handleDownloadTemplate">下载模板</el-button>
      </div>
    </div>
    <el-table :data="list" border stripe style="width: 100%">
      <el-table-column prop="terminal_code" label="终端编码" width="120" />
      <el-table-column prop="terminal_name" label="终端名称" width="160" />
      <el-table-column prop="terminal_type" label="类型" />
      <el-table-column prop="tags" label="标签" />
      <el-table-column prop="customer_manager" label="客户经理" />
      <el-table-column prop="is_scheduled" label="是否排线">
        <template #default="scope">
          <el-tag :type="scope.row.is_scheduled === 1 ? 'success' : 'info'">{{ scope.row.is_scheduled === 1 ? '是' : '否' }}</el-tag>
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
    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑终端' : '新增终端'">
      <el-form :model="dialog.form" label-width="90px">
        <el-form-item label="终端编码"><el-input v-model="dialog.form.terminal_code" /></el-form-item>
        <el-form-item label="终端名称"><el-input v-model="dialog.form.terminal_name" /></el-form-item>
        <el-form-item label="类型"><el-input v-model="dialog.form.terminal_type" /></el-form-item>
        <el-form-item label="标签"><el-input v-model="dialog.form.tags" /></el-form-item>
        <el-form-item label="客户经理"><el-input v-model="dialog.form.customer_manager" /></el-form-item>
        <el-form-item label="是否排线">
          <el-select v-model="dialog.form.is_scheduled">
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
const search = ref({ terminal_name: '' })
const dialog = ref({ visible: false, form: {} })
function fetchList() { /* TODO: 调用终端列表接口 */ }
function openDialog(row) { dialog.value.visible = true; dialog.value.form = row ? { ...row } : {} }
function submit() { /* TODO: 保存终端 */ dialog.value.visible = false }
function remove(row) { /* TODO: 删除终端 */ }
function handleImport() { /* TODO: 实现导入功能 */ }
function handleDownloadTemplate() { /* TODO: 实现下载模板功能 */ }
</script> 