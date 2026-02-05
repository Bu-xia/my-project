import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAdminStore } from '@/stores/admin'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue')
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/auth/AdminLogin.vue')
  },
  {
    path: '/user',
    component: () => import('@/layouts/UserLayout.vue'),
    meta: { requiresAuth: true, role: 'user' },
    children: [
      {
        path: '',
        redirect: '/user/booth'
      },
      {
        path: 'booth',
        name: 'BoothSearch',
        component: () => import('@/views/user/BoothSearch.vue')
      },
      {
        path: 'apply',
        name: 'BoothApply',
        component: () => import('@/views/user/BoothApply.vue')
      },
      {
        path: 'rental',
        name: 'MyRental',
        component: () => import('@/views/user/MyRental.vue')
      },
      {
        path: 'payment',
        name: 'Payment',
        component: () => import('@/views/user/Payment.vue')
      },
      {
        path: 'ai-assistant',
        name: 'AIAssistant',
        component: () => import('@/views/user/AIAssistant.vue')
      },
      {
        path: 'notice',
        name: 'Notice',
        component: () => import('@/views/user/Notice.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue')
      }
    ]
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 'admin' },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'tenant',
        name: 'TenantManagement',
        component: () => import('@/views/admin/TenantManagement.vue')
      },
      {
        path: 'apply',
        name: 'ApplyManagement',
        component: () => import('@/views/admin/ApplyManagement.vue')
      },
      {
        path: 'contract',
        name: 'ContractManagement',
        component: () => import('@/views/admin/ContractManagement.vue')
      },
      {
        path: 'finance',
        name: 'FinanceManagement',
        component: () => import('@/views/admin/FinanceManagement.vue')
      },
      {
        path: 'notice',
        name: 'NoticeManagement',
        component: () => import('@/views/admin/NoticeManagement.vue')
      },
      {
        path: 'inventory',
        name: 'InventoryManagement',
        component: () => import('@/views/admin/InventoryManagement.vue')
      },
      {
        path: 'booth',
        name: 'BoothManagement',
        component: () => import('@/views/admin/BoothManagement.vue')
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('@/views/admin/AdminProfile.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const adminStore = useAdminStore()
  
  // 初始化用户信息
  userStore.initUser()
  adminStore.initAdmin()
  
  if (to.meta.requiresAuth) {
    if (to.meta.role === 'user') {
      // 用户端路由
      if (!userStore.isLoggedIn()) {
        next('/login')
      } else {
        next()
      }
    } else if (to.meta.role === 'admin') {
      // 管理员端路由
      if (!adminStore.isLoggedIn()) {
        next('/admin/login')
      } else {
        next()
      }
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
