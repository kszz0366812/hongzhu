<template>
  <div class="menu-container">
    <div class="page-header">
      <h2>菜单管理</h2>
      <el-button type="primary" @click="handleAdd">新增菜单</el-button>
    </div>

    <div class="menu-content">
             <div class="menu-tree-panel">
         <h3>菜单结构</h3>
         <div class="tree-container">
           <el-tree
             :data="menuTree"
             :props="treeProps"
             node-key="id"
             highlight-current
             @node-click="handleNodeClick"
           >
             <template #default="{ node, data }">
               <span class="tree-node">
                 <i :class="getMenuIcon(data.menuType, data.icon)"></i>
                 {{ node.label }}
                 <el-tag size="small" :type="getMenuTypeTagType(data.menuType)">
                   {{ getMenuTypeLabel(data.menuType) }}
                 </el-tag>
               </span>
             </template>
           </el-tree>
         </div>
       </div>

      <div class="menu-detail-panel">
        <div v-if="currentMenu" class="detail-content">
          <div class="detail-header">
            <div class="menu-info">
              <h3>{{ currentMenu.menuName }}</h3>
              <div class="menu-meta" v-if="currentMenu.menuType === 'C' && currentMenu.parentId">
                <el-text type="info" size="small">
                  上级菜单：{{ getParentMenuName(currentMenu.parentId) }}
                </el-text>
              </div>
              <div class="menu-path" v-if="currentMenu.menuType === 'C'">
                <el-text type="info" size="small">
                  菜单路径：{{ getMenuPath(currentMenu.id) }}
                </el-text>
              </div>
            </div>
            <div class="actions">
              <el-button type="primary" @click="toggleEditMode">
                {{ isEditMode ? '保存' : '编辑' }}
              </el-button>
              <el-button v-if="isEditMode" @click="cancelEdit">取消</el-button>
              <el-button type="danger" @click="handleDelete">删除</el-button>
            </div>
          </div>

          <el-form 
            :model="menuForm"
            :rules="menuRules"
            ref="formRef"
            label-width="120px"
            :disabled="!isEditMode"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="菜单名称" prop="menuName">
                  <el-input v-model="menuForm.menuName" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="菜单类型" prop="menuType">
                  <el-radio-group v-model="menuForm.menuType">
                    <el-radio label="M">一级菜单</el-radio>
                    <el-radio label="C">二级菜单</el-radio>
                  </el-radio-group>
                  <div class="form-tip" v-if="isEditMode">
                    <el-text type="warning" size="small">
                      切换菜单类型将清空相关字段，请谨慎操作
                    </el-text>
                  </div>
                  <div class="form-tip" v-if="isEditMode">
                    <el-text type="info" size="small">
                      提示：一级菜单(M)用于分组，二级菜单(C)用于具体功能页面
                    </el-text>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 上级菜单选择 - 仅在编辑二级菜单时显示 -->
            <el-row :gutter="20" v-if="menuForm.menuType === 'C'">
              <el-col :span="12">
                <el-form-item label="上级菜单" prop="parentId">
                  <el-tree-select
                    v-model="menuForm.parentId"
                    :data="parentMenuTree"
                    :props="treeProps"
                    placeholder="请选择上级菜单"
                    check-strictly
                    clearable
                    :filterable="true"
                    value-key="id"
                    :disabled="!isEditMode"
                  />
                  <div class="form-tip" v-if="isEditMode">
                    <el-text type="info" size="small">只能选择一级菜单作为上级菜单</el-text>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="显示顺序" prop="orderNum">
                  <el-input-number v-model="menuForm.orderNum" :min="0" :max="999" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20" v-if="menuForm.menuType === 'M'">
              <el-col :span="12">
                <el-form-item label="路由地址" prop="path">
                  <el-input v-model="menuForm.path" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="显示顺序" prop="orderNum">
                  <el-input-number v-model="menuForm.orderNum" :min="0" :max="999" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20" v-if="menuForm.menuType === 'C'">
              <el-col :span="12">
                <el-form-item label="路由地址" prop="path">
                  <el-input v-model="menuForm.path" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="组件路径" prop="component">
                  <el-input v-model="menuForm.component" placeholder="如：manage/system/menu/index" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="显示状态">
                  <el-radio-group v-model="menuForm.visible">
                    <el-radio :label="1">显示</el-radio>
                    <el-radio :label="0">隐藏</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="启用状态">
                  <el-radio-group v-model="menuForm.status">
                    <el-radio :label="1">启用</el-radio>
                    <el-radio :label="0">禁用</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
        
        <div v-else class="empty-state">
          <el-empty description="请选择左侧菜单查看详情" />
        </div>
      </div>
    </div>

    <el-dialog title="新增菜单" v-model="addDialogVisible" width="600px">
      <div class="dialog-tip" v-if="currentMenu">
        <el-text type="info" size="small">
          当前选中：{{ currentMenu.menuName }} ({{ getMenuTypeLabel(currentMenu.menuType) }})
        </el-text>
      </div>
      
      <el-form :model="addForm" :rules="addMenuRules" ref="addFormRef" label-width="100px">
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="addForm.menuName" />
        </el-form-item>
        
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="addForm.menuType">
            <el-radio label="M">一级菜单</el-radio>
            <el-radio label="C">二级菜单</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="上级菜单" v-if="addForm.menuType === 'C'" prop="parentId">
          <el-tree-select
            v-model="addForm.parentId"
            :data="parentMenuTree"
            :props="treeProps"
            placeholder="请选择上级菜单"
            check-strictly
            clearable
            :filterable="true"
            value-key="id"
          />
          <div class="form-tip" v-if="!addForm.parentId">
            <el-text type="warning" size="small">请选择上级菜单</el-text>
          </div>
        </el-form-item>
        
        <el-form-item label="路由地址" prop="path">
          <el-input v-model="addForm.path" />
        </el-form-item>
        
        <el-form-item label="组件路径" prop="component" v-if="addForm.menuType === 'C'">
          <el-input v-model="addForm.component" placeholder="如：manage/system/menu/index" />
        </el-form-item>
        
        <el-form-item label="显示顺序" prop="orderNum">
          <el-input-number v-model="addForm.orderNum" :min="0" :max="999" />
        </el-form-item>
        
        <el-form-item label="显示状态">
          <el-radio-group v-model="addForm.visible">
            <el-radio :label="1">显示</el-radio>
            <el-radio :label="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="启用状态">
          <el-radio-group v-model="addForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMenuTree, createMenu, updateMenu, deleteMenu } from '@/api/system'

const menuTree = ref([])
const currentMenu = ref(null)
const isEditMode = ref(false)
const addDialogVisible = ref(false)
const formRef = ref()
const addFormRef = ref()

const menuForm = reactive({
  id: null,
  parentId: null,
  menuName: '',
  orderNum: 0,
  path: '',
  component: '',
  isFrame: 1,
  isCache: 1,
  menuType: 'M',
  visible: 1,
  status: 1
})

const addForm = reactive({
  parentId: null,
  menuName: '',
  orderNum: 0,
  path: '',
  component: '',
  isFrame: 1,
  isCache: 1,
  menuType: 'C',
  visible: 1,
  status: 1
})

const menuRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  orderNum: [{ required: true, message: '请输入显示顺序', trigger: 'blur' }],
  path: [{ required: true, message: '请输入路由地址', trigger: 'blur' }],
  parentId: [
    { 
      required: true, 
      message: '请选择上级菜单', 
      trigger: 'change',
      validator: (rule, value, callback) => {
        if (menuForm.menuType === 'C' && !value) {
          callback(new Error('请选择上级菜单'))
        } else {
          callback()
        }
      }
    }
  ]
}

const addMenuRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
  parentId: [
    { 
      required: true, 
      message: '请选择上级菜单', 
      trigger: 'change',
      validator: (rule, value, callback) => {
        if (addForm.menuType === 'C' && !value) {
          callback(new Error('请选择上级菜单'))
        } else {
          callback()
        }
      }
    }
  ],
  path: [{ required: true, message: '请输入路由地址', trigger: 'blur' }],
  orderNum: [{ required: true, message: '请输入显示顺序', trigger: 'blur' }],
  visible: [{ required: true, message: '请选择显示状态', trigger: 'change' }],
  status: [{ required: true, message: '请选择启用状态', trigger: 'change' }]
}

const treeProps = {
  children: 'children',
  label: 'menuName'
}

const loadMenuTree = async () => {
  try {
    const response = await getMenuTree()
    if (response.code === 200) {
      menuTree.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载菜单树失败')
  }
}

const handleNodeClick = (data) => {
  currentMenu.value = data
  isEditMode.value = false
  Object.assign(menuForm, data)
}

const handleAdd = () => {
  // 重置表单
  Object.assign(addForm, {
    parentId: null,
    menuName: '',
    orderNum: 0,
    path: '',
    component: '',
    isFrame: 1,
    isCache: 1,
    menuType: 'C',
    visible: 1,
    status: 1
  })
  
  // 如果当前选中的是一级菜单，且要新增二级菜单，则自动设置上级菜单
  if (currentMenu.value && currentMenu.value.menuType === 'M') {
    addForm.parentId = currentMenu.value.id
  }
  
  addDialogVisible.value = true
}

const handleAddSubmit = async () => {
  try {
    // 表单验证
    await addFormRef.value.validate()
    
    // 额外验证：如果是二级菜单类型，必须选择上级菜单
    if (addForm.menuType === 'C' && !addForm.parentId) {
      ElMessage.warning('请选择上级菜单')
      return
    }
    
    // 验证：防止循环引用
    if (addForm.menuType === 'C' && checkCircularReference(null, addForm.parentId)) {
      ElMessage.warning('不能选择会造成循环引用的上级菜单')
      return
    }
    
    const response = await createMenu(addForm)
    if (response.code === 200) {
      ElMessage.success('创建成功')
      addDialogVisible.value = false
      await loadMenuTree()
      
      // 如果新增的是二级菜单，自动选中其上级菜单
      if (addForm.menuType === 'C' && addForm.parentId) {
        const parentMenu = findMenuById(menuTree.value, addForm.parentId)
        if (parentMenu) {
          handleNodeClick(parentMenu)
        }
      }
    }
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('创建失败')
    }
  }
}

const toggleEditMode = async () => {
  if (isEditMode.value) {
    try {
      // 表单验证
      await formRef.value.validate()
      
      // 额外验证：如果是二级菜单，必须选择上级菜单
      if (menuForm.menuType === 'C' && !menuForm.parentId) {
        ElMessage.warning('请选择上级菜单')
        return
      }
      
      // 验证：不能选择自己作为上级菜单
      if (menuForm.menuType === 'C' && menuForm.parentId === menuForm.id) {
        ElMessage.warning('不能选择自己作为上级菜单')
        return
      }
      
      // 验证：防止循环引用
      if (menuForm.menuType === 'C' && checkCircularReference(menuForm.id, menuForm.parentId)) {
        ElMessage.warning('不能选择自己的子菜单作为上级菜单，这会造成循环引用')
        return
      }
      
      const response = await updateMenu(menuForm)
      if (response.code === 200) {
        ElMessage.success(`菜单"${menuForm.menuName}"更新成功`)
        isEditMode.value = false
        
        // 重新加载菜单树
        await loadMenuTree()
        
        // 更新成功后，重新选中当前菜单并更新表单数据
        if (currentMenu.value) {
          const updatedMenu = findMenuById(menuTree.value, currentMenu.value.id)
          if (updatedMenu) {
            currentMenu.value = updatedMenu
            Object.assign(menuForm, updatedMenu)
            
            // 显示更新后的菜单信息
            setTimeout(() => {
              ElMessage.info(`当前菜单：${updatedMenu.menuName} (${getMenuTypeLabel(updatedMenu.menuType)})`)
            }, 500)
          }
        }
      }
    } catch (error) {
      if (error.message) {
        ElMessage.error(error.message)
      } else {
        ElMessage.error('更新失败')
      }
    }
  } else {
    isEditMode.value = true
  }
}

const cancelEdit = () => {
  isEditMode.value = false
  if (currentMenu.value) {
    Object.assign(menuForm, currentMenu.value)
  }
}

const handleDelete = async () => {
  if (!currentMenu.value) return
  
  try {
    await ElMessageBox.confirm('确定要删除该菜单吗？', '提示', {
      type: 'warning'
    })
    
    const response = await deleteMenu(currentMenu.value.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      currentMenu.value = null
      await loadMenuTree()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getMenuIcon = (type, icon) => {
  if (icon) return icon
  const iconMap = { 'M': 'el-icon-folder', 'C': 'el-icon-document' }
  return iconMap[type] || 'el-icon-menu'
}

const getMenuTypeLabel = (type) => {
  const typeMap = { 'M': '一级菜单', 'C': '二级菜单' }
  return typeMap[type] || type
}

const getMenuTypeTagType = (type) => {
  const typeMap = { 'M': 'primary', 'C': 'success' }
  return typeMap[type] || 'info'
}

// 根据ID查找菜单
const findMenuById = (menus, id) => {
  for (const menu of menus) {
    if (menu.id === id) {
      return menu
    }
    if (menu.children && menu.children.length > 0) {
      const found = findMenuById(menu.children, id)
      if (found) return found
    }
  }
  return null
}

// 根据ID查找菜单名称
const getParentMenuName = (parentId) => {
  if (!parentId) return '无上级菜单'
  const parentMenu = findMenuById(menuTree.value, parentId)
  return parentMenu ? parentMenu.menuName : '未知上级菜单'
}

// 获取菜单的完整路径
const getMenuPath = (menuId) => {
  const menu = findMenuById(menuTree.value, menuId)
  if (!menu) return '未知路径'

  const path = []
  let currentMenu = menu
  while (currentMenu) {
    path.unshift(currentMenu.menuName)
    currentMenu = findMenuById(menuTree.value, currentMenu.parentId)
  }
  return path.join(' / ')
}

// 检查是否会造成循环引用
const checkCircularReference = (menuId, parentId) => {
  // 如果没有选择上级菜单，不是循环引用
  if (!parentId) return false
  
  // 如果选择自己作为上级菜单，是循环引用
  if (menuId === parentId) return true
  
  const parentMenu = findMenuById(menuTree.value, parentId)
  if (!parentMenu) return false
  
  // 检查父菜单是否是当前菜单的子菜单
  return isChildOf(parentMenu.id, menuId)
}

// 检查menuId是否是parentId的子菜单
const isChildOf = (menuId, parentId) => {
  const parentMenu = findMenuById(menuTree.value, parentId)
  if (!parentMenu || !parentMenu.children) return false
  
  for (const child of parentMenu.children) {
    if (child.id === menuId) return true
    if (isChildOf(menuId, child.id)) return true
  }
  return false
}

// 过滤出只包含一级菜单的树形数据，用于上级菜单选择
const parentMenuTree = computed(() => {
  return menuTree.value.filter(menu => menu.menuType === 'M')
})

// 监听菜单类型变化
watch(() => menuForm.menuType, (newType) => {
  if (newType === 'M') {
    // 如果改为一级菜单，清空上级菜单和组件路径
    menuForm.parentId = null
    menuForm.component = ''
  } else if (newType === 'C') {
    // 如果改为二级菜单，确保有上级菜单
    if (!menuForm.parentId) {
      // 如果没有上级菜单，尝试自动选择当前选中的菜单作为上级（如果它是一级菜单）
      if (currentMenu.value && currentMenu.value.menuType === 'M') {
        menuForm.parentId = currentMenu.value.id
      }
    }
  }
})

// 监听新增菜单类型变化
watch(() => addForm.menuType, (newType) => {
  if (newType === 'M') {
    addForm.parentId = null
    addForm.component = ''
  } else if (newType === 'C') {
    if (!addForm.parentId) {
      if (currentMenu.value && currentMenu.value.menuType === 'M') {
        addForm.parentId = currentMenu.value.id
      }
    }
  }
})

onMounted(() => {
  loadMenuTree()
})
</script>

<style scoped>
.menu-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.menu-content {
  display: flex;
  gap: 20px;
}

.menu-tree-panel {
  width: 300px;
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.menu-tree-panel h3 {
  margin: 0 0 20px 0;
  color: #303133;
  flex-shrink: 0;
}

.tree-container {
  flex: 1;
  overflow-y: auto;
  max-height: calc(100vh - 200px);
}



.menu-detail-panel {
  flex: 1;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.detail-content {
  padding: 20px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.detail-header h3 {
  margin: 0;
  color: #303133;
}

.menu-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.menu-info h3 {
  margin: 0;
  color: #303133;
}

.menu-meta {
  margin: 0;
}

.actions {
  display: flex;
  gap: 10px;
}

.empty-state {
  padding: 60px;
  text-align: center;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tree-node i {
  color: #606266;
}

.tree-node .el-tag {
  margin-left: auto;
}

:deep(.el-tree-node__content) {
  height: 40px;
  border-radius: 4px;
  margin: 2px 0;
}

:deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa;
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: #ecf5ff;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-button) {
  border-radius: 6px;
}

:deep(.el-input__inner) {
  border-radius: 4px;
}

/* 滚动条样式 */
.tree-container::-webkit-scrollbar {
  width: 6px;
}

.tree-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.tree-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.tree-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 表单提示样式 */
.form-tip {
  margin-top: 4px;
}

/* 上级菜单选择器样式 */
:deep(.el-tree-select) {
  width: 100%;
}

:deep(.el-tree-select .el-input__inner) {
  border-radius: 4px;
}

/* 菜单类型切换时的过渡效果 */
.el-form-item {
  transition: all 0.3s ease;
}

/* 对话框提示样式 */
.dialog-tip {
  margin-bottom: 16px;
  padding: 8px 12px;
  background-color: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: 4px;
}
</style> 