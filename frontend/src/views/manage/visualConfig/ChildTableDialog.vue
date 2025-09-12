<template>
  <el-dialog
    :model-value="visible"
    title="添加子表"
    width="600px"
    @update:model-value="val => $emit('update:visible', val)"
  >
    <el-form label-width="90px">
      <el-form-item label="子表">
        <el-select
          v-model="dialogData.selectedTable"
          filterable
          placeholder="搜索子表"
          style="width: 100%"
          @change="onChildTableChange"
        >
          <el-option
            v-for="table in tableList"
            :key="table.tableName"
            :label="table.tableComment + '（' + table.tableName + '）'"
            :value="table.tableName"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="关联条件">
        <div v-for="(rel, idx) in dialogData.relations" :key="idx" style="margin-bottom: 8px; display: flex; align-items: center; gap: 8px;">
          <el-select v-model="rel.mainField" placeholder="主表字段" style="width: 150px">
            <el-option v-for="f in (mainFieldList || [])" :key="f.columnName" :label="f.columnComment || f.columnName" :value="f.columnName" />
          </el-select>
          <el-select v-model="rel.relation" style="width: 80px">
            <el-option label="等于" value="=" />
            <el-option label="大于" value=">" />
            <el-option label="小于" value="<" />
            <el-option label="包含" value="like" />
          </el-select>
          <el-select v-model="rel.childField" placeholder="子表字段" style="width: 150px">
            <el-option v-for="f in childFieldList" :key="f.columnName" :label="f.columnComment || f.columnName" :value="f.columnName" />
          </el-select>
          <el-button icon="el-icon-delete" @click="removeRelation(idx)" v-if="dialogData.relations.length > 1" circle />
        </div>
        <el-button type="primary" @click="addRelation" size="small">添加条件</el-button>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="onConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getTableFields } from '@/api/visualConfig'

const props = defineProps({
  visible: Boolean,
  tableList: Array,
  mainFieldList: Array,
})
const emit = defineEmits(['update:visible', 'confirm'])

const dialogData = ref({
  selectedTable: '',
  relations: [
    { mainField: '', relation: '=', childField: '' }
  ]
})
const childFieldList = ref([])

watch(() => props.visible, (val) => {
  if (val) {
    dialogData.value = {
      selectedTable: '',
      relations: [ { mainField: '', relation: '=', childField: '' } ]
    }
    childFieldList.value = []
  }
})

async function onChildTableChange() {
  if (!dialogData.value.selectedTable) {
    childFieldList.value = []
    return
  }
  const res = await getTableFields(dialogData.value.selectedTable)
  childFieldList.value = res.data.columns
  // 清空所有子表字段
  dialogData.value.relations.forEach(r => r.childField = '')
}
function addRelation() {
  dialogData.value.relations.push({ mainField: '', relation: '=', childField: '' })
}
function removeRelation(idx) {
  if (dialogData.value.relations.length > 1) {
    dialogData.value.relations.splice(idx, 1)
  }
}
function onConfirm() {
  // 校验每组都填写完整
  if (!dialogData.value.selectedTable || dialogData.value.relations.some(r => !r.mainField || !r.childField || !r.relation)) {
    return
  }
  emit('confirm', { selectedTable: dialogData.value.selectedTable, relations: [...dialogData.value.relations] })
  emit('update:visible', false)
}
</script> 