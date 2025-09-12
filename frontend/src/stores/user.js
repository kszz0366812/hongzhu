import { defineStore } from 'pinia';
import { ref } from 'vue';
import { login, logout, getUserInfo } from '@/api/auth';
import { ElMessage } from 'element-plus';
import { addDynamicRoutes } from '@/router';

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    menus: [], // 将菜单信息存储在store中
    isInitialized: false // 标记是否已初始化
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
    
    // 初始化用户信息（优先从缓存读取）
    async initializeUserInfo() {
      if (this.isInitialized) {
        return;
      }
      
      // 尝试从localStorage恢复用户信息
      const cachedUserStore = localStorage.getItem('userStore');
      if (cachedUserStore) {
        try {
          const { userInfo, menus } = JSON.parse(cachedUserStore);
          this.userInfo = userInfo;
          this.menus = menus || [];
          this.isInitialized = true;
          
          // 恢复动态路由
          if (this.menus && this.menus.length > 0) {
            addDynamicRoutes();
          }
          
          console.log('用户信息已从缓存恢复');
          return;
        } catch (error) {
          console.error('从缓存恢复用户信息失败:', error);
          localStorage.removeItem('userStore');
        }
      }
      
      // 缓存中没有用户信息，从服务器获取
      try {
        await this.getUserInfoAction();
      } catch (error) {
        console.error('从服务器获取用户信息失败:', error);
        // 清除可能损坏的缓存
        localStorage.removeItem('userStore');
        throw error; // 重新抛出错误，让调用者处理
      }
    },
    
    // 从服务器获取用户信息（仅在必要时调用）
    async getUserInfoAction() {
      try {
        const res = await getUserInfo();
        this.userInfo = res.data;
        this.menus = res.data?.menus || [];
        this.isInitialized = true;
        
        // 将用户信息和菜单信息存储到localStorage中，供路由配置使用
        localStorage.setItem('userStore', JSON.stringify({ 
          userInfo: this.userInfo,
          menus: this.menus 
        }));
        
        // 获取用户信息后，添加动态路由
        if (this.menus && this.menus.length > 0) {
          addDynamicRoutes();
        }
        
        console.log('用户信息已从服务器获取并缓存');
      } catch (e) {
        this.userInfo = null;
        this.menus = [];
        this.isInitialized = false;
        localStorage.removeItem('userStore');
        console.error('Failed to get user info:', e);
      }
    },
    async logoutAction() {
      localStorage.removeItem('token');
      localStorage.removeItem('userStore');
      this.userInfo = null;
      this.menus = [];
      this.isInitialized = false;
    },
    
    // 更新token
    updateToken(newToken) {
      if (newToken) {
        localStorage.setItem('token', newToken);
        console.log('Token已更新');
      }
    },
    
    // 强制刷新用户信息（仅在必要时使用）
    async refreshUserInfo() {
      this.isInitialized = false;
      await this.getUserInfoAction();
    }
  }
}); 