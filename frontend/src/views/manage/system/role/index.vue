<template>
  <div class="role-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div style="margin-bottom: 16px;">
        <el-row :gutter="12" align="middle">
          <el-col :span="6">
            <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable />
          </el-col>
          <el-col :span="6">
            <el-input v-model="queryParams.roleCode" placeholder="请输入角色编码" clearable />
          </el-col>
          <el-col :span="6">
            <el-input v-model="queryParams.description" placeholder="请输入描述" clearable />
          </el-col>
          <el-col :span="6" style="display: flex; gap: 8px; justify-content: space-between;">
            <div style="display: flex; gap: 8px;">
              <el-button type="primary" @click="handleQuery">查询</el-button>
              <el-button @click="resetQuery">重置</el-button>
            </div>
            <el-button type="primary" @click="handleAdd">新增角色</el-button>
          </el-col>
        </el-row>
      </div>

      <el-table
        v-loading="loading"
        :data="roleList"
        border
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="50" align="center" />
        <el-table-column prop="roleName" label="角色名称" align="center" />
        <el-table-column prop="roleCode" label="角色编码" align="center" />
        <el-table-column prop="description" label="描述" align="center" />

        <el-table-column prop="createTime" label="创建时间" align="center" />
        <el-table-column label="操作" width="300" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handlePermission(row)">权限设置</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          :current-page="queryParams.pageNum"
          :page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 角色表单对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleRules"
        label-width="80px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="roleForm.description"
            type="textarea"
            placeholder="请输入描述"
          />
        </el-form-item>

      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 权限设置对话框 -->
    <el-dialog
      :title="`权限设置 - ${currentRoleName}`"
      v-model="permissionDialogVisible"
      width="600px"
      @close="resetPermissionDialog"
      @open="onPermissionDialogOpen"
    >
      <div class="permission-tip">
        <el-alert
          title="权限说明"
          description="勾选菜单项表示该角色拥有访问该菜单的权限。父级菜单被勾选时，子级菜单自动继承权限；取消父级菜单时，子级菜单也会自动取消。"
          type="info"
          :closable="false"
          show-icon
        />
      </div>
      
      <el-tree
        ref="permissionTreeRef"
        :data="permissionTree"
        :props="{ label: 'menuName' }"
        show-checkbox
        node-key="id"
        default-expand-all
        class="permission-tree"
      />
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="permissionLoading" @click="submitPermission">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getRoleList, getRoleMenus, assignRoleMenus, deleteRole, getMenuTree, addRole, updateRole } from '@/api/system';

const loading = ref(false);
const permissionLoading = ref(false);
const formLoading = ref(false);
const dialogVisible = ref(false);
const permissionDialogVisible = ref(false);
const dialogTitle = ref('');
const total = ref(0);
const roleList = ref([]);
const permissionTree = ref([]);
const currentRoleId = ref(null);
const currentRoleName = ref('');
const roleFormRef = ref();
const permissionTreeRef = ref();

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: '',
  roleCode: '',
  description: ''
});

const roleForm = reactive({
  roleName: '',
  roleCode: '',
  description: ''
});

const roleRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 20, message: '角色名称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { min: 2, max: 20, message: '角色编码长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '描述长度不能超过 200 个字符', trigger: 'blur' }
  ]
};

// 获取角色列表
const getList = async () => {
  loading.value = true;
  try {
    const response = await getRoleList(queryParams);
    if (response.code === 200) {
      roleList.value = response.data;
      total.value = response.data.length;
    }
    loading.value = false;
  } catch (error) {
    loading.value = false;
    ElMessage.error('获取角色列表失败');
  }
};

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

// 重置查询
const resetQuery = () => {
  queryParams.roleName = '';
  queryParams.roleCode = '';
  queryParams.description = '';
  queryParams.pageNum = 1;
  getList();
};

// 递归查找菜单
const findMenuById = (menus, menuId) => {
  for (const menu of menus) {
    if (menu.id === menuId) {
      return menu;
    }
    if (menu.children && menu.children.length > 0) {
      const found = findMenuById(menu.children, menuId);
      if (found) {
        return found;
      }
    }
  }
  return null;
};

// 递归查找菜单的所有父级菜单ID
const findParentMenuIds = (menus, menuId, parentIds = []) => {
  for (const menu of menus) {
    if (menu.id === menuId) {
      return parentIds;
    }
    if (menu.children && menu.children.length > 0) {
      const found = findParentMenuIds(menu.children, menuId, [...parentIds, menu.id]);
      if (found) {
        return found;
      }
    }
  }
  return null;
};





// 验证权限设置的合理性
const validatePermissionSettings = (menuIds) => {
  const issues = [];
  
  // 检查是否至少选择了一个菜单
  if (menuIds.length === 0) {
    issues.push('请至少选择一个菜单权限');
    return { valid: false, issues };
  }
  
  return { valid: true, issues };
};



// 获取权限树
const getPermissionTree = async () => {
  try {
    const response = await getMenuTree();
    if (response.code === 200) {
      permissionTree.value = response.data;
    } else {
      ElMessage.error('获取权限树失败');
    }
  } catch (error) {
    ElMessage.error('获取权限树失败');
  }
};

// 新增角色
const handleAdd = () => {
  dialogTitle.value = '新增角色';
  // 重置表单
  resetForm();
  dialogVisible.value = true;
};

// 编辑角色
const handleEdit = (row) => {
  dialogTitle.value = '编辑角色';
  
  // 复制角色数据到表单
  Object.assign(roleForm, {
    id: row.id,
    roleName: row.roleName,
    roleCode: row.roleCode,
    description: row.description
  });
  
  console.log('编辑角色数据:', row);
  console.log('表单数据:', roleForm);
  
  dialogVisible.value = true;
};

// 权限对话框打开时的处理
const onPermissionDialogOpen = async () => {
  // 等待组件完全渲染
  await nextTick();
};

// 权限设置
const handlePermission = async (row) => {
  try {
    // 先打开对话框，让权限树组件渲染
    currentRoleId.value = row.id;
    currentRoleName.value = row.roleName;
    permissionDialogVisible.value = true;
    
    // 等待对话框打开和权限树组件渲染
    await nextTick();
    
    // 检查权限树组件是否已渲染
    if (!permissionTreeRef.value) {
      ElMessage.error('权限树组件初始化失败，请刷新页面重试');
      return;
    }
    
    // 获取角色菜单权限
    const response = await getRoleMenus(row.id);
    
    if (response.code === 200) {
      // 先清空所有选中状态
      permissionTreeRef.value.setCheckedKeys([]);
      
      // 设置已选中的菜单
      const checkedMenuIds = response.data.map(menu => menu.id);
      
      // 设置选中状态
      permissionTreeRef.value.setCheckedKeys(checkedMenuIds);
    }
  } catch (error) {
    ElMessage.error('获取角色权限失败');
  }
};



// 删除角色
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该角色吗？删除后不可恢复！',
      '提示',
      { type: 'warning' }
    );
    
    const response = await deleteRole(row.id);
    if (response.code === 200) {
      ElMessage.success('删除成功');
      getList(); // 刷新列表
    } else {
      ElMessage.error('删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败');
    }
  }
};

// 提交表单
const submitForm = async () => {
  if (!roleFormRef.value) return;
  
  try {
    await roleFormRef.value.validate();
    
    formLoading.value = true;
    
    let response;
    if (roleForm.id) {
      // 编辑角色
      response = await updateRole(roleForm);
    } else {
      // 新增角色
      response = await addRole(roleForm);
    }
    
    if (response.code === 200) {
      dialogVisible.value = false;
      ElMessage.success(roleForm.id ? '编辑成功' : '新增成功');
      getList(); // 刷新列表
    } else {
      ElMessage.error(roleForm.id ? '编辑失败' : '新增失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败');
    }
  } finally {
    formLoading.value = false;
  }
};

// 提交权限设置
const submitPermission = async () => {
  if (!permissionTreeRef.value || !currentRoleId.value) return;
  
  try {
    permissionLoading.value = true;
    
    // 直接获取所有选中的节点（checkedKeys已经包含了所有相关节点）
    const checkedKeys = permissionTreeRef.value.getCheckedKeys();
  
    // 过滤有效的菜单ID（目录和菜单类型）
    const validMenuIds = checkedKeys.filter(menuId => {
      const menu = findMenuById(permissionTree.value, menuId);
      return menu && (menu.menuType === 'M' || menu.menuType === 'C');
    });
    
    const finalMenuIds = validMenuIds;
    
    // 验证权限设置是否合理
    const validation = validatePermissionSettings(finalMenuIds);
    if (!validation.valid) {
      ElMessage.warning(validation.issues.join('; '));
      return;
    }
    
    const response = await assignRoleMenus(currentRoleId.value, finalMenuIds);
    if (response.code === 200) {
      permissionDialogVisible.value = false;
      ElMessage.success('权限设置成功');
      
      // 刷新角色列表
      getList();
    } else {
      ElMessage.error('权限设置失败');
    }
  } catch (error) {
    ElMessage.error('权限设置失败');
  } finally {
    permissionLoading.value = false;
  }
};

// 重置表单
const resetForm = () => {
  if (roleFormRef.value) {
    roleFormRef.value.resetFields();
  }
  Object.assign(roleForm, {
    id: undefined,
    roleName: '',
    roleCode: '',
    description: ''
  });
};

// 重置权限对话框
const resetPermissionDialog = () => {
  currentRoleId.value = null;
  currentRoleName.value = '';
  
  // 等待组件完全渲染后再清理
  nextTick(() => {
    if (permissionTreeRef.value) {
      permissionTreeRef.value.setCheckedKeys([]);
    }
  });
};

// 分页大小改变
const handleSizeChange = (val) => {
  queryParams.pageSize = val;
  getList();
};

// 页码改变
const handleCurrentChange = (val) => {
  queryParams.pageNum = val;
  getList();
};

onMounted(async () => {
  // 并行获取数据
  await Promise.all([
    getList(),
    getPermissionTree()
  ]);
});
</script>

<style scoped>
.role-container {
  padding: 20px;
}



.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-area {
  margin-bottom: 16px;
}

.search-area .el-input,
.search-area .el-select {
  width: 100%;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.permission-tip {
  margin-bottom: 20px;
}

.permission-tree {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
}
</style> 