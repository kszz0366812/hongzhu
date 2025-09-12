import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import zhCn from 'element-plus/dist/locale/zh-cn.mjs';
import { useUserStore } from '@/stores/user';
import $ from 'jquery';
window.$ = window.jQuery = $;

const app = createApp(App);
app.use(createPinia());
app.use(router);
app.use(ElementPlus, { locale: zhCn });

// 自动拉取用户信息并挂载应用
const userStore = useUserStore();
const token = localStorage.getItem('token');

async function initializeApp() {
  console.log('应用初始化开始');
  console.log('当前token状态:', token ? '有token' : '无token');
  
  // 无论是否有token，都尝试恢复路由（可能是页面刷新）
  try {
    const { restoreDynamicRoutes } = await import('@/router');
    const restored = restoreDynamicRoutes();
    console.log('路由恢复结果:', restored);
  } catch (error) {
    console.error('Failed to restore routes:', error);
  }
  
  // 只有在有token的情况下才尝试初始化用户信息
  if (token) {
    try {
      console.log('尝试初始化用户信息...');
      await userStore.initializeUserInfo();
      console.log('用户信息初始化成功');
    } catch (error) {
      console.error('Failed to initialize user info:', error);
      // 如果初始化用户信息失败，清除token
      localStorage.removeItem('token');
      localStorage.removeItem('userStore');
      console.log('已清除无效token和用户信息');
      // 跳转到登录页
      window.location.href = '/login';
      return;
    }
  } else {
    console.log('无token，跳过用户信息初始化');
  }
  
  // 无论是否有token，都挂载应用
  console.log('开始挂载应用...');
  try {
    app.mount('#app');
    console.log('应用挂载完成');
  } catch (error) {
    console.error('应用挂载失败:', error);
  }
}

initializeApp(); 