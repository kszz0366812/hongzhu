<template>
  <el-card>
    <h2>菜单调试页面</h2>
    
    <div class="debug-section">
      <h3>用户信息</h3>
      <pre>{{ JSON.stringify(userStore.userInfo, null, 2) }}</pre>
    </div>
    
    <div class="debug-section">
      <h3>菜单数据</h3>
      <pre>{{ JSON.stringify(userStore.menus, null, 2) }}</pre>
    </div>
    
    <div class="debug-section">
      <h3>顶部菜单</h3>
      <pre>{{ JSON.stringify(topMenus, null, 2) }}</pre>
    </div>
    
    <div class="debug-section">
      <h3>当前激活的顶部菜单</h3>
      <p>activeTopMenu: {{ activeTopMenu }}</p>
    </div>
    
    <div class="debug-section">
      <h3>当前菜单列表</h3>
      <pre>{{ JSON.stringify(currentMenuList, null, 2) }}</pre>
    </div>
    
    <div class="debug-section">
      <h3>项目管理菜单详情</h3>
      <pre>{{ JSON.stringify(projectMenu, null, 2) }}</pre>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 顶部菜单
const topMenus = computed(() => {
  if (!userStore.userInfo?.menus) return [];
  return userStore.userInfo.menus.filter(menu => menu.menuType === 'M' && menu.visible === 1);
});

// 当前激活的顶部菜单
const activeTopMenu = computed(() => {
  return 'project'; // 强制设置为项目管理
});

// 当前菜单列表
const currentMenuList = computed(() => {
  const currentTopMenu = topMenus.value.find(menu => menu.path === activeTopMenu.value);
  if (!currentTopMenu?.children) return [];
  return currentTopMenu.children.filter(child => child.visible === 1);
});

// 项目管理菜单详情
const projectMenu = computed(() => {
  return topMenus.value.find(menu => menu.path === 'project');
});
</script>

<style scoped>
.debug-section {
  margin-bottom: 20px;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.debug-section h3 {
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
