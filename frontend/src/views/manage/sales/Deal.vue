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
      <el-table-column prop="deal_time" label="成交时间" width="160" />
      <el-table-column prop="visit_id" label="拜访ID" />
      <el-table-column prop="deal_employee_id" label="成交人ID" />
      <el-table-column prop="product_id" label="商品ID" />
      <el-table-column prop="quantity" label="数量" />
      <el-table-column prop="unit" label="单位" />
      <el-table-column prop="unit_price" label="单价" />
      <el-table-column prop="total_price" label="总价" />
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
    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑成交' : '新增成交'">
      <el-form :model="dialog.form" label-width="90px">
        <el-form-item label="成交时间"><el-date-picker v-model="dialog.form.deal_time" type="datetime" /></el-form-item>
        <el-form-item label="拜访ID"><el-input v-model="dialog.form.visit_id" /></el-form-item>
        <el-form-item label="成交人ID"><el-input v-model="dialog.form.deal_employee_id" /></el-form-item>
        <el-form-item label="商品ID"><el-input v-model="dialog.form.product_id" /></el-form-item>
        <el-form-item label="数量"><el-input v-model="dialog.form.quantity" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="dialog.form.unit" /></el-form-item>
        <el-form-item label="单价"><el-input v-model="dialog.form.unit_price" /></el-form-item>
        <el-form-item label="总价"><el-input v-model="dialog.form.total_price" /></el-form-item>
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
const list = ref([
  { id: 1, deal_time: '2024-07-29 10:00:00', visit_id: 101, deal_employee_id: 1, product_id: 1001, quantity: 10, unit: '箱', unit_price: 50, total_price: 500 },
  { id: 2, deal_time: '2024-07-29 10:05:00', visit_id: 102, deal_employee_id: 2, product_id: 1002, quantity: 5, unit: '箱', unit_price: 120, total_price: 600 },
  { id: 3, deal_time: '2024-07-29 10:10:00', visit_id: 103, deal_employee_id: 1, product_id: 1003, quantity: 8, unit: '件', unit_price: 30, total_price: 240 },
  { id: 4, deal_time: '2024-07-29 10:15:00', visit_id: 104, deal_employee_id: 3, product_id: 1004, quantity: 12, unit: '箱', unit_price: 80, total_price: 960 },
  { id: 5, deal_time: '2024-07-29 10:20:00', visit_id: 105, deal_employee_id: 2, product_id: 1005, quantity: 20, unit: '瓶', unit_price: 5, total_price: 100 },
  { id: 6, deal_time: '2024-07-29 10:25:00', visit_id: 106, deal_employee_id: 1, product_id: 1001, quantity: 15, unit: '箱', unit_price: 50, total_price: 750 },
  { id: 7, deal_time: '2024-07-29 10:30:00', visit_id: 107, deal_employee_id: 4, product_id: 1006, quantity: 2, unit: '件', unit_price: 200, total_price: 400 },
  { id: 8, deal_time: '2024-07-29 10:35:00', visit_id: 108, deal_employee_id: 3, product_id: 1007, quantity: 30, unit: '袋', unit_price: 15, total_price: 450 },
  { id: 9, deal_time: '2024-07-29 10:40:00', visit_id: 109, deal_employee_id: 2, product_id: 1002, quantity: 7, unit: '箱', unit_price: 120, total_price: 840 },
  { id: 10, deal_time: '2024-07-29 10:45:00', visit_id: 110, deal_employee_id: 1, product_id: 1008, quantity: 25, unit: '瓶', unit_price: 8, total_price: 200 }
])
const page = ref({ pageNum: 1, pageSize: 10, total: 10 })
const search = ref({ keyword: '' })
const dialog = ref({ visible: false, form: {} })
function fetchList() { /* TODO: 调用成交列表接口 */ }
function openDialog(row) { dialog.value.visible = true; dialog.value.form = row ? { ...row } : {} }
function submit() { /* TODO: 保存成交 */ dialog.value.visible = false }
function remove(row) { /* TODO: 删除成交 */ }
function handleAdd() { dialog.value.visible = true; dialog.value.form = {}; }
function handleImport() { ElMessage.info('导入功能开发中'); }
function handleDownloadTemplate() { ElMessage.info('下载模板功能开发中'); }
</script>

<style scoped>
.action-bar { display: flex; justify-content: space-between; align-items: center; gap: 8px; margin-bottom: 16px; }
.search-bar { display: flex; align-items: center; }
.button-bar { display: flex; gap: 8px; }
</style> 