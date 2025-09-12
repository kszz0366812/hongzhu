<template>
  <div class="user-container">
    <el-card>
      <div style="margin-bottom: 16px;">
        <el-row :gutter="12" align="middle">
          <el-col :span="6">
            <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
          </el-col>
          <el-col :span="6">
            <el-input v-model="queryParams.realName" placeholder="请输入真实姓名" clearable />
          </el-col>
          <el-col :span="6">
            <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 100%">
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-col>
          <el-col :span="6" style="display: flex; gap: 8px; justify-content: space-between;">
            <div style="display: flex; gap: 8px;">
              <el-button type="primary" @click="handleQuery">查询</el-button>
              <el-button @click="resetQuery">重置</el-button>
            </div>
            <el-button type="primary" @click="handleAdd">新增用户</el-button>
          </el-col>
        </el-row>
      </div>

      <el-table
        v-loading="loading"
        :data="userList"
        border
        stripe
        highlight-current-row
        style="width: 100%"
        class="user-table"
      >
        <el-table-column type="index" label="序号" align="center" width="60" />
        <el-table-column prop="username" label="用户名" align="center" />
        <el-table-column prop="realName" label="真实姓名" align="center" />
        <el-table-column prop="employeeInfo.employeeCode" label="员工号" align="center">
          <template #default="{ row }">
            <span v-if="row.employeeInfo && row.employeeInfo.employeeCode">{{ row.employeeInfo.employeeCode }}</span>
            <span v-else style="color: #909399;">未绑定员工</span>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" align="center" width="200" />
        <el-table-column prop="mobile" label="手机号" align="center" />
        <el-table-column prop="roleNames" label="角色" align="center">
          <template #default="{ row }">
            <template v-if="row.roleNames && row.roleNames.length > 0">
              <el-tag v-for="roleName in row.roleNames" 
                     :key="roleName" 
                     size="small" 
                     style="margin-right: 4px;">
                {{ roleName }}
              </el-tag>
            </template>
            <span v-else style="color: #909399;">未分配角色</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建日期" align="center" width="120">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="350" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
                      <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
        <el-button type="warning" size="small" @click="handleResetPwd(row)">重置密码</el-button>
        <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        <el-button
          size="small"
          :type="row.status === 1 ? 'danger' : 'success'"
          @click="handleStatusChange(row)"
        >
          {{ row.status === 1 ? '禁用' : '启用' }}
        </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        :current-page="queryParams.current"
        :page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 16px; text-align: right;"
      />
    </el-card>

    <!-- 用户表单对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
      @close="resetForm"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
        class="user-form"
      >
        <el-form-item label="绑定员工" prop="employeeId">
          <EmployeeSelector
            v-model="selectedEmployee"
            placeholder="请选择员工"
            @change="handleEmployeeChange"
          />
        </el-form-item>
        
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="优先使用员工号，可手动修改" />
        </el-form-item>
        
        <el-form-item label="头像" prop="avatarUrl">
          <div class="avatar-section">
            <div class="current-avatar-container">
              <img 
                :src="userForm.avatarUrl" 
                :alt="userForm.realName"
                class="current-avatar-image"
                v-if="userForm.avatarUrl && userForm.avatarUrl.trim() !== ''"
                @error="handleAvatarError"
              />
              <div 
                v-else
                class="current-avatar-placeholder"
              >
                <el-icon class="placeholder-icon"><UserFilled /></el-icon>
              </div>
            </div>
            <el-button 
              size="small" 
              type="primary" 
              @click="handleAvatarSelectClick"
              class="avatar-select-btn"
            >
              选择头像
            </el-button>
            
            <!-- 头像选择器弹窗 -->
            <AvatarSelector
              v-model="userForm.avatarUrl"
              :visible="showAvatarSelector"
              @update:visible="showAvatarSelector = $event"
              @upload="handleCustomAvatarUpload"
              @confirm="handleAvatarConfirm"
              @update:modelValue="handleAvatarUpdate"
            />
          </div>
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" placeholder="自动填充员工姓名" readonly />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="userForm.mobile" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item label="角色" prop="roleId">
          <el-select
            v-model="userForm.roleId"
            placeholder="请选择角色"
            style="width: 100%"
          >
            <el-option
              v-for="role in roleOptions"
              :key="role.id"
              :label="role.roleName"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="submitForm"
          :loading="loading"
          :disabled="loading"
        >
          {{ loading ? '处理中...' : '确定' }}
        </el-button>
      </template>
    </el-dialog>


  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { UserFilled, Plus, Edit, Delete, Search, Refresh } from '@element-plus/icons-vue';
import AvatarSelector from '@/components/AvatarSelector.vue';
import EmployeeSelector from '@/components/EmployeeSelector.vue'; // 导入EmployeeSelector组件
import { useUserStore } from '@/stores/user';

import { 
  getUserPage, 
  getUserById, 
  addUser, 
  updateUser, 
  deleteUser, 
  resetUserPassword, 
  changeUserStatus,
  getRoleList,
  getEmployeeList,
  uploadAvatar
} from '@/api/system';

// 用户store
const userStore = useUserStore();

// 响应式数据
const loading = ref(false);
const total = ref(0);
const userList = ref([]);
const roleOptions = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref('');
const showAvatarSelector = ref(false);
const userFormRef = ref();
const selectedEmployee = ref(null);

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  username: '',
  realName: '',
  status: undefined
});

// 表单数据
const userForm = reactive({
  id: undefined,
  username: '',
  realName: '',
  avatarUrl: '',
  employeeId: undefined,
  email: '',
  mobile: '',
  roleId: undefined,
  status: 1,
  oldFilePath: '',
  newAvatarFile: null,
  newAvatarType: ''
});

const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 1, max: 20, message: '长度在1 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  employeeId: [
    { required: true, message: '请选择员工', trigger: 'change' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  mobile: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  roleId: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
};

// 获取用户列表
const getList = async () => {
  loading.value = true;
  try {
    const res = await getUserPage(queryParams);
    if (res.code === 200) {
      // 适配新的PageInfo返回结构
      userList.value = res.data.list || [];
      total.value = res.data.total || 0;
      queryParams.current = res.data.pageNum || 1;
      queryParams.size = res.data.pageSize || 10;
      
      // 为每个用户设置角色名称（直接从roleId获取）
      userList.value.forEach(user => {
        if (user.roleId) {
          const role = roleOptions.value.find(r => r.id === user.roleId);
          user.roleNames = role ? [role.roleName] : [];
        } else {
          user.roleNames = [];
        }
      });
    } else {
      ElMessage.error(res.message || '获取用户列表失败');
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败');
  } finally {
    loading.value = false;
  }
};

// 获取角色列表
const getRoleOptions = async () => {
  try {
    const res = await getRoleList();
    if (res.code === 200) {
      roleOptions.value = res.data;
    } else {
      ElMessage.error(res.message || '获取角色列表失败');
    }
  } catch (error) {
    ElMessage.error('获取角色列表失败');
  }
};

// 处理头像选择按钮点击
const handleAvatarSelectClick = () => {
  showAvatarSelector.value = true;
};

// 处理自定义头像上传
const handleCustomAvatarUpload = async (uploadData) => {
  try {
    const { file } = uploadData;
    
    // 不立即上传，而是存储文件信息，等待保存时统一处理
    userForm.newAvatarFile = file;
    userForm.newAvatarType = 'custom';
    
    // 显示预览（使用base64）
    const reader = new FileReader();
    reader.onload = (e) => {
      userForm.avatarUrl = e.target.result;
      // 强制触发响应式更新
      userForm.avatarUrl = e.target.result;
    };
    reader.readAsDataURL(file);
    
    ElMessage.success('头像已选择，请点击确定按钮保存更改');
    // 关闭头像选择器
    showAvatarSelector.value = false;
  } catch (error) {
    console.error('头像选择失败:', error);
    ElMessage.error('头像选择失败');
  }
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
        userForm.newAvatarFile = file;
        userForm.newAvatarType = 'default';
        
        // 更新预览
        userForm.avatarUrl = avatar;
        // 强制触发响应式更新
        userForm.avatarUrl = avatar;
        
        ElMessage.success('头像已选择，请点击确定按钮保存更改');
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

// 处理头像加载失败
const handleAvatarError = (event) => {
  // 头像加载失败时，隐藏图片元素，让占位符显示
  event.target.style.display = 'none';
  // 清空avatar字段，触发占位符显示
  userForm.avatarUrl = '';
};

// 处理头像选择器更新
const handleAvatarUpdate = (newAvatarUrl) => {
  userForm.avatarUrl = newAvatarUrl;
};

// 员工选择变化处理
const handleEmployeeChange = (employee) => {
  console.log('选择的员工信息:', employee)
  if (employee) {
    // 绑定员工ID
    userForm.employeeId = employee.id;
    // 自动填充员工相关信息
    userForm.realName = employee.name || '';
    // 用户名优先使用员工号，如果没有员工号则使用员工姓名
    if (employee.employeeCode) {
      userForm.username = employee.employeeCode;
    } else if (employee.name) {
      userForm.username = employee.name;
    }
    // 注意：员工实体中没有email和mobile字段，所以不自动填充
  } else {
    // 清空员工相关信息
    userForm.employeeId = undefined;
    userForm.realName = '';
    userForm.username = '';
  }
};

// 查询
const handleQuery = () => {
  queryParams.current = 1; // 重置到第一页
  getList();
};

// 重置查询
const resetQuery = () => {
  queryParams.username = '';
  queryParams.realName = '';
  queryParams.status = undefined;
  queryParams.current = 1; // 重置到第一页
  getList();
};

// 新增用户
const handleAdd = () => {
  dialogTitle.value = '新增用户';
  // 重置整个表单，确保所有字段都被正确初始化
  resetForm();
  dialogVisible.value = true;
};

// 编辑用户
const handleEdit = (row) => {
  dialogTitle.value = '编辑用户';
  
  Object.assign(userForm, {
    id: row.id,
    username: row.username,
    realName: row.realName,
    avatarUrl: row.avatarUrl || '', // 使用正确的字段名
    employeeId: row.employeeId,
    email: row.email,
    mobile: row.mobile,
    roleId: row.roleId,
    status: row.status,
    oldFilePath: row.avatarUrl || '', // 保存旧头像路径
    newAvatarFile: null,
    newAvatarType: ''
  });
  
  // 如果绑定了员工，设置选中的员工对象
  if (row.employeeId && row.employeeInfo) {
    selectedEmployee.value = row.employeeInfo;
    userForm.realName = row.employeeInfo.name || row.realName;
  } else {
    selectedEmployee.value = null;
  }
  
  dialogVisible.value = true;
};

// 删除用户
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      type: 'warning'
    });
    const res = await deleteUser(row.id);
    if (res.code === 200) {
      ElMessage.success('删除成功');
      getList();
    } else {
      ElMessage.error(res.message || '删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败');
    }
  }
};



// 重置密码
const handleResetPwd = async (row) => {
  try {
    await ElMessageBox.confirm('确定要重置该用户的密码吗？', '提示', {
      type: 'warning'
    });
    const res = await resetUserPassword(row.id);
    if (res.code === 200) {
      ElMessage.success('密码重置成功');
    } else {
      ElMessage.error(res.message || '重置密码失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重置密码失败');
    }
  }
};

// 修改用户状态
const handleStatusChange = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 1 ? '禁用' : '启用'}该用户吗？`,
      '提示',
      { type: 'warning' }
    );
    const newStatus = row.status === 1 ? 0 : 1;
    const res = await changeUserStatus(row.id, newStatus);
    if (res.code === 200) {
      ElMessage.success('操作成功');
      // 检查是否需要更新token
      if (res.data && typeof res.data === 'string') {
        userStore.updateToken(res.data);
        // 更新token后刷新用户信息，确保主界面头像更新
        await userStore.getUserInfoAction();
      }
      getList();
    } else {
      ElMessage.error(res.message || '操作失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('修改状态失败');
    }
  }
};



// 提交表单
const submitForm = async () => {
  if (!userFormRef.value) return;
  
  try {
    await userFormRef.value.validate();
    
    let isNewUser = false;
    
    // 如果是新增用户，先保存用户信息获取ID
    if (!userForm.id) {
      isNewUser = true;
      
      // 创建不包含id和头像字段的对象，避免传递undefined给后端
      const addUserData = {
        username: userForm.username,
        realName: userForm.realName,
        employeeId: userForm.employeeId,
        email: userForm.email,
        mobile: userForm.mobile,
        roleId: userForm.roleId,
        status: userForm.status
      };
      
      // 过滤掉空值，避免传递undefined或空字符串
      const filteredAddUserData = {};
      Object.keys(addUserData).forEach(key => {
        if (addUserData[key] !== undefined && addUserData[key] !== null && addUserData[key] !== '') {
          filteredAddUserData[key] = addUserData[key];
        }
      });
      
      const addRes = await addUser(filteredAddUserData);
      if (addRes.code === 200) {
        // 新增成功后，获取用户ID和用户信息
        const newUser = addRes.data;
        userForm.id = newUser.id;
        // 使用返回的用户信息更新表单，确保数据一致性
        Object.assign(userForm, {
          id: newUser.id,
          username: newUser.username,
          realName: newUser.realName,
          email: newUser.email,
          mobile: newUser.mobile,
          employeeId: newUser.employeeId,
          roleId: newUser.roleId,
          status: newUser.status,
          avatarUrl: newUser.avatarUrl || ''
        });
        ElMessage.success('用户新增成功，正在处理头像...');
      } else {
        ElMessage.error(addRes.message || '新增用户失败');
        return;
      }
    }
    
    // 如果有新头像需要上传，处理头像上传
    if (userForm.newAvatarFile) {
      try {
        // 显示loading状态
        loading.value = true;
        ElMessage.info('正在上传头像，请稍候...');
        
        const res = await uploadAvatar(userForm.newAvatarFile, userForm.id, userForm.oldFilePath);
        if (res.code === 200) {
          // 使用返回的文件路径更新avatar
          userForm.avatarUrl = res.data.filePath;
          // 清除临时头像信息
          userForm.newAvatarFile = null;
          userForm.newAvatarType = '';
          userForm.oldFilePath = '';
          
          // 头像上传成功后，必须调用更新接口更新用户信息
          ElMessage.info('头像上传成功，正在更新用户信息...');
          
          // 创建不包含临时字段的更新数据
          const updateUserData = {
            id: userForm.id,
            username: userForm.username,
            realName: userForm.realName,
            employeeId: userForm.employeeId,
            email: userForm.email,
            mobile: userForm.mobile,
            roleId: userForm.roleId,
            status: userForm.status,
            avatarUrl: userForm.avatarUrl
          };
          
          const updateRes = await updateUser(updateUserData);
          if (updateRes.code === 200) {
            ElMessage.success('头像上传成功，用户信息更新完成');
            // 检查是否需要更新token
            if (updateRes.data && typeof updateRes.data === 'string') {
              userStore.updateToken(updateRes.data);
              // 更新token后刷新用户信息，确保主界面头像更新
              await userStore.refreshUserInfo();
            }
            dialogVisible.value = false;
            getList();
          } else {
            ElMessage.error('头像上传成功，但用户信息更新失败：' + updateRes.message);
          }
        } else {
          throw new Error(res.message || '头像上传失败');
        }
      } catch (error) {
        console.error('头像上传失败:', error);
        ElMessage.error('头像上传失败：' + error.message);
        return;
      } finally {
        // 隐藏loading状态
        loading.value = false;
      }
    } else {
      // 没有新头像，直接处理结果
      if (isNewUser) {
        // 新增用户且没有头像，直接成功
        ElMessage.success('用户新增成功');
        dialogVisible.value = false;
        getList();
      } else {
        // 编辑用户，调用更新接口
        try {
          // 显示loading状态
          loading.value = true;
          ElMessage.info('正在更新用户信息，请稍候...');
          
          // 创建不包含临时字段的更新数据
          const updateUserData = {
            id: userForm.id,
            username: userForm.username,
            realName: userForm.realName,
            employeeId: userForm.employeeId,
            email: userForm.email,
            mobile: userForm.mobile,
            roleId: userForm.roleId,
            status: userForm.status,
            avatarUrl: userForm.avatarUrl
          };
          
          const updateRes = await updateUser(updateUserData);
          if (updateRes.code === 200) {
            ElMessage.success('修改成功');
            // 检查是否需要更新token
            if (updateRes.data && typeof updateRes.data === 'string') {
              userStore.updateToken(updateRes.data);
              // 更新token后刷新用户信息，确保主界面头像更新
              await userStore.refreshUserInfo();
            }
            dialogVisible.value = false;
            getList();
          } else {
            ElMessage.error(updateRes.message || '操作失败');
          }
        } finally {
          // 隐藏loading状态
          loading.value = false;
        }
      }
    }
  } catch (error) {
    console.error('提交失败:', error);
    ElMessage.error('提交失败');
    // 确保loading状态被重置
    loading.value = false;
  }
};

// 重置表单
const resetForm = () => {
  if (userFormRef.value) {
    userFormRef.value.resetFields();
  }
  Object.assign(userForm, {
    id: undefined,
    username: '',
    realName: '',
    avatarUrl: '',
    employeeId: undefined,
    email: '',
    mobile: '',
    roleId: 2, // 默认设置为普通用户角色
    status: 1,
    oldFilePath: '',
    newAvatarFile: null,
    newAvatarType: ''
  });
  
  // 清空选中的员工
  selectedEmployee.value = null;
};

// 格式化日期为 yyyy-mm-dd
const formatDate = (dateString) => {
  if (!dateString) return '-';
  try {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return '-';
    return date.toISOString().split('T')[0];
  } catch (error) {
    return '-';
  }
};

// 分页大小改变
const handleSizeChange = (val) => {
  queryParams.size = val;
  queryParams.current = 1; // 重置到第一页
  getList();
};

// 页码改变
const handleCurrentChange = (val) => {
  queryParams.current = val;
  getList();
};

onMounted(async () => {
  await Promise.all([
    getRoleOptions() // 获取角色列表
  ]);
  getList(); // 再获取用户列表
});
</script>

<style scoped>
.user-container {
  padding: 20px;
}



.user-table {
  margin-top: 0;
}

/* 表格列宽度优化 */
.user-table .el-table__header-wrapper th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

.user-table .el-table__body-wrapper td {
  padding: 8px 0;
}

/* 序号列样式 */
.user-table .el-table__row .el-table_1_column_1 {
  background-color: #fafafa;
}

/* 邮箱列样式 */
.user-table .el-table__row .el-table_1_column_6 {
  word-break: break-all;
}

/* 创建日期列样式 */
.user-table .el-table__row .el-table_1_column_9 {
  font-family: 'Courier New', monospace;
  font-size: 13px;
}

.action-buttons {
  display: flex;
  gap: 4px;
  justify-content: center;
  flex-wrap: nowrap;
}

.action-buttons .el-button {
  margin: 0;
  white-space: nowrap;
}



.user-form .el-form-item {
  margin-bottom: 20px;
}

.user-form .el-form-item__label {
  font-weight: 500;
  color: #606266;
}

.user-form .el-input,
.user-form .el-select {
  width: 100%;
}

.user-form .el-radio-group {
  display: flex;
  gap: 16px;
}

/* 状态标签样式 */
.el-tag--success {
  background: #f0f9ff;
  border-color: #67c23a;
  color: #67c23a;
}

.el-tag--danger {
  background: #fef0f0;
  border-color: #f56c6c;
  color: #f56c6c;
}

/* 表格行悬停效果 */
.user-table .el-table__row:hover {
  background-color: #f5f7fa;
}

/* 头像选择器样式 */
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.current-avatar-container {
  position: relative;
  width: 80px;
  height: 80px;
}

.current-avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border: 3px solid #ffffff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border-radius: 0;
}

.current-avatar-image:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.current-avatar-placeholder {
  width: 80px;
  height: 80px;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  min-width: 80px;
  min-height: 80px;
}

.current-avatar-placeholder:hover {
  background-color: #ebeef5;
  border-color: #c0c4cc;
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.placeholder-icon {
  font-size: 36px;
  color: #909399;
  transition: all 0.3s ease;
}

.current-avatar-placeholder:hover .placeholder-icon {
  color: #606266;
  transform: scale(1.1);
}

/* 按钮样式统一 */
.search-form .el-button {
  margin-right: 8px;
}

.search-form .el-button:last-child {
  margin-right: 0;
}


</style> 