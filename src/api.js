export default{
    /**
     * 网络中添加了路由
     * @param {Object} routeNode 
     */
    onAddRoute:(routeNode)=>{
        console.log('newroute:',routeNode.id);
        console.log('newroute:',routeNode.label);
    },
    /**
     * 两个路由直接建立连接
     * @param {Object} route1 
     * @param {Object} route2 
     */
    onLinkRoutes(route1,route2){
        console.log('route1:',route1.label,route1.id);
        console.log('route2:',route2.label,route2.id);
    }
}