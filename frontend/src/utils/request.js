import axios from 'axios';
import { ElMessage } from 'element-plus';
import router from '@/router';

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 15000 // 保持默认15秒
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    
    // 只对导入请求设置更长的超时时间
    if (config.url && config.url.includes('/excel/import')) {
      config.timeout = 300000; // 导入请求设置为5分钟
    }
    
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 如果是blob响应（文件下载），直接返回
    if (response.config.responseType === 'blob') {
      return response;
    }
    
    if (response.data.code === 200) {
      return response.data; 
    } else if(response.data.code === 401){
      //弹出错误信息，延时3秒后跳转登录
      ElMessage.error(response.data.message);
      setTimeout(() => {
        localStorage.removeItem('token');
        window.location.href = '/login';
      }, 3000);
    }else{
      ElMessage.error(response.data.message);
      return Promise.reject(response.data.message);
    }
  },
  error => {
    // 处理超时错误
    if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
      ElMessage.error('请求超时，请检查网络连接或稍后重试');
      return Promise.reject(new Error('请求超时'));
    }
    
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    } else {
      ElMessage.error(error.message || '请求出错');
    }
    return Promise.reject(error);
  }
);

// 封装请求方法
const request = (config) => {
  return service.request(config);
};

export default request; 