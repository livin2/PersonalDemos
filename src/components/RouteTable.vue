<template>
  <a-affix
    :offset-bottom="10"
    :style="{
      position: 'absolute',
      bottom: '10px',
      left: '10px',
      'z-index': '999',
    }"
  >
    <a-popover trigger="click" placement="topLeft">
      <template slot="content">
        <a-table
          style="width: 30vw"
          :columns="columns"
          :data-source="data"
          bordered
          :pagination="{ pageSize: 50 }"
          :scroll="{ y: 240 }"
        >
          <template slot="name" slot-scope="text">
            <a>{{ text }}</a>
          </template>
        </a-table>
      </template>
      <a-button type="primary"> 路由表 </a-button>
    </a-popover>
  </a-affix>
</template>
<script>
const columns = [
  {
    title: "目标",
    dataIndex: "to",
  },
  {
    title: "距离",
    dataIndex: "dist",
  },
  {
    title: "下一跳",
    dataIndex: "next",
  },
];

const data = [];
for (let i = 0; i < 100; i++) {
  data.push({
    key: i,
    to: `K${i}`,
    dist: 32,
    next: `L${i}`,
  });
}

export default {
  name: "RouteTable",
  data() {
    return {
      data,
      columns,
    };
  },
};
</script>
