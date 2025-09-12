<template>
  <el-dialog
    :model-value="visible"
    :title="title"
    v-bind="dialogProps"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="接口名称" prop="tepName">
        <el-input v-model="formData.tepName" placeholder="请输入接口名称" />
      </el-form-item>
      <el-form-item label="数据源SQL" prop="resourceSql">
        <el-input
          v-model="formData.resourceSql"
          type="textarea"
          :rows="18"
          placeholder="请输入数据源SQL"
        />
      </el-form-item>
      <el-form-item label="接口类型" prop="repType">
        <el-select v-model="formData.repType" placeholder="请选择接口类型">
          <el-option :value="1" label="查询接口" />
          <el-option :value="2" label="新增接口" />
          <el-option :value="3" label="修改接口" />
          <el-option :value="4" label="删除接口" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="formData.repType === 1" label="是否分页" prop="isPage">
        <el-switch
          v-model="formData.isPage"
          :active-value="1"
          :inactive-value="0"
          active-text="是"
          inactive-text="否"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleOk">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue';
const props = defineProps({
  visible: Boolean,
  title: String,
  formData: Object,
  rules: Object,
  width: {
    type: String,
    default: '800px'
  }
});
const emit = defineEmits(['confirm', 'cancel']);
const formRef = ref();

// 透传除已消费props外的所有props给el-dialog
const dialogProps = computed(() => {
  const { visible, title, formData, rules, width, ...rest } = props;
  return { width, ...rest };
});

const handleOk = () => {
  formRef.value.validate().then(() => {
    emit('confirm');
  });
};

const handleClose = () => {
  if (formRef.value) formRef.value.resetFields();
  emit('cancel');
};
</script> 