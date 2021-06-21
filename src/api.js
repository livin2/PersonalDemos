// 这里节点数组，最多10个节点
var nodeRalation = [
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
];
var nodeCount = 0;

var stack = new Array();
var routeMap = new Map();
var Node = {
  name: "",
  delete: false,
  relationNodes: Array,
  createNew: (name) => {
    var node = {};
    node.name = name;
    return node;
  }
}

/**
   * 打印每个节点所有的路由表
   */
const printRouter = () => {
  /* 定义节点数组 */
  var node = new Array();
  for (var i1 = 0; i1 < nodeCount; i1++) {
    var nd = Node.createNew("R" + i1);
    node[i1] = nd;
  }
  /* 定义与节点相关联的节点集合 */
  for (var i2 = 0; i2 < nodeCount; i2++) {
    var List = new Array();
    for (var j2 = 0; j2 < nodeRalation[i2].length; j2++) {
      if (nodeRalation[i2][j2] != 0) {
        List[List.length] = (node[j2]);
      }
    }
    node[i2].relationNodes = List;
    node[i2].delete = List.length==0;
    List = null;  //释放内存
  }
  /* 开始搜索所有路径 */
  for (var i3 = 0; i3 < node.length; i3++) {
    if (node[i3].delete) {
      continue;
    }
    for (var j3 = 0; j3 < node.length; j3++) {
      if (i3 == j3 || node[j3].delete) {
        continue;
      }
      console.log("起始点" + node[i3].name + "，终点" + node[j3].name + "的所有路径：");
      getPaths(node[i3], null, null, node[j3]);
      console.log("起始点" + node[i3].name + "，终点" + node[j3].name + "的最短路径：");
      getMinRoute(node[i3].name, node[j3].name);
      console.log("\n")
    }
  }
}

/**
    * 获取两个节点间的最短路径
    *
    * @param startNodeName 起始节点名称
    * @param endNodeName 终止节点名称
    */
const getMinRoute = (startNodeName, endNodeName) => {
  var routes = routeMap.get(getRouteKey(startNodeName, endNodeName));
  var minRoute = null;
  var minRouteCount = 0;
  for (let idx = 0; routes != null && idx < routes.length; idx++) {
    var route = routes[idx];
    var routeCount = getRouteCount(route);
    if (idx == 0 || routeCount < minRouteCount) {
      minRoute = route;
      minRouteCount = routeCount;
    }
  }
  print(minRoute);
}

/**
     * 添加节点,nodeCount+1
     */
const onNodeAdd = () => {
  if (nodeCount >= nodeRalation.length) {
    console.log("超过上限，无法添加节点！")
    return;
  }
  console.log("添加节点R" + (nodeCount))
  nodeCount++;
}

/**
 * 更新两个节点之间的值
 * @param {*} nodeIdx1 
 * @param {*} nodeIdx2 
 * @param {*} num 
 */
const updateNodeLink = (nodeIdx1, nodeIdx2, num) => {
  if (nodeIdx1 < nodeCount && nodeIdx2 < nodeCount) {
    nodeRalation[nodeIdx1][nodeIdx2] = num;
    nodeRalation[nodeIdx2][nodeIdx1] = num;
    // printRouter();
  }
}

/**
 * 取消两个节点间的连接
 * @param {*} nIdx1 
 * @param {*} nIdx2 
 */
const onNodeDisconnect = (nIdx1, nIdx2) => {
  updateNodeLink(nIdx1, nIdx2, 0);
}
/**
 * 删除节点,这里实际上是取消节点的所有关连
 * @param {} delNodeIdx 
 */
const onNodeDel = (delNodeIdx) => {
  if (delNodeIdx >= nodeCount) {
    console.log("不存在该节点R" + delNodeIdx);
  } else {
    console.log("删除节点R" + delNodeIdx);
    for (let dI = 0; dI < nodeCount; dI++) {
      nodeRalation[delNodeIdx][dI] = 0;
    }
    for (let dJ = 0; dJ < nodeRalation.length; dJ++) {
      nodeRalation[dJ][delNodeIdx] = 0;
    }
    // printRouter();
  }
}

/*
     * 寻找路径的方法
     * cNode: 当前的起始节点currentNode
     * pNode: 当前起始节点的上一节点previousNode
     * sNode: 最初的起始节点startNode
     * eNode: 终点endNode
     */
const getPaths = (cNode, pNode, sNode, eNode) => {
  if (cNode.delete) {
    return false;
  }
  var nNode = null;
  /* 如果符合条件判断说明出现环路，不能再顺着该路径继续寻路，返回false */
  if (cNode != null && pNode != null && cNode == pNode)
    return false;
  if (cNode != null) {
    var index = 0;
    /* 起始节点入栈 */
    stack.push(cNode);
    /* 如果该起始节点就是终点，说明找到一条路径 */
    if (cNode == eNode) {
      /* 转储并打印输出该路径，返回true */
      showAndSavePath();
      return true;
    }
    /* 如果不是,继续寻路 */
    else if (!cNode.relationNodes.isEmpty) {
      /*
       * 从与当前起始节点cNode有连接关系的节点集中按顺序遍历得到一个节点
       * 作为下一次递归寻路时的起始节点
       */
      nNode = cNode.relationNodes[index];
      while (nNode != null) {
        /*
         * 如果nNode是最初的起始节点或者nNode就是cNode的上一节点或者nNode已经在栈中 ，
         * 说明产生环路 ，应重新在与当前起始节点有连接关系的节点集中寻找nNode
         */
        if (pNode != null && (nNode == sNode || nNode == pNode || isNodeInStack(nNode))) {
          index++;
          if (index >= cNode.relationNodes.length)
            nNode = null;
          else
            nNode = cNode.relationNodes[index];
          continue;
        }
        /* 以nNode为新的起始节点，当前起始节点cNode为上一节点，递归调用寻路方法 */
        if (getPaths(nNode, cNode, sNode, eNode))/* 递归调用 */ {
          /* 如果找到一条路径，则弹出栈顶节点 */
          stack.pop();
        }
        /* 继续在与cNode有连接关系的节点集中测试nNode */
        index++;
        if (index >= cNode.relationNodes.length)
          nNode = null;
        else
          nNode = cNode.relationNodes[index];
      }
      /*
       * 当遍历完所有与cNode有连接关系的节点后，
       * 说明在以cNode为起始节点到终点的路径已经全部找到
       */
      stack.pop();
      return false;
    }
  }
  return false;
}

const showAndSavePath = () => {
  var o = stack.slice(0, stack.length);
  print(o);
  var routeKey = getRouteKey(o[0].name, o[o.length - 1].name);
  if (routeMap.get(routeKey) == null) {
    var stacks1 = new Array();
    stacks1[0] = o;
    routeMap.set(routeKey, stacks1);
  } else {
    var stacks2 = routeMap.get(routeKey);
    stacks2[stacks2.length] = o;
  }
}

const print = (o) => {
  if (o == null) {
    return;
  }
  var s = "";
  for (let index1 = 0; index1 < o.length; index1++) {
    if (index1 < (o.length - 1)) {
      s += o[index1].name + "->";
    } else {
      s += o[index1].name + "   " + getRouteCount(o);
    }
  }
  console.log(s);
}

const isNodeInStack = (nNode) => {
  for (let index = 0; index < stack.length; index++) {
    if (stack[index] == nNode)
      return true;
  }
  return false;
}
const getRouteCount = (route) => {
  var count = 0;
  for (var k = 0; k < route.length - 1; k++) {
    var curNode = route[k];
    var nextNode = route[k + 1];
    count += nodeRalation[(curNode.name.charAt(1))][(nextNode.name.charAt(1))];
  }
  return count;
}
const getRouteKey = (start, end) => {
  return start + "," + end;
}




export default {
  printRouter,
  onNodeAdd,
  updateNodeLink,
  onNodeDisconnect,
  onNodeDel,

  /**
   * 网络中添加了路由
   * @param {Object} routeNode
   */
  onAddRoute: (routeNode) => {
    console.log("onAddRoute:", routeNode.label);
    // 节点自增
    onNodeAdd();
  },
  /**
   * 两个路由直接建立连接
   * @param {Object} route1
   * @param {Object} route2
   * @param {Number} distance
   */
  onLinkRoutes(route1, route2, dist=1) {
    console.log("onLinkRoutes1:", route1.label);
    console.log("onLinkRoutes2:", route2.label);
    // 参数为数组下表
    updateNodeLink(Number(route1.label.slice(1)),
      Number(route2.label.slice(1)),dist);
  },
  /**
   * 两个路由直接连接断开
   * @param {Object} route1
   * @param {Object} route2
   */
  onDisconnect(route1, route2) {
    console.log("onDisconnect1:", route1.label, route1.id);
    console.log("onDisconnect2:", route2.label, route2.id);
    // 参数为数组下表
    onNodeDisconnect(Number(route1.label.slice(1)),Number(route2.label.slice(1)));
  },
  /**
   * 触发RIP算法更新全网络路由表
   */
  startRIP() {
    printRouter();
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
  onRouteTableUpdateCB: () => { },//Implement After Vue mounted,
  onRouteTableUpdate(route, routeTable) {
    this.onRouteTableUpdateCBs(route, routeTable);
  },

};
