<template>
  <el-card>
    <el-form :model="form" label-width="100px" style="margin-bottom: 16px;">
        <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="报表名称">
            <el-input v-model="form.reportName" placeholder="请输入报表名称" />
          </el-form-item>
        </el-col>
        </el-row>
        <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="数据源">
            <el-select 
            v-model="form.selectedTable" 
             placeholder="请选择数据源"
            style="width: 220px;
             margin-right: 8px;" 
             @change="fetchFields">
              <el-option v-for="table in tableList" 
              :key="table.tableName" 
              :label="table.tableComment" 
              :value="table.tableName"
              />
            </el-select>
            <el-button type="primary" @click="childDialogVisible = true">添加子表</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-table border stripe style="width: 100%" v-if="fieldList.length > 0">
      <el-table-column label="字段" prop="operation" width="60" align="center" />
      <el-table-column 
        v-for="field in fieldList"
        :key="field.columnName"
        :label="field.columnComment"
        :prop="field.columnName"
      />
    </el-table>
    <ChildTableDialog
      v-model:visible="childDialogVisible"
      :table-list="tableList"
      :main-field-list="fieldList"
      @confirm="onChildTableConfirm"
    />
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { getTableList, getTableFields } from '@/api/visualConfig'
import ChildTableDialog from './ChildTableDialog.vue'

const tableList = ref([])
const fieldList = ref([])
const form = ref({ reportName: '', selectedTable: '' })
const filedSetList = ref([])

const childTables = ref([])
const childDialogVisible = ref(false)

async function fetchTableList() {
   const res = await getTableList()
   console.log(res.data)
   tableList.value = res.data
   console.log(tableList.value)
}
async function fetchFields() {
  if (!form.value.selectedTable) return
  const res = await getTableFields(form.value.selectedTable)
  fieldList.value = res.data.columns
  console.log('字段',fieldList.value)
}
function onChildTableConfirm(data) {
  childTables.value.push(data)
}
fetchTableList()
</script> 