import { defineStore } from 'pinia';
import { ref } from 'vue';
import { login, logout, getUserInfo } from '@/api/auth';
import { ElMessage } from 'element-plus';
import { addDynamicRoutes } from '@/router';

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    menus: [] // 将菜单信息存储在store中
  }),
  actions: {
    async loginAction(loginParams) {
      const res = await login(loginParams);
      if(res.code === 200){
        localStorage.setItem('token', res.data);
        await this.getUserInfoAction();
      }else{
        ElMessage.error(res.message)
      }
     
    },
    async getUserInfoAction() {
      try {
        const res = await getUserInfo();
        this.userInfo = res.data;
        this.menus = res.data?.menus || [];
        
        // 将用户信息和菜单信息存储到localStorage中，供路由配置使用
        localStorage.setItem('userStore', JSON.stringify({ 
          userInfo: this.userInfo,
          menus: this.menus 
        }));
        
        // 获取用户信息后，添加动态路由
        if (this.menus && this.menus.length > 0) {
          addDynamicRoutes();
        }
      } catch (e) {
        this.userInfo = null;
        this.menus = [];
        localStorage.removeItem('userStore');
        console.error('Failed to get user info:', e);
      }
    },
    async logoutAction() {
      localStorage.removeItem('token');
      localStorage.removeItem('userStore');
      this.userInfo = null;
      this.menus = [];
    },
    
    // 更新token
    updateToken(newToken) {
      if (newToken) {
        localStorage.setItem('token', newToken);
        console.log('Token已更新');
      }
    }
  }
}); 