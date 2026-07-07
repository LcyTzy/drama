import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false, title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: '/employee',
        name: 'Employee',
        component: () => import('@/views/EmployeeList.vue'),
        meta: { title: '员工管理' }
      },
      {
        path: '/category',
        name: 'Category',
        component: () => import('@/views/CategoryList.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: '/drama',
        name: 'Drama',
        component: () => import('@/views/DramaList.vue'),
        meta: { title: '短剧管理' }
      },
      {
        path: '/episode',
        name: 'Episode',
        component: () => import('@/views/EpisodeList.vue'),
        meta: { title: '剧集管理' }
      },
      {
        path: '/order',
        name: 'Order',
        component: () => import('@/views/OrderList.vue'),
        meta: { title: '订单管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫：校验登录状态
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  // 需要鉴权但缺少 token，跳转登录页
  if (to.meta.requiresAuth !== false && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
