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
      <el-table-column prop="product_code" label="商品编码" width="120" align="center" />
      <el-table-column prop="product_name" label="商品名称" width="160" align="center" />
      <el-table-column prop="specification" label="规格" align="center" />
      <el-table-column prop="unit_price" label="单价" align="center" />
      <el-table-column prop="case_price" label="件价" align="center" />
      <el-table-column prop="series" label="所属系列" align="center" />
      <el-table-column label="操作" width="180" align="center">
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
    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑商品' : '新增商品'">
      <el-form :model="dialog.form" label-width="90px">
        <el-form-item label="商品编码"><el-input v-model="dialog.form.product_code" /></el-form-item>
        <el-form-item label="商品名称"><el-input v-model="dialog.form.product_name" /></el-form-item>
        <el-form-item label="规格"><el-input v-model="dialog.form.specification" /></el-form-item>
        <el-form-item label="单价"><el-input v-model="dialog.form.unit_price" /></el-form-item>
        <el-form-item label="件价"><el-input v-model="dialog.form.case_price" /></el-form-item>
        <el-form-item label="所属系列"><el-input v-model="dialog.form.series" /></el-form-item>
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
import ExcelTemplateUtil from '@/utils/excelTemplate'
const list = ref([])
const page = ref({ pageNum: 1, pageSize: 10, total: 0 })
const search = ref({ keyword: '' })
const dialog = ref({ visible: false, form: {} })
function fetchList() { /* TODO: 调用商品列表接口 */ }
function openDialog(row) { dialog.value.visible = true; dialog.value.form = row ? { ...row } : {} }
function submit() { /* TODO: 保存商品 */ dialog.value.visible = false }
function remove(row) { /* TODO: 删除商品 */ }
function handleAdd() { dialog.value.visible = true; dialog.value.form = {}; }
function handleImport() { ElMessage.info('导入功能开发中'); }
function handleDownloadTemplate() { 
  ExcelTemplateUtil.downloadProductTemplate().then(result => {
    if (result.success) {
      ElMessage.success('模版下载成功')
    } else {
      ElMessage.error(result.message)
    }
  }).catch(error => {
    console.error('下载模版失败:', error)
    ElMessage.error('下载模版失败')
  })
}
</script>

<style scoped>
.action-bar { display: flex; justify-content: space-between; align-items: center; gap: 8px; margin-bottom: 16px; }
.search-bar { display: flex; align-items: center; }
.button-bar { display: flex; gap: 8px; }
</style> 