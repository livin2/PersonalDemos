<template>
<div>
  <a-affix :offset-top="10"
     :style="{ position: 'absolute', top: '10px', right: '10px','z-index':'999'}">
      <a-button type="primary" @click="addRoute">
        添加节点
      </a-button>
      <a-button type="primary" style="margin-left:10px">
        连接路由
      </a-button>
    </a-affix>
<div id="main">
  <div id="container" ref="container"></div>
</div>
</div>
</template>

<script>
import { Graph} from "@antv/x6";
import api from "../api"
export default {
  name: "DrawBoard",
  props: {
    msg: String,
  },
  data(){
    return{
      graph:null,
      routeLabelSta:0,
      selectionStack:[null,null],
    }
  },
  methods:{
    addRoute(){
      let rcolor = (Math.floor(Math.random()*128+127)<<16|Math.floor(Math.random()*128+127)<<8|Math.floor(Math.random()*128+127)).toString(16);
      const node = this.graph.addNode({
        shape: 'rect', // 指定使用何种图形，默认值为 'rect'
        x: 40,
        y: 40,
        width: 80,
        height: 40,
        label: `R${this.routeLabelSta}`,
        attrs:{
          body: {
            fill:`#${rcolor}`  // 背景颜色
          },
        }
      })
      this.routeLabelSta++;
      api.onAddRoute(node)
    },
    onRouteSelected(node){
      let pre = this.selectionStack.shift();
      console.warn(pre)
      this.selectionStack.push(node)
      if(pre) this.graph.unselect(pre);
      console.warn(this.selectionStack)
    }
  },
  mounted() {
    this.graph = new Graph({
      container: this.$refs.container,
      autoResize:window.Docuemnt,
      grid: {
        size: 10, // 网格大小 10px
        visible: true, // 渲染网格背景
      },
      selecting: {
        enabled: true,
        rubberband: true, // 启用框选
        showNodeSelectionBox:true
      },
    });
    this.graph.on('node:selected', ({node}) => this.onRouteSelected(node))
    this.graph.on('node:unselected',()=>this.graph.isSelectionEmpty()?this.selectionStack=[null,null]:'');
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="less">
#main{
  width:100vw;
  height: 100vh;
  position: absolute;
  top: 0;
  left: 0;
  display: flex;
  flex-wrap: nowrap;
  align-items: stretch;
}
#container{
  height: auto;
  flex: 1;
}
::v-deep .x6-widget-selection-box{
    // border:none;
    margin: 0;
    padding: 0;
    box-shadow: 0px 0px 9px 4px #5498f1;
    border: none;
}
</style>
