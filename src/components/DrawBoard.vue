<template>
  <div id="container" ref="container"></div>
</template>

<script>
import { Graph } from "@antv/x6";
export default {
  name: "DrawBoard",
  props: {
    msg: String,
  },
  mounted() {
    const data = {
      // 节点
      nodes: [
        {
          id: "route1", // String，可选，节点的唯一标识
          x: 40, // Number，必选，节点位置的 x 值
          y: 40, // Number，必选，节点位置的 y 值
          width: 80, // Number，可选，节点大小的 width 值
          height: 40, // Number，可选，节点大小的 height 值
          label: "route1", // String，节点标签
        },
        {
          id: "route2", // String，节点的唯一标识
          x: 160, // Number，必选，节点位置的 x 值
          y: 180, // Number，必选，节点位置的 y 值
          width: 80, // Number，可选，节点大小的 width 值
          height: 40, // Number，可选，节点大小的 height 值
          label: "route2", // String，节点标签
        },
      ],
      // 边
      edges: [
        {
          source: "route1", // String，必须，起始节点 id
          target: "route2", // String，必须，目标节点 id
          attrs:{
            line:{
              stroke: "#7c68fc", 
              targetMarker:''
            }
          }
        },
      ],
    };

    const graph = new Graph({
      container: document.getElementById("container"),
      width: 800,
      height: 600,
      grid: {
        size: 10, // 网格大小 10px
        visible: true, // 渲染网格背景
      },
    });
    graph.fromJSON(data);
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="less">
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
