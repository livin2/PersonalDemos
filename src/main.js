import Vue from 'vue'
import App from './App.vue'
import './registerServiceWorker'
import eruda from 'eruda'
Vue.config.productionTip = false
eruda.init()
new Vue({
  render: h => h(App),
}).$mount('#app')
