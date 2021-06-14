import Vue from 'vue'
import App from './App.vue'
import './registerServiceWorker'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import eruda from 'eruda'
Vue.config.productionTip = false
Vue.use(Antd);
eruda.init()
new Vue({
  render: h => h(App),
}).$mount('#app')
