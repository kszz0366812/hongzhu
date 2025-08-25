import { defineStore } from 'pinia';
import { ref, toHandlerKey } from 'vue';
import { login, logout, getUserInfo } from '@/api/auth';
import { ElMessage } from 'element-plus';
import { addDynamicRoutes } from '@/router';

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null
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
        
        // 将用户信息存储到localStorage中，供路由配置使用
        localStorage.setItem('userStore', JSON.stringify({ userInfo: this.userInfo }));
        
        // 获取用户信息后，添加动态路由
        if (this.userInfo?.menus) {
          addDynamicRoutes();
        }
      } catch (e) {
        this.userInfo = null;
        localStorage.removeItem('userStore');
      }
    },
    async logoutAction() {
      localStorage.removeItem('token');
      localStorage.removeItem('userStore');
      this.userInfo = null;
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