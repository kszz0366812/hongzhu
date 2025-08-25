<template>
  <el-card>
    <div style="margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center;">
      <div>
        <el-input v-model="search.dealer_name" placeholder="经销商名称" style="width: 200px; margin-right: 8px;" />
        <el-button type="primary" @click="fetchList">搜索</el-button>
      </div>
      <div style="display: flex; gap: 8px;">
        <el-button type="primary" @click="openDialog()">新增</el-button>
        <el-button @click="handleImport">导入</el-button>
        <el-button @click="handleDownloadTemplate">下载模板</el-button>
      </div>
    </div>
    <el-table :data="list" border stripe style="width: 100%">
      <el-table-column prop="dealer_code" label="经销商编码" width="120" />
      <el-table-column prop="dealer_name" label="经销商名称" width="160" />
      <el-table-column prop="level" label="等级" />
      <el-table-column prop="contact_person" label="联系人" />
      <el-table-column prop="contact_phone" label="联系电话" />
      <el-table-column prop="customer_manager" label="客户经理" />
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
    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑批发商' : '新增批发商'">
      <el-form :model="dialog.form" label-width="90px">
        <el-form-item label="经销商编码"><el-input v-model="dialog.form.dealer_code" /></el-form-item>
        <el-form-item label="经销商名称"><el-input v-model="dialog.form.dealer_name" /></el-form-item>
        <el-form-item label="等级"><el-input v-model="dialog.form.level" /></el-form-item>
        <el-form-item label="联系人"><el-input v-model="dialog.form.contact_person" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="dialog.form.contact_phone" /></el-form-item>
        <el-form-item label="客户经理"><el-input v-model="dialog.form.customer_manager" /></el-form-item>
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
import { ElMessage } from 'element-plus'
const list = ref([])
const page = ref({ pageNum: 1, pageSize: 10, total: 0 })
const search = ref({ dealer_name: '' })
const dialog = ref({ visible: false, form: {} })
function fetchList() { /* TODO: 调用批发商列表接口 */ }
function openDialog(row) { dialog.value.visible = true; dialog.value.form = row ? { ...row } : {} }
function submit() { /* TODO: 保存批发商 */ dialog.value.visible = false }
function remove(row) { /* TODO: 删除批发商 */ }
function handleImport() { ElMessage.info('导入功能开发中'); }
function handleDownloadTemplate() { ElMessage.info('下载模板功能开发中'); }
</script> 