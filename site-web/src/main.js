// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import store from './store'
import ViewUI from 'view-design';
import i18n from '@/locale'
import config from '@/config'
import importDirective from '@/directive'
import { directive as clickOutside } from 'v-click-outside-x'
import installPlugin from '@/plugin'
import './index.less'
import './them/them.less'
import '@/assets/fonts/iconfont.css'
// import style
import 'view-design/dist/styles/iview.css';
import TreeTable from 'tree-table-vue'
import VOrgTree from 'v-org-tree'
import 'v-org-tree/dist/v-org-tree.css'

// 引入svgIcon
import '@/assets/icons';
import SvgIcon from '@/components/svg-icon/svg-icon';
// vue原型上全局属性和方法统一注册入口
import Global from '@/libs/global'
Vue.use(Global)
// 实际打包时应该不引入mock
/* eslint-disable */
//if (process.env.NODE_ENV !== 'production') require('@/mock')

Vue.component('SvgIcon', SvgIcon);


Vue.use(ViewUI, {
  i18n: (key, value) => i18n.t(key, value)
})
Vue.use(TreeTable)
Vue.use(VOrgTree)
/**
 * @description 注册admin内置插件
 */
installPlugin(Vue)
/**
 * @description 生产环境关掉提示
 */
Vue.config.productionTip = false
/**
 * @description 全局注册应用配置
 */
Vue.prototype.$config = config
/**
 * 注册指令
 */
importDirective(Vue)
Vue.directive('clickOutside', clickOutside)

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  i18n,
  store,
  render: h => h(App)
})
