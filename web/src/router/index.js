import { createRouter, createWebHistory } from 'vue-router'
import {notification} from "ant-design-vue";
import store from "@/store";

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
    component: () => import( '../views/main.vue'),
      meta: {
        loginRequire: true
      },
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})


// 路由登录拦截
router.beforeEach((to, from, next) => {
    // 要不要对meta.loginRequire属性做监控拦截
    if (to.matched.some(function (item) {
        console.log(item, "是否需要登录校验：", item.meta.loginRequire || false);
        return item.meta.loginRequire
    })) {
        const _member = store.state.member;
        console.log("页面登录校验开始：", _member);
        if (!_member.token) {
            console.log("用户未登录或登录超时！");
            notification.error({ description: "未登录或登录超时" });
            next('/login');
        } else {
            next();
        }
    } else {
        next();
    }
});

export default router
