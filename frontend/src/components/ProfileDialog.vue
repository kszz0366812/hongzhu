<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="$emit('update:visible', $event)"
    title="个人信息"
    width="900px"
    :close-on-click-modal="false"
    @close="handleClose"
    class="profile-dialog"
  >

    <el-form
      ref="profileFormRef"
      :model="profileForm"
      :rules="profileRules"
      label-width="100px"
      class="profile-form"
    >
      <!-- 头像和基本信息区域 -->
      <div class="profile-header">
        <!-- 右上角编辑按钮 -->
        <div class="edit-button-container" v-if="!isEditing">
          <el-button 
            type="primary" 
            @click="handleEdit"
            class="edit-btn-top"
            size="small"
          >
            <el-icon><Edit /></el-icon>
            编辑信息
          </el-button>
        </div>
        
        <div class="avatar-section">
           <div class="profile-avatar-container">
             <img 
               :src="avatarDisplayUrl" 
               :alt="profileForm.username"
               class="profile-avatar-image"
               v-if="showCustomAvatar"
               @error="handleAvatarError"
             />
             <div 
               v-else
               class="profile-avatar-placeholder-plus"
             >
               <el-icon class="plus-icon"><Plus /></el-icon>
             </div>
           </div>
                     <!-- 头像选择器 -->
           <el-button 
             v-if="isEditing"
             size="small" 
             type="primary" 
             @click="handleAvatarSelectClick"
             class="avatar-select-btn"
           >
             选择头像
           </el-button>
           
           <!-- 头像选择器弹窗 -->
           <AvatarSelector
             v-model="profileForm.avatarUrl"
             :visible="showAvatarSelector"
             @update:visible="showAvatarSelector = $event"
             @upload="handleCustomAvatarUpload"
             @confirm="handleAvatarConfirm"
           />
        </div>
        
        <div class="basic-info">
          <div class="info-row">
            <span class="info-label">用户名：</span>
            <span class="info-value">{{ profileForm.username }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">员工号：</span>
            <span class="info-value">{{ profileForm.employeeCode }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">状态：</span>
            <el-tag :type="profileForm.status === 1 ? 'success' : 'warning'" size="small">
              {{ profileForm.status === 1 ? '正常' : '待激活' }}
            </el-tag>
          </div>
        </div>
      </div>

      <!-- 详细信息区域 -->
      <div class="profile-details">
        <el-row :gutter="32">
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input 
                v-model="profileForm.realName" 
                placeholder="请输入真实姓名"
                class="form-input"
                :disabled="!isEditing"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-input 
                v-model="profileForm.position" 
                disabled
                class="form-input"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="32">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input 
                v-model="profileForm.email" 
                placeholder="请输入邮箱"
                class="form-input"
                :disabled="!isEditing"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="mobile">
              <el-input 
                v-model="profileForm.mobile" 
                placeholder="请输入手机号"
                class="form-input"
                :disabled="!isEditing"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="32">
          <el-col :span="12">
            <el-form-item label="部门" prop="department">
              <el-input 
                v-model="profileForm.department" 
                disabled
                class="form-input"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </div>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <div class="footer-right">
          <template v-if="isEditing">
            <el-button @click="handleCancel" class="cancel-btn">取消</el-button>
            <el-button 
              type="primary" 
              @click="handleSave" 
              :loading="loading"
              :disabled="loading"
              class="save-btn"
            >
              {{ loading ? '处理中...' : '保存' }}
            </el-button>
          </template>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { UserFilled, Edit, Check, Close, Plus } from '@element-plus/icons-vue';
import AvatarSelector from './AvatarSelector.vue';
import { getUserInfo, updateUserProfile } from '../api/auth.js';
import { uploadAvatar } from '../api/system.js';
import { useUserStore } from '../stores/user.js';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:visible', 'success']);

const userStore = useUserStore();
const profileFormRef = ref();
const isEditing = ref(false);
const loading = ref(false);
const showAvatarSelector = ref(false);

const profileForm = reactive({
  id: '',
  username: '',
  realName: '',
  avatarUrl: '', // 改为avatarUrl以匹配后端User实体
  email: '',
  mobile: '',
  employeeCode: '',
  position: '',
  department: '',
  joinDate: '',
  status: 1,
  remark: '',
  // 新增字段：临时存储新选择的头像文件
  newAvatarFile: null,
  newAvatarType: '', // 'custom' 或 'default'
  oldFilePath: '',
  // 新增字段：头像加载状态
  avatarLoadFailed: false
});

const profileRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  mobile: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
};

// 获取用户信息
const getProfileInfo = async () => {
  try {
    const res = await getUserInfo();
    if (res.code === 200) {
      const userInfo = res.data;
      
      Object.assign(profileForm, {
        id: userInfo.id,
        username: userInfo.username,
        realName: userInfo.realName,
        avatarUrl: userInfo.avatarUrl || '', // 使用avatarUrl字段，确保不为undefined
        email: userInfo.email,
        mobile: userInfo.mobile,
        employeeCode: userInfo.employeeInfo?.employeeCode || '',
        position: userInfo.employeeInfo?.position || '',
        department: userInfo.employeeInfo?.responsibleRegions || '',
        joinDate: userInfo.employeeInfo?.entryDate || '', // 修正字段名
        status: userInfo.status || 1, // 默认为1（正常状态）
        remark: userInfo.remark || '',
        oldFilePath: userInfo.avatarUrl || '',
        avatarLoadFailed: false // 重置头像加载失败状态
      });
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    ElMessage.error('获取用户信息失败');
  }
};

// 处理自定义头像上传
const handleCustomAvatarUpload = async (uploadData) => {
  try {
    const { file } = uploadData;
    
    // 不立即上传，而是存储文件信息，等待保存时统一处理
    profileForm.newAvatarFile = file;
    profileForm.newAvatarType = 'custom';
    
    // 显示预览（使用base64）
    const reader = new FileReader();
    reader.onload = (e) => {
      profileForm.avatarUrl = e.target.result;
    };
    reader.readAsDataURL(file);
    
    ElMessage.success('头像已选择，请点击保存按钮保存更改');
    // 关闭头像选择器
    showAvatarSelector.value = false;
  } catch (error) {
    console.error('头像选择失败:', error);
    ElMessage.error('头像选择失败');
  }
};

// 处理头像选择按钮点击
const handleAvatarSelectClick = () => {
  showAvatarSelector.value = true;
};

// 处理头像确认选择
const handleAvatarConfirm = async (avatar) => {
  try {
    if (avatar && !avatar.startsWith('data:image/')) {
      // 如果是默认头像路径，需要先获取文件然后存储
      if (avatar.includes('/avatar/')) {
        // 将默认头像转换为File对象并存储
        const response = await fetch(avatar);
        if (!response.ok) {
          throw new Error('无法访问默认头像文件');
        }
        
        const blob = await response.blob();
        const fileName = avatar.split('/').pop();
        const file = new File([blob], fileName, { type: 'image/png' });
        
        // 存储文件信息，等待保存时统一处理
        profileForm.newAvatarFile = file;
        profileForm.newAvatarType = 'default';
        
        // 更新预览
        profileForm.avatarUrl = avatar;
        
        ElMessage.success('头像已选择，请点击保存按钮保存更改');
        showAvatarSelector.value = false;
      } else {
        // 如果是其他类型的头像URL，也需要处理
        ElMessage.warning('该头像需要先上传到服务器');
      }
    } else if (avatar && avatar.startsWith('data:image/')) {
      // 如果是base64格式（自定义上传），已经在上传处理函数中处理了
      // 这里不需要额外操作
    }
  } catch (error) {
    console.error('头像选择失败:', error);
    ElMessage.error('头像选择失败：' + error.message);
  }
};

// 编辑个人信息
const handleEdit = () => {
  isEditing.value = true;
};

// 保存个人信息
const handleSave = async () => {
  if (!profileFormRef.value) return;
  
  try {
    await profileFormRef.value.validate();
    
    // 如果有新头像需要上传，先处理头像上传
    if (profileForm.newAvatarFile) {
      try {
        // 显示loading状态
        loading.value = true;
        ElMessage.info('正在上传头像，请稍候...');
        
        const res = await uploadAvatar(profileForm.newAvatarFile, profileForm.id, profileForm.oldFilePath);
        if (res.code === 200) {
          // 使用返回的文件路径更新avatarUrl
          profileForm.avatarUrl = res.data.filePath;
          // 清除临时头像信息
          profileForm.newAvatarFile = null;
          profileForm.newAvatarType = '';
          
          // 头像上传成功后，继续保存用户信息
          ElMessage.info('头像上传成功，正在保存用户信息...');
        } else {
          throw new Error(res.message || '头像上传失败');
        }
      } catch (error) {
        console.error('头像上传失败:', error);
        ElMessage.error('头像上传失败：' + error.message);
        loading.value = false;
        return;
      }
    } else {
      // 没有新头像，直接显示保存loading状态
      loading.value = true;
      ElMessage.info('正在保存用户信息，请稍候...');
    }
    
    // 保存用户信息
    const res = await updateUserProfile(profileForm);
    
    if (res.code === 200) {
      ElMessage.success('保存成功');
      isEditing.value = false;
      
      // 检查返回结果中是否包含新的token
      if (res.data && typeof res.data === 'string') {
        // 更新token（data直接是token字符串）
        userStore.updateToken(res.data);
        // 更新token后刷新用户信息，确保主界面头像更新
        await userStore.getUserInfoAction();
      }
      
      // 清除临时头像信息
      profileForm.newAvatarFile = null;
      profileForm.newAvatarType = '';
      profileForm.oldFilePath = '';
      profileForm.avatarLoadFailed = false;
      
      // 关闭弹窗
      emit('update:visible', false);
      
      // 发送成功事件
      emit('success');
    } else {
      ElMessage.error(res.message || '保存失败');
    }
  } catch (error) {
    console.error('保存失败:', error);
    ElMessage.error('保存失败');
  } finally {
    // 确保loading状态被正确重置
    loading.value = false;
  }
};

// 取消编辑
const handleCancel = () => {
  isEditing.value = false;
  // 清除临时头像信息
  profileForm.newAvatarFile = null;
  profileForm.newAvatarType = '';
  // 重新获取用户信息，恢复原始数据
  getProfileInfo();
};

// 关闭弹窗
const handleClose = () => {
  emit('update:visible', false);
  isEditing.value = false;
  resetForm();
};

// 重置表单
const resetForm = () => {
  if (profileFormRef.value) {
    profileFormRef.value.resetFields();
  }
  Object.assign(profileForm, {
    id: '',
    username: '',
    realName: '',
    avatarUrl: '',
    email: '',
    mobile: '',
    employeeCode: '',
    position: '',
    department: '',
    joinDate: '',
    status: 1,
    remark: '',
    newAvatarFile: null,
    newAvatarType: '',
    oldFilePath: '',
    avatarLoadFailed: false
  });
};

// 监听弹窗显示状态，获取用户信息
watch(() => props.visible, (newVal) => {
  if (newVal) {
    getProfileInfo();
  }
});

// 组件挂载时初始化
onMounted(() => {
  if (props.visible) {
    getProfileInfo();
  }
});

// 处理头像加载失败
const handleAvatarError = (event) => {
  // 设置头像加载失败状态
  profileForm.avatarLoadFailed = true;
};

// 获取头像URL，处理默认头像
const getAvatarUrl = (url) => {
  if (!url || url.trim() === '') {
    return null; // 返回null表示显示默认头像
  }
  return url;
};

// 计算属性：头像显示URL
const avatarDisplayUrl = computed(() => {
  if (!profileForm.avatarUrl || profileForm.avatarUrl.trim() === '' || profileForm.avatarLoadFailed) {
    return null; // 返回null表示显示默认头像
  }
  return profileForm.avatarUrl;
});

// 计算属性：是否显示自定义头像
const showCustomAvatar = computed(() => {
  return profileForm.avatarUrl && profileForm.avatarUrl.trim() !== '' && !profileForm.avatarLoadFailed;
});
</script>

<style scoped>
.profile-dialog :deep(.el-dialog__header) {
  padding: 24px 32px 16px;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
}

/* 右上角编辑按钮容器 */
.edit-button-container {
  position: absolute;
  top: 24px;
  right: 24px;
  z-index: 100;
}

.edit-btn-top {
  border-radius: 20px;
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 500;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  border: none;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
  cursor: pointer;
  pointer-events: auto;
}

.edit-btn-top:hover {
  background: linear-gradient(135deg, #337ecc 0%, #2d6da3 100%);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  transform: translateY(-1px);
}

.profile-dialog :deep(.el-dialog__title) {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.profile-dialog :deep(.el-dialog__body) {
  padding: 32px;
}

.profile-dialog :deep(.el-dialog__footer) {
  padding: 16px 32px 24px;
  border-top: 1px solid #f0f0f0;
}

.profile-form {
  margin: 0;
}

/* 头部区域样式 */
.profile-header {
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: 40px;
  margin-bottom: 40px;
  padding: 24px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px;
  border: 1px solid #e9ecef;
  overflow: visible;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.avatar-select-btn {
  border-radius: 20px;
  padding: 8px 20px;
  font-size: 13px;
  font-weight: 500;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  border: none;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

.avatar-select-btn:hover {
  background: linear-gradient(135deg, #337ecc 0%, #2d6da3 100%);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  transform: translateY(-1px);
}

.profile-avatar-container {
  position: relative;
  width: 100px;
  height: 100px;
}

.profile-avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border: 4px solid #ffffff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transition: all 0.3s ease;
  border-radius: 0;
}

.profile-avatar-image:hover {
  transform: scale(1.05);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.16);
}

.profile-avatar-placeholder {
  border: 4px solid #ffffff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transition: all 0.3s ease;
  border-radius: 0;
}

.profile-avatar-placeholder:hover {
  transform: scale(1.05);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.16);
}

.profile-avatar-placeholder-plus {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa; /* 浅灰色背景 */
  border: 2px dashed #dcdfe6; /* 虚线边框 */
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 100px;
  min-height: 100px;
}

.profile-avatar-placeholder-plus:hover {
  background-color: #ebeef5;
  border-color: #c0c4cc;
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.plus-icon {
  font-size: 40px; /* 图标大小 */
  color: #909399; /* 图标颜色 */
  transition: all 0.3s ease;
}

.profile-avatar-placeholder-plus:hover .plus-icon {
  color: #606266;
  transform: scale(1.1);
}


.basic-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-top: 8px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.info-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  min-width: 60px;
}

.info-value {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
}

/* 详细信息区域样式 */
.profile-details {
  background: #ffffff;
  border-radius: 8px;
}

.form-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: all 0.3s ease;
}

.form-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

.form-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
}

.form-textarea :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s ease;
}

.form-textarea :deep(.el-textarea__inner:hover) {
  border-color: #c0c4cc;
}

.form-textarea :deep(.el-textarea__inner:focus) {
  border-color: #409eff;
}

.el-form-item {
  margin-bottom: 24px;
}

.el-form-item__label {
  font-weight: 600;
  color: #606266;
  font-size: 14px;
}

/* 底部按钮区域样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  width: 100%;
}

.footer-right {
  display: flex;
  gap: 12px;
}

.edit-btn {
  border-radius: 6px;
  padding: 10px 24px;
  font-weight: 500;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  border: none;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.edit-btn:hover {
  background: linear-gradient(135deg, #337ecc 0%, #2d6da3 100%);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.cancel-btn {
  border-radius: 6px;
  padding: 10px 20px;
  font-weight: 500;
  border: 1px solid #dcdfe6;
  color: #606266;
  background: #ffffff;
}

.cancel-btn:hover {
  border-color: #c0c4cc;
  color: #303133;
  background: #f5f7fa;
}

.save-btn {
  border-radius: 6px;
  padding: 10px 24px;
  font-weight: 500;
  background: linear-gradient(135deg, #67c23a 0%, #5daf34 100%);
  border: none;
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);
}

.save-btn:hover {
  background: linear-gradient(135deg, #5daf34 0%, #529b2e 100%);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    gap: 24px;
    text-align: center;
  }
  
  .basic-info {
    align-items: center;
  }
  
  .dialog-footer {
    justify-content: center;
  }
}
</style> 