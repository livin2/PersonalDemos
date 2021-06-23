<template>
  <div>
    <a-affix
      :offset-top="10"
      :style="{
        position: 'absolute',
        top: '10px',
        right: '10px',
        'z-index': '999',
      }"
    >
      <a-button type="primary" @click="addRoute"> 添加节点 </a-button>
      <a-button
        type="primary"
        style="margin-left: 10px"
        v-if="stepQueue.length<=0"
        @click="updateAllRoute"
      >
        RIP更新路由
      </a-button>
      <a-button-group v-if="stepQueue.length" style="margin-left: 10px">
        <a-button type="primary" v-if="backStack.length" icon="step-backward" @click="StepBackward" />
        <a-button type="" icon="step-forward" @click="StepForward">
          {{stepT}}
        </a-button>
        <a-button type="danger" icon="logout" @click="Stop"/>
      </a-button-group>
      <a-button
        type="primary"
        style="margin-left: 10px"
        v-if="selectionStack[0] && selectionStack[1] && canLink"
        @click="prelinkRoute"
      >
        连接路由
      </a-button>
      <a-button
        type="danger"
        style="margin-left: 10px"
        v-if="!selectionStack[0] && selectionStack[1] && stepQueue.length<=0"
        @click="delRoute"
      >
        删除路由
      </a-button>
      <!-- <a-button
        type="danger"
        style="margin-left: 10px"
        @click="test"
      >
        test
      </a-button> -->
    </a-affix>
    <a-modal
      v-model="modalvisable" 
      title="" 
      @ok="linkRoute"
      style="position:absolute;z-index:999">
      请输入距离权重: 
      <a-input-number
        v-model='dist'
        :min='1'
        :max='16'
        :step='1'
        />
    </a-modal>
    <RouteTable ref='rtab' :data="currentRouteTable"/>
    <div id="main">
      <div id="container" ref="container"></div>
    </div>
  </div>
</template>

<script>
import { Graph } from "@antv/x6";
import RouteTable from './RouteTable.vue'
import api from "../api";
/**
  *   @argument {Number} key 索引
  *   @argument {RouteRef} to 目标
  *   @argument {Number} dist 距离
  *   @argument {RouteRef} next 下一跳
 */
class rtItem {
  constructor(key,to,dist,next) {
    this.key = key;
    this.to = to;
    this.dist = dist;
    this.next = next;
  }
  clone(){
    return new rtItem(this.key,this.to,this.dist,this.next);
  }
}
const RTadapter = (route)=>
  Object.values(route['routeTables']).map(x=>({
      ...x,
      to: x.to.label,
      next: x.next.label,
  }));
export default {
  name: "DrawBoard",
  components: {
    RouteTable
  },
  props: {
    msg: String,
  },
  data() {
    return {
      currentRouteTable: [],
      graph: null,
      canLink: false,
      routeLabelSta: 0,
      selectionStack: [null, null],
      modalvisable:false,
      dist:1,
      stepQueue:[],
      backStack:[],
      unreachable:16,
      stepOnBegin:false,
      stepT:'下一步'
    };
  },
  methods: {
    test(){
      let node = this.addRoute();
      api.onRouteTableUpdate(node,[{
        key:0,
        to:'Rn',
        dist:99,
        next:'R5'
      }])
    },
    Stop(){
      this.graph.cleanSelection();
      this.stepQueue = [];
      this.backStack = [];
    },
    StepBackward(){
      if(!this.backStack.length) return console.warn('NO CHANGE');
      if(this.stepOnBegin){ //刚更新完
        let [from,route,routeT] = this.backStack.pop();
        // console.log(777,routeT)
        route['routeTables'] = routeT;
        this.currentRouteTable = RTadapter(route);
        console.info('-----------------------------------------')
        console.info(`回退路由表更新：${from.label} => ${route.label}`)
        console.table(JSON.parse(JSON.stringify(this.currentRouteTable)));
        this.$notification.warning({
          message: route.label+'回退路由表更新',
          description:'接受来自'+from.label+'路由表的更新',
          placement:'bottomRight',
        });

        this.stepT = '更新'
        this.stepOnBegin = false;
        // console.info('-----------------------------------------')
      }else{
        this.graph.cleanSelection();
        this.$refs.rtab.visible = false;
        let route = this.backStack[this.backStack.length - 1][1];
        this.graph.select(route);
        this.currentRouteTable = RTadapter(route);
        this.$refs.rtab.visible = true;

        this.stepT = '下一步'
        this.stepOnBegin = true;
      }
    },
    StepForward(){
      if(!this.stepQueue.length) return console.warn('NO CHANGE');
      if(this.stepOnBegin){
        this.graph.cleanSelection();
        this.$refs.rtab.visible = false;
        let route = this.stepQueue[0][1];
        this.graph.select(route);
        this.currentRouteTable = RTadapter(route);
        this.$refs.rtab.visible = true;
        this.stepT = '更新'
        this.stepOnBegin = false;
      }else{
        this.StepDvAlgorithm();
        this.stepT = '跳转'
        this.stepOnBegin = true;
      }
    },
    StepDvAlgorithm(){
      let [from,route] = this.stepQueue.shift(); //from:the route send RouteTable
      console.info('-----------------------------------------')
      console.info(`更新路由表：${from.label} => ${route.label}`)
      console.info(`${route.label}更新前：`)
      console.table(JSON.parse(JSON.stringify(this.currentRouteTable)));
      //TODO UPDATE RouteTable
      this.backStack.push([from,route,{...route['routeTables']}]);
      for(let tid in from['routeTables']){
        if(tid == route.id) continue;
        let ndist = from['routeTables'][tid].dist + route['routeTables'][from.id].dist;

        if(tid in route['routeTables']){
          
          if(from['routeTables'][tid].dist >= this.unreachable &&
            route['routeTables'][tid].next == from.id){
              route['routeTables'][tid].dist = this.unreachable;
              route['rTableDirty'] = true;
          }
          
          if(ndist< route['routeTables'][tid].dist){
            route['routeTables'][tid].next = from;
            route['routeTables'][tid].dist = ndist;
            route['rTableDirty'] = true;
          }

        }else{
          this.addRouteTableItem(route,from['routeTables'][tid].to,ndist,from)
          route['rTableDirty'] = true;
        }
      }
      this.currentRouteTable = RTadapter(route);
      if(route['rTableDirty']){
        // this.$message.success(route.label+'路由表已更新');
        this.$notification.success({
          message: route.label+'路由表已更新',
          description:'接受来自'+from.label+'的路由表',
          placement:'bottomRight',
        });
        console.info(`${route.label}更新后：`)
        console.table(JSON.parse(JSON.stringify(this.currentRouteTable)));
      }
      else{
        // this.$message.info(route.label+'路由表无改动');
        this.$notification.info({
          message: route.label+'路由表无改动',
          description:'接受来自'+from.label+'的路由表',
          placement:'bottomRight',
        });
        console.info(`${route.label}路由表无更新`);
        this.backStack.pop();
      }
      // console.info('-----------------------------------------');
      this.updateQueue(route);
      this.$refs.rtab.visible = true;
      // route.routeTable = routeTable;
      // console.log(from);
    },
    updateQueue(from){
      if(!from['rTableDirty']) return;
      this.stepQueue = 
          this.stepQueue.concat(this.graph.getNeighbors(from).map(x=>[from,x]));
      from['rTableDirty'] = false;
    },
    updateAllRoute(){
      this.backStack = [];
      this.stepQueue = [];
      let selected = this.graph.getSelectedCells();
      let start = this.graph.getNodes()[0];
      if(selected.length>0) start = selected.pop();
      // console.log(this.graph.getNeighbors(start));
      this.updateQueue(start);
      this.StepForward();
      return start;
      // api.startRIP(start);
    },
    addRoute() {
      // if(this.routeLabelSta==10) return console.error('节点数不能超过10');
      let rcolor = (
        (Math.floor(Math.random() * 128 + 127) << 16) |
        (Math.floor(Math.random() * 128 + 127) << 8) |
        Math.floor(Math.random() * 128 + 127)
      ).toString(16);
      const node = this.graph.addNode({
        shape: "rect", // 指定使用何种图形，默认值为 'rect'
        x: 40,
        y: 40,
        width: 80,
        height: 40,
        label: `R${this.routeLabelSta}`,
        attrs: {
          body: {
            fill: `#${rcolor}`, // 背景颜色
          },
        },
      });
      this.routeLabelSta++;
      // node['ConnectedRoutes'] = {}
      // node.getRid = function(){return this.label} //OR return this.id
      api.onAddRoute(node);
      node['routeTables'] = {};
      return node;
    },
    delRoute(){
      let node = this.selectionStack[1];
      this.graph.getConnectedEdges(node).forEach(edge => {
        this.graph.removeEdge(edge);
      });
      this.graph.removeNode(node);
    },
    onRouteSelected(node) {
      if(this.graph.getSelectedCells().length == 1){
        this.currentRouteTable = RTadapter(node);
      }
      this.selectionStack.push(node);
      let pre = this.selectionStack.shift();
      if (pre) this.graph.unselect(pre);
      if (this.selectionStack[0] && this.selectionStack[1]) {
        if (
          !this.graph.isNeighbor(this.selectionStack[0], this.selectionStack[1])
        )
          this.canLink = true;

        // console.log(555,
        //   this.selectionStack[0],
        //   this.selectionStack[1],
        //   this.graph.isNeighbor(this.selectionStack[0], this.selectionStack[1]));
      }
      // console.log("onRouteSelected", this.selectionStack);
    },
    prelinkRoute(){
      let selected = this.graph.getSelectedCells();
      if (this.graph.isNeighbor(selected[0], selected[1])) return console.info('linked');
      this.modalvisable = true;

    },
    addRouteTableItem(f,t,d,n){
      let rt = f['routeTables'];
      rt[t.id] = new rtItem(Object.keys(rt).length,t,d,n);
      f['rTableDirty'] = true;
    },
    linkRoute() {
      let selected = this.graph.getSelectedCells();
      if (this.graph.isNeighbor(selected[0], selected[1])) return;
      this.modalvisable = false;
      let tdist = this.dist;
      this.dist = 1;
      this.graph.addEdge({
        shape: "edge", // 指定使用何种图形，默认值为 'edge'
        source: selected[0],
        target: selected[1],
        label:`${tdist}`,
        attrs: {
          line: { targetMarker: "" },
        },
        tools: {
          name: "button-remove",
        },
      });
      this.canLink = false;
      api.onLinkRoutes(selected[0], selected[1],tdist);
      this.addRouteTableItem(selected[0],selected[1],tdist,selected[1]);
      this.addRouteTableItem(selected[1],selected[0],tdist,selected[0]);
    },
  },
  mounted() {
    this.$notification.open({
        message: 'F12进入控制台查看日志',
        description:'请使用Chrome/Edge浏览器打开',
        duration: 60,
      });
    this.graph = new Graph({
      container: this.$refs.container,
      autoResize: window.Docuemnt,
      grid: {
        size: 10, // 网格大小 10px
        visible: true, // 渲染网格背景
      },
      selecting: {
        enabled: true,
        rubberband: true, // 启用框选
        showNodeSelectionBox: true,
        filter: ["edge"],
      },
    });
    this.graph.on("node:selected", ({ node }) => this.onRouteSelected(node));
    this.graph.on("node:unselected", () =>
      this.graph.isSelectionEmpty() ? (this.selectionStack = [null, null]) : ""
    );
    this.graph.on("edge:removed", ({ edge }) => {
      let fnode = this.graph.getCellById(edge.getSourceCellId());
      let tnode = this.graph.getCellById(edge.getTargetCellId());
      // delete fnode['routeTables'][tnode.id]; 
      // delete tnode['routeTables'][fnode.id];
      fnode['routeTables'][tnode.id].dist = this.unreachable;
      tnode['routeTables'][fnode.id].dist = this.unreachable;
      api.onDisconnect(fnode,tnode);
    });
    // api.onRouteTableUpdateCBs=(route,routeTable)=>{
    //   this.graph.cleanSelection();
    //   this.$refs.rtab.visible = false;
    //   this.currentRouteTable = routeTable;
    //   if(route instanceof String)
    //     route = this.graph.getCellById(route)
    //   this.graph.select(route)
    //   route.routeTable = routeTable;
    //   this.$refs.rtab.visible = true;
    // };
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="less">
#main {
  width: 100vw;
  height: 100vh;
  position: absolute;
  top: 0;
  left: 0;
  display: flex;
  flex-wrap: nowrap;
  align-items: stretch;
}
#container {
  height: auto;
  flex: 1;
}
::v-deep .x6-widget-selection-box {
  // border:none;
  margin: 0;
  padding: 0;
  box-shadow: 0px 0px 9px 4px #5498f1;
  border: none;
}
</style>
