<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="$emit('update:visible', $event)"
    title="修改密码"
    width="500px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="passwordFormRef"
      :model="passwordForm"
      :rules="passwordRules"
      label-width="120px"
    >
      <el-form-item label="当前密码" prop="oldPassword">
        <el-input
          v-model="passwordForm.oldPassword"
          type="password"
          placeholder="请输入当前密码"
          show-password
          clearable
        />
      </el-form-item>
      
      <el-form-item label="新密码" prop="newPassword">
        <el-input
          v-model="passwordForm.newPassword"
          type="password"
          placeholder="请输入新密码"
          show-password
          clearable
        />
        <div class="password-tips">
          <p>密码要求：</p>
          <ul>
            <li>长度至少8位</li>
            <li>不能与当前密码相同</li>
          </ul>
        </div>
      </el-form-item>
      
      <el-form-item label="确认新密码" prop="confirmPassword">
        <el-input
          v-model="passwordForm.confirmPassword"
          type="password"
          placeholder="请再次输入新密码"
          show-password
          clearable
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          确认修改
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { changePassword } from '@/api/auth';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:visible', 'success']);

const passwordFormRef = ref();
const loading = ref(false);

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 自定义密码验证规则
const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'));
    return;
  }
  
  // 密码长度验证
  if (value.length < 8) {
    callback(new Error('密码长度至少8位'));
    return;
  }
  
  callback();
};

// 确认密码验证
const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'));
    return;
  }
  
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'));
    return;
  }
  
  callback();
};

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
};

// 监听弹窗显示状态，重置表单
watch(() => props.visible, (newVal) => {
  if (newVal) {
    resetForm();
  }
});

// 重置表单
const resetForm = () => {
  if (passwordFormRef.value) {
    passwordFormRef.value.resetFields();
  }
  Object.assign(passwordForm, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  });
};

// 关闭弹窗
const handleClose = () => {
  emit('update:visible', false);
  resetForm();
};

// 提交修改密码
const handleSubmit = async () => {
  if (!passwordFormRef.value) return;
  
  try {
    await passwordFormRef.value.validate();
    
    // 检查新密码是否与旧密码相同
    if (passwordForm.oldPassword === passwordForm.newPassword) {
      ElMessage.error('新密码不能与当前密码相同');
      return;
    }
    
    loading.value = true;
    const res = await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    });
    
    if (res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录');
      emit('success');
      handleClose();
    } else {
      ElMessage.error(res.message || '密码修改失败');
    }
  } catch (error) {
    console.error('密码修改失败:', error);
    ElMessage.error('密码修改失败');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}

.password-tips {
  margin-top: 8px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
}

.password-tips p {
  margin: 0 0 8px 0;
  font-weight: 500;
}

.password-tips ul {
  margin: 0;
  padding-left: 16px;
}

.password-tips li {
  margin-bottom: 4px;
  line-height: 1.4;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-input {
  width: 100%;
}
</style> 