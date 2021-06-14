export default{
    /**
     * 网络中添加了路由
     * @param {Object} routeNode 
     */
    onAddRoute:(routeNode)=>{
        console.log('onAddRoute:',routeNode.label,routeNode.id);
    },
    /**
     * 两个路由直接建立连接
     * @param {Object} route1 
     * @param {Object} route2 
     */
    onLinkRoutes(route1,route2){
        console.log('onLinkRoutes1:',route1.label,route1.id);
        console.log('onLinkRoutes2:',route2.label,route2.id);
    },
    /**
     * 两个路由直接连接断开
     * @param {Object} route1 
     * @param {Object} route2 
     */
     onDisconnect(route1,route2){
        console.log('onDisconnect1:',route1.label,route1.id);
        console.log('onDisconnect2:',route2.label,route2.id);
    }
}