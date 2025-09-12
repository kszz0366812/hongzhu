import { createRouter, createWebHistory, RouterView } from 'vue-router'

// 路径验证和清理函数
function sanitizeComponentPath(componentPath) {
  if (!componentPath || typeof componentPath !== 'string') {
    return null;
  }
  
  // 移除可能的文件扩展名
  let cleanPath = componentPath.replace(/\.vue$/, '');
  
  // 移除开头的斜杠
  cleanPath = cleanPath.replace(/^\/+/, '');
  
  // 移除结尾的斜杠
  cleanPath = cleanPath.replace(/\/+$/, '');
  
  return cleanPath;
}

// 生成组件导入路径
function generateImportPath(componentPath) {
  const sanitizedPath = sanitizeComponentPath(componentPath);
  if (!sanitizedPath) {
    return null;
  }
  
  // 构建相对路径
  return `../views/${sanitizedPath}.vue`;
}

// 生成错误组件
function createErrorComponent(componentPath, error) {
  return {
    template: `
      <div style="padding: 40px; text-align: center; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;">
        <div style="max-width: 500px; margin: 0 auto; background: #fef0f0; border: 1px solid #fde2e2; border-radius: 8px; padding: 24px;">
          <div style="color: #f56c6c; font-size: 48px; margin-bottom: 16px;">⚠️</div>
          <h3 style="color: #f56c6c; margin: 0 0 16px 0; font-size: 18px;">组件加载失败</h3>
          <div style="background: #fff; border-radius: 4px; padding: 16px; margin: 16px 0; text-align: left; font-family: monospace; font-size: 14px;">
            <div><strong>组件路径:</strong> ${componentPath}</div>
            <div><strong>错误信息:</strong> ${error?.message || '未知错误'}</div>
          </div>
          <p style="color: #666; margin: 16px 0 0 0; font-size: 14px;">
            请检查数据库中的component字段配置是否正确，或联系系统管理员
          </p>
        </div>
      </div>
    `
  };
}

// 基础路由
const baseRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/',
    component: () => import('@/views/dashboard/BigScreen.vue')
  },
  {
    path: '/portal',
    name: 'portal',
    component: () => import('@/layouts/default/index.vue'),
    children: [
      // 门户首页
      {
        path: '',
        name: 'portal-home',
        component: () => import('@/views/dashboard/index.vue')
      },
      // 数据大屏
      {
        path: 'bigscreen',
        name: 'bigscreen',
        component: () => import('@/views/dashboard/BigScreen.vue')
      }
    ]
  }
]

// 动态生成路由的函数
function generateDynamicRoutes(menus) {
  const dynamicRoutes = []
  
  if (!menus || !Array.isArray(menus)) {
    console.log('generateDynamicRoutes: 无效的菜单数据', menus);
    return dynamicRoutes
  }
  
  console.log(`generateDynamicRoutes: 开始处理 ${menus.length} 个菜单项`);
  
  menus.forEach((menu, index) => {
    if (menu.menuType === 'M' && menu.visible === 1) {
      console.log(`处理菜单 ${index + 1}: ${menu.name || menu.path}`);
      
      const route = {
        path: menu.path,
        name: menu.path,
        component: RouterView,
        children: []
      }
      
      // 添加子菜单路由
      if (menu.children && Array.isArray(menu.children)) {
        console.log(`菜单 ${menu.name || menu.path} 有 ${menu.children.length} 个子项`);
        
        menu.children.forEach((child, childIndex) => {
          if (child.visible === 1 && child.component) {
            // 验证组件路径
            const componentPath = child.component;
            const importPath = generateImportPath(componentPath);
            
            if (!importPath) {
              console.warn(`无效的组件路径: ${componentPath}`, child);
              return;
            }
            
            console.log(`添加子路由 ${childIndex + 1}: ${child.path} -> ${importPath}`);
            
            const childRoute = {
              path: child.path,
              name: `${menu.path}-${child.path}`,
              component: () => {
                try {
                  // 使用 vite-ignore 抑制动态导入警告
                  return import(/* @vite-ignore */ importPath);
                } catch (error) {
                  console.error('组件导入失败:', importPath, error);
                  // 尝试使用原始路径
                  try {
                    const originalPath = generateImportPath(componentPath);
                    console.log('尝试使用原始路径:', originalPath);
                    // 使用 vite-ignore 抑制动态导入警告
                    return import(/* @vite-ignore */ originalPath);
                  } catch (origError) {
                    console.error('原始路径也失败:', originalPath, origError);
                    // 返回一个默认的错误提示组件
                    return Promise.resolve(createErrorComponent(componentPath, origError));
                  }
                }
              }
            }
            route.children.push(childRoute)
          }
        })
      }
      
      // 如果有子菜单，添加默认重定向
      if (route.children.length > 0) {
        route.children.unshift({
          path: '',
          name: `${menu.path}-redirect`,
          redirect: route.children[0].path
        })
        console.log(`菜单 ${menu.name || menu.path} 添加了 ${route.children.length} 个路由`);
      }
      
      dynamicRoutes.push(route)
    }
  })
  
  console.log(`generateDynamicRoutes: 成功生成 ${dynamicRoutes.length} 个动态路由`);
  return dynamicRoutes
}

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes: baseRoutes
})

// 路由守卫：确保动态路由已添加
router.beforeEach(async (to, from, next) => {
  console.log('路由守卫 - 目标路径:', to.path);
  
  // 如果是登录页面或默认页面（根路径），直接放行
  if (to.path === '/login' || to.path === '/') {
    console.log('路由守卫 - 允许访问:', to.path);
    next()
    return
  }
  
  // 检查是否有token，如果没有则跳转到登录页
  const token = localStorage.getItem('token')
  if (!token && to.path !== '/login') {
    console.log('路由守卫 - 无token，跳转登录页');
    next('/login')
    return
  }
  
  // 检查是否需要添加动态路由（使用localStorage中的菜单信息）
  const userStore = JSON.parse(localStorage.getItem('userStore') || '{}')
  const menus = userStore.menus || userStore.userInfo?.menus
  
  if (menus && menus.length > 0) {
    // 检查目标路由是否已经存在
    const targetRoute = router.getRoutes().find(route => route.path === to.path)
    
    if (!targetRoute && to.path.startsWith('/portal/')) {
      // 动态路由还没有添加，先添加动态路由
      console.log('Adding dynamic routes before navigation...')
      addDynamicRoutes()
      
      // 等待路由添加完成
      await new Promise(resolve => setTimeout(resolve, 100))
      
      // 重新解析路由
      const resolvedRoute = router.resolve(to.path)
      if (resolvedRoute.matched.length > 0) {
        // 路由存在，重新导航
        next({ ...to, replace: true })
        return
      } else {
        // 路由仍然不存在，可能是路径错误
        console.error('Route not found after adding dynamic routes:', to.path)
        // 跳转到门户首页
        next('/portal')
        return
      }
    }
  } else if (to.path.startsWith('/portal/') && to.path !== '/portal') {
    // 没有菜单信息，跳转到门户首页
    console.warn('No menu information available, redirecting to portal')
    next('/portal')
    return
  }
  
  console.log('路由守卫 - 正常放行:', to.path);
  next()
})

// 路由调试函数：显示当前所有路由信息
export function debugRoutes() {
  const routes = router.getRoutes()
  console.log('=== 当前所有路由 ===')
  routes.forEach(route => {
    console.log(`路径: ${route.path}, 名称: ${route.name}, 组件: ${route.component}`)
    if (route.children && route.children.length > 0) {
      console.log(`  子路由: ${route.children.length} 个`)
      route.children.forEach(child => {
        console.log(`    - ${child.path}: ${child.name}`)
      })
    }
  })
  console.log('====================')
}

// 路由恢复函数：用于页面刷新时恢复动态路由
export function restoreDynamicRoutes() {
  try {
    const userStore = JSON.parse(localStorage.getItem('userStore') || '{}')
    const menus = userStore.menus || userStore.userInfo?.menus
    
    if (menus && menus.length > 0) {
      console.log('Restoring dynamic routes from localStorage...')
      addDynamicRoutes()
      return true
    }
    return false
  } catch (error) {
    console.error('Failed to restore dynamic routes:', error)
    return false
  }
}

// 动态添加路由的函数
export function addDynamicRoutes() {
  try {
    // 获取用户store中的菜单信息
    const userStore = JSON.parse(localStorage.getItem('userStore') || '{}')
    const menus = userStore.menus || userStore.userInfo?.menus
    
    if (!menus || !Array.isArray(menus)) {
      console.log('addDynamicRoutes: 无效的菜单数据', menus);
      return;
    }
    
    console.log('Adding dynamic routes for menus:', menus);
    
    const dynamicRoutes = generateDynamicRoutes(menus)
    console.log('Generated dynamic routes:', dynamicRoutes);
    
    // 找到portal路由，添加动态子路由
    const portalRoute = router.getRoutes().find(route => route.name === 'portal')
    if (portalRoute) {
      dynamicRoutes.forEach(route => {
        // 检查路由是否已存在
        const existingRoute = router.getRoutes().find(r => r.path === `/portal/${route.path}`)
        if (!existingRoute) {
          console.log('Adding dynamic route:', route.path, route)
          try {
            router.addRoute('portal', route)
            console.log('Successfully added route:', route.path)
          } catch (error) {
            console.error('Failed to add route:', route.path, error)
          }
        } else {
          console.log('Route already exists:', route.path)
        }
      })
    } else {
      console.error('Portal route not found')
    }
  } catch (error) {
    console.error('Error adding dynamic routes:', error)
  }
}

export default router 