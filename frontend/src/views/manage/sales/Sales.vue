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
      <el-table-column prop="sales_order_no" label="销售单号" width="120" align="center" />
      <el-table-column prop="sales_date" label="销售日期" width="160" align="center" />
      <el-table-column prop="customer_name" label="客户名称" align="center" />
      <el-table-column prop="customer_level" label="客户等级" align="center" />
      <el-table-column prop="distributor" label="配送商" align="center" />
      <el-table-column prop="customer_manager" label="客户经理" align="center" />
      <el-table-column prop="salesperson" label="业务员" align="center" />
      <el-table-column prop="product_id" label="商品ID" align="center" />
      <el-table-column prop="quantity" label="销售数量" align="center" />
      <el-table-column prop="is_gift" label="是否赠品" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.is_gift === 1 ? 'success' : 'info'">{{ scope.row.is_gift === 1 ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="unit" label="销售单位" align="center" />
      <el-table-column prop="unit_price" label="销售单价" align="center" />
      <el-table-column prop="total_price" label="总价" align="center" />
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
    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑销售' : '新增销售'">
      <el-form :model="dialog.form" label-width="90px">
        <el-form-item label="销售单号"><el-input v-model="dialog.form.sales_order_no" /></el-form-item>
        <el-form-item label="销售日期"><el-date-picker v-model="dialog.form.sales_date" type="datetime" /></el-form-item>
        <el-form-item label="客户名称"><el-input v-model="dialog.form.customer_name" /></el-form-item>
        <el-form-item label="客户等级"><el-input v-model="dialog.form.customer_level" /></el-form-item>
        <el-form-item label="配送商"><el-input v-model="dialog.form.distributor" /></el-form-item>
        <el-form-item label="客户经理"><el-input v-model="dialog.form.customer_manager" /></el-form-item>
        <el-form-item label="业务员"><el-input v-model="dialog.form.salesperson" /></el-form-item>
        <el-form-item label="商品ID"><el-input v-model="dialog.form.product_id" /></el-form-item>
        <el-form-item label="销售数量"><el-input v-model="dialog.form.quantity" /></el-form-item>
        <el-form-item label="是否赠品">
          <el-select v-model="dialog.form.is_gift">
            <el-option :value="1" label="是" />
            <el-option :value="0" label="否" />
          </el-select>
        </el-form-item>
        <el-form-item label="销售单位"><el-input v-model="dialog.form.unit" /></el-form-item>
        <el-form-item label="销售单价"><el-input v-model="dialog.form.unit_price" /></el-form-item>
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
import ExcelTemplateUtil from '@/utils/excelTemplate'
const list = ref([])
const page = ref({ pageNum: 1, pageSize: 10, total: 0 })
const search = ref({ keyword: '' })
const dialog = ref({ visible: false, form: {} })
function fetchList() { /* TODO: 调用销售列表接口 */ }
function openDialog(row) { dialog.value.visible = true; dialog.value.form = row ? { ...row } : {} }
function submit() { /* TODO: 保存销售 */ dialog.value.visible = false }
function remove(row) { /* TODO: 删除销售 */ }
function handleAdd() { dialog.value.visible = true; dialog.value.form = {}; }
function handleImport() { ElMessage.info('导入功能开发中'); }
function handleDownloadTemplate() { 
  ExcelTemplateUtil.downloadSalesTemplate().then(result => {
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