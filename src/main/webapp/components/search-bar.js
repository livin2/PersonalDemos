///search按钮抛出search事件与参数对象{selected,value,clearInput(),inputInvaild()}
Vue.component('search-bar', {
    props: {
        options: Object,
        inputLength: {
            type: String,
            default: "is-7"
        },
        newButton: {
            type: Boolean,
            default: true
        },
        isLoad: {
            type: Boolean,
            default: false
        },
        textPlaceholder: {
            type: Object,
            default: function () {
                return {
                    all: 'id:value,name:"value",...',
                    id: '请输入编号',
                    name: '请输入姓名',
                    age: '请输入年龄',
                    sex: '男/女',
                    regDate: '年/月/日 格式:yyyy/mm/dd',
                    phone: '请输入11位手机号',
                    points: '请输入积分数'
                }
            }
        }
    },
    data: function () {
        return {
            selected: 'all',
            inputValue: '',
            inputVaild: false
        }
    },
    methods: {
        searchClick: function () {
            this.$emit('search', {
                selected: this.selected,
                value: this.inputValue,
                clearInput: () => {
                    this.inputValue = ""
                },
                inputInvaild: () => {
                    this.inputVaild = true
                }
            })
        }
    },
    template: '<div class="columns" v-on:mousedown="inputVaild = false">\
  <div class="column" :class="inputLength">\
    <div class="field is-grouped">\
      <div class="control is-expanded field has-addons">\
        <p class="control">\
          <span class="select">\
            <select v-model="selected" @change="inputValue=\'\'">\
              <option v-for="key in Object.keys(options)">{{key}}</option>\
              <option>all</option>\
            </select>\
          </span>\
        </p>\
        <p class="control is-expanded">\
          <input class="input" :class="{\'is-danger\':inputVaild}" type="text" v-model="inputValue"\
            :placeholder="textPlaceholder[selected]">\
        </p>\
      </div>\
      <p class="control">\
        <button class="button is-info" :class="{\'is-loading\':isLoad}" @click="searchClick">Search</button>\
      </p>\
    </div>\
  </div>\
  <div class="column">\
    <slot></slot>\
  </div>\
</div>'
});