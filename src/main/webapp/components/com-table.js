//用法示例
/* <com-table :tables="tables" :useslot="true" v-slot="slotProps">
    <button class="button" v-on:click="console.log(slotProps.trIndex),tables.splice(slotProps.trIndex,1)">edit</button>
</com-table> */

Vue.component('com-table', {
    props: {
        tables: Array,
        useslot: {
            type: Boolean,
            default: false
        },
        titleHead: Object
    },
    methods: {
        trOnClick: function (item) {
            this.$emit('tr-on-click', item)
            console.log("tr-on-click -on")
        }
    },
    template: '<table class="table is-hoverable table is-fullwidth">\
    <thead>\
      <th v-for="key in Object.keys(titleHead)">{{titleHead[key]}}</th>\
      <th v-if="useslot"></th>\
    </thead>\
    <tbody>\
      <tr v-for="(item,index) in tables" v-on:click="trOnClick(item)">\
          <th>{{item.id}}</th>\
          <td v-for="key in Object.keys(titleHead)"  v-if="key !=\'id\'">{{item[key]}}</td>\
          <td v-if="useslot"><slot :tr-index="index"></slot></td>\
      </tr>\
    </tbody>\
  </table>'
});