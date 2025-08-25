import { createRouter, createWebHistory, RouterView } from 'vue-router'

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
    return dynamicRoutes
  }
  
  menus.forEach(menu => {
    if (menu.menuType === 'M' && menu.visible === 1) {
      const route = {
        path: menu.path,
        name: menu.path,
        component: RouterView,
        children: []
      }
      
      // 添加子菜单路由
      if (menu.children && Array.isArray(menu.children)) {
        menu.children.forEach(child => {
          if (child.visible === 1 && child.component) {
            // 直接使用数据库中的组件路径，不做映射
            const componentPath = child.component;
              
            const importPath = `../views/${componentPath}.vue`;
            const childRoute = {
              path: child.path,
              name: `${menu.path}-${child.path}`,
              component: () => {
                try {
                  return import(importPath);
                } catch (error) {
                  console.error('组件导入失败:', importPath, error);
                  // 尝试使用原始路径
                  try {
                    const originalPath = `../views/${componentPath}.vue`;
                    console.log('尝试使用原始路径:', originalPath);
                    return import(originalPath);
                  } catch (origError) {
                    console.error('原始路径也失败:', originalPath, origError);
                    // 返回一个默认的错误提示
                    return Promise.resolve({
                      template: `<div>组件加载失败: ${componentPath}<br>请检查数据库中的component字段配置</div>`
                    });
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
      }
      
      dynamicRoutes.push(route)
    }
  })
  
  return dynamicRoutes
}

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes: baseRoutes
})

// 动态添加路由的函数
export function addDynamicRoutes() {
  try {
    // 获取用户store中的菜单信息
    const userStore = JSON.parse(localStorage.getItem('userStore') || '{}')
    const menus = userStore.userInfo?.menus
    
    console.log('Adding dynamic routes for menus:', menus);
    
    if (menus) {
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
    } else {
      console.log('No menus found in user store')
    }
  } catch (error) {
    console.error('Error adding dynamic routes:', error)
  }
}

export default router 