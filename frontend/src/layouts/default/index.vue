<template>
  <div class="layout-root">
    <header class="header-bar-unified">
      <div class="header-logo-area">
        <div class="logo-icon">经</div>
        <span class="logo-title">经营数据分析系统</span>
      </div>
      <el-menu
        mode="horizontal"
        :default-active="activeTopMenu"
        class="top-menu"
        background-color="transparent"
        text-color="#fff"
        active-text-color="#409EFF"
        @select="handleTopMenuSelect"
        style="flex:1; margin-left: 32px; border-bottom: none; background: transparent;"
      >
        <el-menu-item 
          v-for="menu in topMenus" 
          :key="menu.id" 
          :index="menu.path"
        >
          {{ menu.menuName }}
        </el-menu-item>
      </el-menu>
      <div class="header-center">
        <span class="system-time">{{ currentTime }}</span>
      </div>
      <div class="header-right" style="margin-right: 40px;">
        <el-dropdown v-if="userStore.userInfo" trigger="click" @command="handleCommand">
          <span class="user-info user-info-flex">
            <span class="avatar-double-circle">
              <span class="avatar-outer">
                <span class="avatar-inner">
                  <!-- 有头像时显示头像，无头像时显示姓名首字母 -->
                  <img 
                    v-if="userStore.userInfo?.avatarUrl && userStore.userInfo.avatarUrl.trim() !== ''"
                    :src="userStore.userInfo.avatarUrl" 
                    :alt="userStore.userInfo?.realName"
                    class="header-avatar-image"
                    @error="handleHeaderAvatarError"
                  />
                  <span v-else>{{ userStore.userInfo?.realName?.charAt(0) || '' }}</span>
                </span>
              </span>
            </span>
            <span class="user-realname">{{ userStore.userInfo?.realName }}</span>
            <el-icon><CaretBottom /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="password">修改密码</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>
    <div class="main-flex">
      <transition name="aside-zoom">
        <aside
          v-show="true"
          :class="['mech-aside', isCollapse ? 'collapsed' : 'expanded']"
          :style="{ width: asideWidth + 'px', height: (!isCollapse ? (currentMenuList.length * menuItemHeight + (currentMenuList.length - 1) * menuItemGap + 180) : asideHeight) + 'px' }"
          @click="isCollapse && toggleAside()"
          :key="activeTopMenu"
        >
          <div class="mech-aside-inner">
            <div class="mech-menu-list-wrapper">
              <div class="mech-menu-list">
                <template v-if="!isCollapse">
                  <div
                    v-for="item in currentMenuList"
                    :key="item.id"
                    :class="['mech-menu-item', { active: isMenuActive(item) }]"
                    @click="handleMenuClick(item)"
                  >
                    {{ item.menuName }}
                  </div>
                </template>
                <template v-else>
                  <div class="mech-menu-item collapsed-item active">
                    <span class="collapsed-text">{{ currentMenuLabel }}</span>
                  </div>
                </template>
              </div>
            </div>
            <div v-if="!isCollapse" class="mech-toggle" :class="{ collapsed: isCollapse, expanded: !isCollapse }" @click.stop="toggleAside">
              <div class="mech-toggle-lines">
                <div class="mech-toggle-line left" :style="isCollapse ? 'height:28px' : 'height:16px'"></div>
                <div class="mech-toggle-line right" :style="isCollapse ? 'height:16px' : 'height:28px'"></div>
              </div>
            </div>
          </div>
        </aside>
      </transition>
      <transition name="main-zoom">
        <div class="main-content" :class="{ 'collapsed': isCollapse, 'expanded': !isCollapse }" >
          <el-container>
            <el-main class="main-bg">
              <div class="custom-breadcrumb">
                <span class="breadcrumb-top">{{ getCurrentTopMenuLabel() }}</span>
                <span v-if="currentMenuLabel"> - {{ currentMenuLabel }}</span>
              </div>
              <el-card class="content-card" ref="contentCardRef">
                <router-view v-slot="{ Component }">
                  <transition name="fade" mode="out-in">
                    <component :is="Component" />
                  </transition>
                </router-view>
              </el-card>
            </el-main>
          </el-container>
        </div>
      </transition>
    </div>
    
    <!-- 修改密码弹窗 -->
    <ChangePasswordDialog
      v-model:visible="passwordDialogVisible"
      @success="handlePasswordChangeSuccess"
    />
    
    <!-- 个人信息弹窗 -->
    <ProfileDialog
      v-model:visible="profileDialogVisible"
      @success="handleProfileUpdateSuccess"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessageBox } from 'element-plus';
import { useUserStore } from '@/stores/user';
import { watch } from 'vue';
import { ArrowLeft, ArrowRight, CaretBottom } from '@element-plus/icons-vue';
import ChangePasswordDialog from '@/components/ChangePasswordDialog.vue';
import ProfileDialog from '@/components/ProfileDialog.vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const isCollapse = ref(false);
const passwordDialogVisible = ref(false);
const profileDialogVisible = ref(false);

// 从用户信息中获取菜单数据
const topMenus = computed(() => {
  if (!userStore.userInfo?.menus) return [];
  return userStore.userInfo.menus.filter(menu => menu.menuType === 'M' && menu.visible === 1);
});

// 获取当前激活的顶部菜单
const activeTopMenu = computed(() => {
  const currentPath = route.path;
  const found = topMenus.value.find(menu => {
    if (menu.path === 'dashboard' && (currentPath === '/portal' || currentPath === '/')) {
      return true;
    }
    return currentPath.startsWith(`/portal/${menu.path}`);
  });
  return found ? found.path : 'dashboard';
});

// 获取当前顶部菜单的子菜单
const currentMenuList = computed(() => {
  const currentTopMenu = topMenus.value.find(menu => menu.path === activeTopMenu.value);
  if (!currentTopMenu?.children) return [];
  return currentTopMenu.children.filter(child => child.visible === 1);
});

// 获取当前菜单标签
const currentMenuLabel = computed(() => {
  const currentPath = route.path;
  const found = currentMenuList.value.find(item => {
    // 检查当前路径是否匹配子菜单
    return currentPath.includes(item.path);
  });
  return found ? found.menuName : (currentMenuList.value[0]?.menuName || '');
});

// 获取当前顶部菜单标签
const getCurrentTopMenuLabel = () => {
  const found = topMenus.value.find(menu => menu.path === activeTopMenu.value);
  return found ? found.menuName : '首页';
};

// 根据路径获取顶部菜单
function getTopMenuByPath(path) {
  if (path === '/portal' || path === '/') return 'dashboard';
  
  const found = topMenus.value.find(menu => {
    if (menu.path === 'dashboard') return false;
    return path.startsWith(`/portal/${menu.path}`);
  });
  
  return found ? found.path : 'dashboard';
}

const activeMenu = computed(() => route.path);
const currentTime = ref('');
let timer = null;
const contentCardRef = ref(null);

// 安全获取 innerWidth，避免 SSR 或 undefined 报错
const safeInnerWidth = computed(() => (typeof window !== 'undefined' && window.innerWidth) ? window.innerWidth : 1920);

function handleTopMenuSelect(menuPath) {
  console.log('Top menu selected:', menuPath);
  
  // 跳转到一级菜单默认页
  if (menuPath === 'dashboard') {
    router.push('/portal');
  } else {
    const menu = topMenus.value.find(m => m.path === menuPath);
    if (menu?.children && menu.children.length > 0) {
      const firstChild = menu.children[0];
      router.push(`/portal/${menuPath}/${firstChild.path}`);
    } else {
      router.push(`/portal/${menuPath}`);
    }
  }
}

onMounted(() => {
  updateTime();
  timer = setInterval(updateTime, 1000);
  setContentCardHeight();
  window.addEventListener('resize', setContentCardHeight);
});

onUnmounted(() => {
  clearInterval(timer);
  window.removeEventListener('resize', setContentCardHeight);
});

// 监听路由变化，自动切换顶部菜单
watch(() => route.path, (val) => {
  // 路由变化时不需要手动设置activeTopMenu，computed会自动处理
});

const updateTime = () => {
  const now = new Date();
  currentTime.value = now.toLocaleString();
};

function setContentCardHeight() {
  nextTick(() => {
    const mainBg = document.querySelector('.main-bg');
    const cardDom = contentCardRef.value?.$el || contentCardRef.value;
    if (mainBg && cardDom && cardDom.getBoundingClientRect) {
      const mainBgRect = mainBg.getBoundingClientRect();
      const cardTop = cardDom.getBoundingClientRect().top;
      const windowHeight = window.innerHeight;
      // 距离底部20px
      const cardHeight = windowHeight - cardTop - 50;
      cardDom.style.height = cardHeight > 0 ? cardHeight + 'px' : '0px';
    }
  });
}

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      profileDialogVisible.value = true;
      break;
    case 'password':
      passwordDialogVisible.value = true;
      break;
    case 'logout':
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      });
      userStore.logoutAction();
      router.push('/');
      break;
  }
};

// 处理密码修改成功
const handlePasswordChangeSuccess = () => {
  // 密码修改成功后，清除用户信息并跳转到登录页
  userStore.logoutAction();
  router.push('/login');
};

// 处理个人信息更新成功
const handleProfileUpdateSuccess = () => {
  // 个人信息更新成功后的处理
  console.log('个人信息更新成功');
};

function handleMenuClick(menuItem) {
  console.log('Side menu clicked:', menuItem);
  
  // 构建完整路径
  let targetPath = '';
  
  if (activeTopMenu.value === 'dashboard') {
    // 首页菜单特殊处理
    if (menuItem.path === 'bigscreen') {
      targetPath = '/portal/bigscreen';
    } else {
      targetPath = '/portal';
    }
  } else {
    // 其他菜单构建完整路径
    targetPath = `/portal/${activeTopMenu.value}/${menuItem.path}`;
  }
  
  
  router.push(targetPath);
}

function toggleAside() {
  isCollapse.value = !isCollapse.value;
}

const expandedWidth = 100;
const collapsedWidth = 32;
const menuItemHeight = 32;
const menuItemGap = 18;
const asidePadding = 24;
const toggleBtnHeight = 48;
const currentMenuCount = computed(() => currentMenuList.value.length || 1);
const asideHeight = computed(() => {
  if (isCollapse.value) {
    return (currentMenuCount.value * menuItemHeight + (currentMenuCount.value - 1) * menuItemGap + asidePadding * 2 + toggleBtnHeight);
  } else {
    return 45 * window.innerHeight / 100;
  }
});
const asideWidth = computed(() => isCollapse.value ? collapsedWidth : expandedWidth);

function isMenuActive(item) {
  const currentPath = route.path;
  if (activeTopMenu.value === 'dashboard') {
    if (item.path === 'bigscreen') {
      return currentPath === '/portal/bigscreen';
    } else {
      return currentPath === '/portal';
    }
  } else {
    return currentPath.startsWith(`/portal/${activeTopMenu.value}/${item.path}`);
  }
}

// 处理头像加载失败
const handleHeaderAvatarError = (event) => {
  event.target.src = ''; // 清空src，显示默认文字
  event.target.alt = userStore.userInfo?.realName?.charAt(0) || ''; // 设置alt为默认文字
};
</script>

<style>
/* 全局重置 */
* {
  box-sizing: border-box;
}

html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow: hidden;
}

#app {
  height: 100%;
  overflow: hidden;
}
</style>

<style scoped>
.layout-root {
  width: 100vw;
  min-height: 100vh;
  height: 100vh;
  background: #f5f7fa;
  margin: 0;
  padding: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.header-bar-unified {
  width: 100%;
  background: linear-gradient(90deg, #409EFF 0%, #2a3650 100%);
  border-bottom: none;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 0 24px;
  height: 64px;
  box-shadow: 0 2px 8px #22304a11;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 200;
  transition: none !important;
}
.top-menu {
  background: transparent;
  border-bottom: none;
  margin-left: 32px;
  flex: 1;
  min-width: 700px;
  transition: none !important;
}
.top-menu .el-menu-item {
  font-size: 16px;
  font-weight: 500;
  padding: 0 24px;
  height: 64px;
  line-height: 64px;
  transition: color 0.2s, background 0.2s, box-shadow 0.2s;
  border-radius: 0;
}
.top-menu .el-menu-item.is-active {
  color: #fff !important;
  background: linear-gradient(90deg, #409EFF 60%, #66b1ff 100%);
  box-shadow: 0 2px 8px #409eff22;
  font-weight: bold;
}
.top-menu .el-menu-item:hover {
  color: #fff;
  background: #409EFF;
}
.header-center {
  flex: none;
  text-align: center;
  color: #fff;
  font-size: 15px;
  font-weight: 500;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: 40px;
}
.main-flex {
  display: flex;
  flex: 1;
  height: calc(100vh - 64px);
  margin-top: 64px;
  overflow: hidden;
}
.mech-aside {
  min-width: 20px;
  margin: auto 0;
  background: linear-gradient(120deg, #e3f0fc 0%, #c9e0f7 100%);
  border-radius: 8px 20px 8px 8px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: stretch;
  position: relative;
  clip-path: polygon(0 0, 100% 18%, 100% 82%, 0 100%);
  transition: width 0.35s cubic-bezier(.4,0,.2,1), height 0.35s cubic-bezier(.4,0,.2,1), clip-path 0.35s, box-shadow 0.35s;
  z-index: 10;
}
.mech-aside.expanded {
  border-radius: 8px 20px 8px 8px;
}
.mech-aside.collapsed {
  border-radius: 8px 10px 8px 8px;
}
.mech-aside-inner {
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: center;
  align-items: stretch;
  position: relative;
}
.mech-menu-list-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
}
.mech-menu-list {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: stretch;
  gap: 18px;
  margin: 0 0 0 0;
}
.mech-menu-item {
  background: rgba(255,255,255,0.7);
  color: #3a4a5a;
  font-size: 0.8rem;
  font-weight: 700;
  border-radius: 6px;
  margin: 0 8px;
  padding: 4px 0;
  text-align: center;
  border: 1px solid #b3d0ee;
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
  letter-spacing: 2px;
  position: relative;
}
.mech-menu-item.active {
  background: linear-gradient(90deg, #e3f0fc 60%, #b3d0ee 100%);
  color: #1565c0;
  border: 1.5px solid #1976d2;
  box-shadow: 0 2px 8px #90caf944;
  transform: scale(1.05);
}
.mech-menu-item.collapsed-item {
  margin: 0 2px;
  padding: 4px 0;
  font-size: 1rem;
  background: #e3f0fc;
  color: #1976d2;
  writing-mode: vertical-rl;
  letter-spacing: 1px;
  border-radius: 4px;
  border: 1px solid #b3d0ee;
}
.collapsed-text {
  font-size: 1rem;
  color: #1976d2;
  font-weight: 600;
  letter-spacing: 1px;
  writing-mode: vertical-rl;
}
.mech-aside.collapsed .mech-menu-item {
  font-size: 1rem;
  padding: 4px 0;
}
.mech-toggle {
  position: absolute;
  right: -15px;
  left: auto;
  top: 50%;
  transform: translateY(-50%);
  width: 44px;
  height: 60px;
  background: transparent;
  border-radius: 0;
  border: none;
  box-shadow: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 20;
  transition: right 0.35s;
}
.mech-toggle.collapsed {
  right: -14px;
  left: auto;
}
.mech-toggle-lines {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  height: 32px;
  width: 24px;
}
.mech-toggle-line {
  width: 4px;
  border-radius: 2px;
  margin: 0 2px;
  background: #fff;
  transition: height 0.35s, background 0.2s;
}
.main-content {
  flex: 1;
  min-width: 0;
  height: 100%;
  transition: margin-left 0.35s cubic-bezier(.4,0,.2,1), width 0.35s cubic-bezier(.4,0,.2,1), transform 0.35s cubic-bezier(.4,0,.2,1);
  margin-left: 0;
  will-change: margin-left, width, transform;
}
.main-content.expanded {
  margin-left: 0;
  width: calc(100vw - 100px);
  transform: none;
}
.main-content.collapsed {
  margin-left: 0;
  width: calc(100vw - 32px);
  transform: scale(1.04);
}
/* 动效 */
.aside-zoom-enter-active, .aside-zoom-leave-active,
.main-zoom-enter-active, .main-zoom-leave-active {
  transition: all 0.35s cubic-bezier(.4,0,.2,1);
}
.aside-zoom-enter-from, .aside-zoom-leave-to,
.main-zoom-enter-from, .main-zoom-leave-to {
  opacity: 0.7;
  transform: scale(0.98);
}
.header-logo-area {
  display: flex;
  align-items: center;
  height: 72px;
}
.logo-icon {
  width: 44px;
  height: 44px;
  background: #fff;
  color: #409EFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  font-weight: bold;
  margin-left: 0;
  margin-right: 12px;
  box-shadow: 0 2px 8px #409eff33;
}
.logo-title {
  font-size: 22px;
  font-weight: 900;
  color: #fff;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px #409eff44;
  transition: none !important;
  display: inline;
  writing-mode: unset;
  height: auto;
  margin-left: 0;
  margin-right: 0;
  line-height: normal;
}
.main-bg {
  background: #f7fafd;
  height: 100%;
  padding: 24px 24px 0 24px;
  overflow: hidden;
}
.content-card {
  border-radius: 18px;
  box-shadow: 0 4px 24px #22304a11;
  border: none;
  padding: 20px 20px 8px 20px;
  height: 100%;
  background: #fff;
  overflow: auto;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
@media (max-width: 900px) {
  .aside { width: 48px !important; }
  .aside-collapsed { width: 48px !important; }
  .menu .el-menu-item { width: 48px; }
  .logo-title { display: none; }
  .header-bar-unified { padding: 0 8px; }
  .main-bg { padding: 8px; }
  .content-card { padding: 8px; }
  .layout-container { margin-top: 64px; }
}
.el-container {
  height: 100%;
  flex: 1;
  overflow: hidden;
}
.el-main {
  height: 100%;
  overflow: hidden;
  background: transparent;
}
.submenu-fade-slide-enter-active, .submenu-fade-slide-leave-active {
  transition: all 0.35s cubic-bezier(.4,0,.2,1);
}
.submenu-fade-slide-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}
.submenu-fade-slide-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
.custom-breadcrumb {
  font-size: 16px;
  font-weight: 600;
  color: #1976d2;
  margin-bottom: 12px;
  margin-left: 2px;
  letter-spacing: 1px;
}
.breadcrumb-top {
  color: #1565c0;
}
.user-info-flex {
  display: flex;
  align-items: center;
  gap: 10px;
}
.user-realname {
  font-size: 12px;
  color: #fff;
  font-weight: 500;
  margin-right: 2px;
  letter-spacing: 1px;
  display: flex;
  align-items: center;
  height: 48px;
}
.avatar-double-circle {
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar-outer {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF 60%, #66b1ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px #409eff33;
}
.avatar-inner {
  width: 30px;
  height:30px;
  border-radius: 50%;
  background: #fff;
  color: #409EFF;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
  letter-spacing: 2px;
  box-shadow: 0 2px 8px #409eff22;
}
.header-avatar-image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}
</style> 