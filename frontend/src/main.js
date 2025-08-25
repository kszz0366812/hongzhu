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
  if (token) {
    try {
      await userStore.getUserInfoAction();
    } catch (error) {
      console.error('Failed to get user info:', error);
    }
  }
  
  // 无论是否有token，都挂载应用
  app.mount('#app');
}

initializeApp(); 