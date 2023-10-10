import { createRouter, createWebHistory } from 'vue-router'

const routes = [
 /* //这种加载方式，是项目打包的时候就会被编译，不适合大项目
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  //采用懒加载模式，其中懒加载模式表示 在大型项目中当访问当前页面的时候才会被编译
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/!* webpackChunkName: "about" *!/ '../views/AboutView.vue')
  },*/
   {
    path: '/login',
    component: () => import( '../views/login.vue')
  },

  {
    path: '/',
    component: () => import( '../views/main.vue')
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
