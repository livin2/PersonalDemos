Vue.component('vip-tr', {
    props: {
        vip: Object,
        isSlot: {
            type: Boolean,
            default: false
        },
    },
    computed: {
        sextag: function () {
            if (this.vip.sex == 0)
                return "女"
            else
                return "男"
        }
    },
    template: ' <tr>\
                    <th>{{vip.id}}</th>\
                    <td>{{vip.name}}</td>\
                    <td>{{vip.age}}</td>\
                    <td>{{sextag}}</td>\
                    <td>{{vip.regDate}}</td>\
                    <td>{{vip.phone}}</td>\
                    <td>{{vip.points}}</td>\
                    <td v-if="isSlot"><slot></slot></td>\
                </tr>'
})