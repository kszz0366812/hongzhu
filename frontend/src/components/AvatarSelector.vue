<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="$emit('update:visible', $event)"
    title="选择头像"
    width="500px"
    :close-on-click-modal="true"
    class="avatar-selector-dialog"
  >
    <div class="avatar-selector">
      <div class="avatar-grid">
        <!-- 第一格：自定义上传 -->
        <div 
          class="avatar-item custom-upload" 
          :class="{ active: selectedAvatar && !defaultAvatars.includes(selectedAvatar) }"
          @click="triggerFileUpload"
        >
          <div class="upload-icon">
            <el-icon><Plus /></el-icon>
          </div>
          <span class="upload-text">自定义</span>
        </div>
        
        <!-- 默认头像列表 -->
        <div 
          v-for="(avatar, index) in defaultAvatars" 
          :key="index"
          class="avatar-item"
          :class="{ active: selectedAvatar === avatar }"
          @click="selectDefaultAvatar(avatar)"
        >
          <img :src="avatar" :alt="`头像${index + 1}`" class="avatar-image" />
        </div>
      </div>
      
      <!-- 隐藏的文件上传输入框 -->
      <input
        ref="fileInput"
        type="file"
        accept="image/*"
        style="display: none"
        @change="handleFileChange"
      />
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  visible: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:modelValue', 'update:visible', 'upload', 'confirm']);

const fileInput = ref(null);
const defaultAvatars = ref([]);
const selectedAvatar = ref('');

// 获取默认头像列表
const getDefaultAvatars = () => {
  // 使用正确的静态资源路径
  const avatarPaths = [
    '/src/static/images/avatar/dw1.png',
    '/src/static/images/avatar/dw2.png',
    '/src/static/images/avatar/dw3.png',
    '/src/static/images/avatar/dw4.png',
    '/src/static/images/avatar/dw5.png',
    '/src/static/images/avatar/dw6.png',
    '/src/static/images/avatar/dw7.png',
    '/src/static/images/avatar/dw8.png',
    '/src/static/images/avatar/dw9.png',
    '/src/static/images/avatar/man1.png',
    '/src/static/images/avatar/man2.png',
    '/src/static/images/avatar/man3.png',
    '/src/static/images/avatar/man4.png',
    '/src/static/images/avatar/man5.png',
    '/src/static/images/avatar/man6.png',
    '/src/static/images/avatar/man7.png',
    '/src/static/images/avatar/man8.png',
    '/src/static/images/avatar/man9.png',
    '/src/static/images/avatar/woman1.png',
    '/src/static/images/avatar/woman2.png',
    '/src/static/images/avatar/woman3.png',
    '/src/static/images/avatar/woman4.png',
    '/src/static/images/avatar/woman5.png',
    '/src/static/images/avatar/women6.png',
    '/src/static/images/avatar/women7.png',
    '/src/static/images/avatar/woman8.png',
    '/src/static/images/avatar/women9.png'
  ];
  
  defaultAvatars.value = avatarPaths;
};

// 选择默认头像
const selectDefaultAvatar = (avatar) => {
  console.log('选择头像:', avatar);
  // 如果点击的是当前已选中的头像，则取消选中
  if (selectedAvatar.value === avatar) {
    selectedAvatar.value = '';
  } else {
    selectedAvatar.value = avatar;
  }
  console.log('当前选中头像:', selectedAvatar.value);
};

// 触发文件选择
const triggerFileUpload = () => {
  console.log('触发文件上传');
  fileInput.value?.click();
};

// 处理文件选择
const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (!file) return;
  
  // 验证文件
  if (!validateFile(file)) {
    event.target.value = '';
    return;
  }
  
  // 读取文件并转换为base64
  const reader = new FileReader();
  reader.onload = (e) => {
    const base64 = e.target.result;
    selectedAvatar.value = base64;
    emit('upload', { file, base64 });
  };
  reader.readAsDataURL(file);
  
  // 清空input值，允许重复选择同一文件
  event.target.value = '';
};

// 验证文件
const validateFile = (file) => {
  const isJPG = file.type === 'image/jpeg';
  const isPNG = file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG && !isPNG) {
    ElMessage.error('头像只能是 JPG 或 PNG 格式!');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!');
    return false;
  }
  return true;
};

// 确认选择
const handleConfirm = () => {
  if (selectedAvatar.value) {
    // 先更新父组件的值
    emit('update:modelValue', selectedAvatar.value);
    // 然后触发确认事件
    emit('confirm', selectedAvatar.value);
    // 最后关闭弹窗
    emit('update:visible', false);
  } else {
    ElMessage.warning('请选择一个头像');
  }
};

// 取消选择
const handleCancel = () => {
  selectedAvatar.value = props.modelValue; // 恢复到原始值
  emit('update:visible', false);
};

// 监听visible变化，初始化选中值
watch(() => props.visible, (newVal) => {
  console.log('visible 变化:', newVal);
  if (newVal) {
    selectedAvatar.value = props.modelValue || '';
    console.log('初始化选中头像:', selectedAvatar.value);
  } else {
    // 弹窗关闭时重置选中状态
    selectedAvatar.value = '';
  }
});

onMounted(() => {
  console.log('AvatarSelector 组件已挂载');
  getDefaultAvatars();
  console.log('默认头像列表:', defaultAvatars.value);
  
  // 测试：强制显示弹窗
  setTimeout(() => {
    console.log('测试：尝试显示弹窗');
    console.log('当前 visible 值:', props.visible);
    console.log('当前 defaultAvatars 长度:', defaultAvatars.value.length);
  }, 1000);
});
</script>

<style scoped>
.avatar-selector-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.avatar-selector {
  margin: 0;
}

.avatar-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
  max-width: 100%;
}

.avatar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 6px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #ffffff;
  min-height: 80px;
  aspect-ratio: 1;
  position: relative;
}

.avatar-item:hover {
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.avatar-item.active {
  border-color: #409eff !important;
  border-width: 3px !important;
  background: #f0f9ff !important;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.3), 0 4px 12px rgba(64, 158, 255, 0.2) !important;
  transform: scale(1.02) !important;
}

/* 选中状态下的悬停效果 */
.avatar-item.active:hover {
  border-color: #409eff !important;
  border-width: 3px !important;
  background: #f0f9ff !important;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.3), 0 4px 12px rgba(64, 158, 255, 0.2) !important;
  transform: scale(1.02) !important;
}

.custom-upload {
  border-style: dashed;
  border-color: #c0c4cc;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.custom-upload:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.custom-upload.active {
  border-color: #409eff !important;
  border-width: 3px !important;
  background: #f0f9ff !important;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.3), 0 4px 12px rgba(64, 158, 255, 0.2) !important;
  transform: scale(1.02) !important;
}

/* 自定义上传选中状态下的悬停效果 */
.custom-upload.active:hover {
  border-color: #409eff !important;
  border-width: 3px !important;
  background: #f0f9ff !important;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.3), 0 4px 12px rgba(64, 158, 255, 0.2) !important;
  transform: scale(1.02) !important;
}

.upload-icon {
  font-size: 24px;
  color: #909399;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
}

.upload-text {
  font-size: 12px;
  color: #606266;
  font-weight: 500;
  text-align: center;
  line-height: 1;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 0;
  transition: all 0.3s ease;
  display: block;
}

.avatar-item:not(.custom-upload):hover .avatar-image {
  transform: scale(1.05);
}

.avatar-item.active .avatar-image {
  border: 2px solid #409eff !important;
  transform: scale(1.05) !important;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.4) !important;
}



.dialog-footer {
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 600px) {
  .avatar-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: 6px;
  }
  
  .avatar-item {
    padding: 4px;
    min-height: 70px;
  }
  
  .upload-icon {
    font-size: 20px;
  }
  
  .upload-text {
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .avatar-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 4px;
  }
}
</style> 