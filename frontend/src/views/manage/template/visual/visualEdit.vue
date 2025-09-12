<template>
  <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
    <el-form-item label="模板名称" prop="tepName">
      <el-input v-model="formData.tepName" />
    </el-form-item>
    <el-form-item label="接口名称" prop="itfId">
      <el-select v-model="formData.itfId" placeholder="请选择接口" filterable>
        <el-option v-for="item in interfaceList" :key="item.id" :label="item.tepName" :value="item.id" />
      </el-select>
    </el-form-item>
    <el-form-item label="是否分页" prop="isPage">
      <el-switch v-model="formData.isPage" :active-value="1" :inactive-value="0" active-text="是" inactive-text="否" />
    </el-form-item>
    <el-form-item label="字段配置" v-if="fieldConfig.length">
      <el-table :data="fieldConfig" border style="width: 100%; max-height: 55vh; overflow-y: auto; display: block;">
        <el-table-column prop="name" label="字段名"  />
        <el-table-column label="别名">
          <template #default="scope">
            <el-input v-model="scope.row.alias" placeholder="请输入别名" />
          </template>
        </el-table-column>
        <el-table-column label="是否显示" width="100">
          <template #default="scope">
            <el-switch v-model="scope.row.visible" />
          </template>
        </el-table-column>
      </el-table>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { getTemplateList,getCustomInterfaceField } from '@/api/template'

const props = defineProps({
  formData: { type: Object, required: true },
  rules: { type: Object, required: true },
  formRef: { type: Object, required: true }
})

const interfaceList = ref([])
const fieldConfig = ref([])

const fetchInterfaceList = async () => {
  const res = await getTemplateList({})
  interfaceList.value = res.data || []
}

const fetchFieldConfig = async (id) => {
  if (id) {
    const res = await getCustomInterfaceField(id)
    fieldConfig.value = (res.data || []).map(name => ({ name, alias: name, visible: true }))
  } else {
    fieldConfig.value = []
  }
}

// 监听接口ID变化
watch(() => props.formData.itfId, (newId) => {
  fetchFieldConfig(newId)
})

onMounted(() => {
  fetchInterfaceList()
  if (props.formData.itfId) {
    fetchFieldConfig(props.formData.itfId)
  }
})
</script> 