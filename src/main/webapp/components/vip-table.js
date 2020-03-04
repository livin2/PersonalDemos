//点击抛出tr-on-click事件与参数vipClicked
Vue.component('vip-table', {
    props: {
        vips: Array,
        isSlot: {
            type: Boolean,
            default: false
        },
    },
    methods: {
        trOnClick: function (vipClicked) {
            this.$emit('tr-on-click', vipClicked)
            // console.log("tr-on-click -on")
        }
    },
    template: '<table class="table is-hoverable table is-fullwidth">\
                <thead>\
                    <th>Id</th>\
                    <th>姓名</th>\
                    <th>年龄</th>\
                    <th>性别</th>\
                    <th>注册日期</th>\
                    <th>手机号</th>\
                    <th>积分</th>\
                    <th v-if="isSlot"></th>\
                </thead>\
                <tbody>\
                    <tr is="vip-tr" :is-slot="isSlot" v-for="(vip,index) in vips" v-bind:vip="vip" v-bind:key="vip.id" v-on:click.native="trOnClick(vip)">\
                    <slot v-if="isSlot" :tr-vip="{index,vip}"></slot>\
                    </tr>\
                </tbody>\
            </table>'
});