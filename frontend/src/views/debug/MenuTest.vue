<template>
  <el-card>
    <h2>菜单测试页面</h2>
    
    <div class="test-section">
      <h3>项目管理菜单详情</h3>
      <el-button @click="testProjectMenu">测试项目管理菜单</el-button>
      <pre v-if="projectMenuData">{{ JSON.stringify(projectMenuData, null, 2) }}</pre>
    </div>
    
    <div class="test-section">
      <h3>当前激活的顶部菜单</h3>
      <p>activeTopMenu: {{ activeTopMenu }}</p>
    </div>
    
    <div class="test-section">
      <h3>当前菜单列表</h3>
      <pre>{{ JSON.stringify(currentMenuList, null, 2) }}</pre>
    </div>
    
    <div class="test-section">
      <h3>强制设置项目管理为激活菜单</h3>
      <el-button @click="forceSetProjectMenu">设置为项目管理</el-button>
    </div>
  </el-card>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const projectMenuData = ref(null)

// 顶部菜单
const topMenus = computed(() => {
  if (!userStore.userInfo?.menus) return [];
  return userStore.userInfo.menus.filter(menu => menu.menuType === 'M' && menu.visible === 1);
});

// 当前激活的顶部菜单
const activeTopMenu = ref('project')

// 当前菜单列表
const currentMenuList = computed(() => {
  const currentTopMenu = topMenus.value.find(menu => menu.path === activeTopMenu.value);
  if (!currentTopMenu?.children) return [];
  return currentTopMenu.children.filter(child => child.visible === 1);
});

// 测试项目管理菜单
function testProjectMenu() {
  const projectMenu = topMenus.value.find(menu => menu.path === 'project');
  projectMenuData.value = projectMenu;
  console.log('项目管理菜单:', projectMenu);
  console.log('子菜单:', projectMenu?.children);
}

// 强制设置项目管理为激活菜单
function forceSetProjectMenu() {
  activeTopMenu.value = 'project';
  console.log('已设置为项目管理菜单');
}
</script>

<style scoped>
.test-section {
  margin-bottom: 20px;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.test-section h3 {
  margin-top: 0;
  color: #409eff;
}

pre {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 12px;
  line-height: 1.4;
}
</style>
