/*
* description: vue原型上全局属性和方法统一注册入口
* author: ycz
* date: 2019-12-23
*/
import Vue from 'vue'
import echarts from 'echarts'
import modalm from '@/components/mic-confirm/index.js'
import config from '@/config'

export default {
  install(vue, options) {
    // 按钮是否显示
    Vue.prototype.$canShowButton = (type) => {
      return true
    }
    // 全局引入echarts组件
    Vue.prototype.$echarts = echarts
    // 全局模态框覆盖
    Vue.prototype.$Modal = modalm
    // 全局注册应用配置
    Vue.prototype.$config = config
  }
}
