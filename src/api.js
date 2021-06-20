export default {
  /**
   * 网络中添加了路由
   * @param {Object} routeNode
   */
  onAddRoute: (routeNode) => {
    console.log("onAddRoute:", routeNode.label, routeNode.id);
  },
  /**
   * 两个路由直接建立连接
   * @param {Object} route1
   * @param {Object} route2
   * @param {Number} distance
   */
  onLinkRoutes(route1, route2, dist=1) {
    console.log("onLinkRoutes1:", route1.label, route1.id);
    console.log("onLinkRoutes2:", route2.label, route2.id);
    console.log("onLink:Distance:",dist)
  },
  /**
   * 两个路由直接连接断开
   * @param {Object} route1
   * @param {Object} route2
   */
  onDisconnect(route1, route2) {
    console.log("onDisconnect1:", route1.label, route1.id);
    console.log("onDisconnect2:", route2.label, route2.id);
  },
  /**
   * 触发RIP算法更新全网络路由表
   */
  startRIP(){
    
  },
  /**
   * 节点路由表更新后调用触发控件显示
   * @param {Object|String} route # X6 Cell Object|route.id
   * @param {Array} routeTable:[{
   *    @argument {Number} key 索引
   *    @argument {String} to 目标
   *    @argument {Number} dist 距离
   *    @argument {String} next 下一跳
   * }]
   */
  onRouteTableUpdateCB:()=>{},//Implement After Vue mounted,
  onRouteTableUpdate(route,routeTable) {
      this.onRouteTableUpdateCBs(route,routeTable);
  },
  
};
