Vue.component("vip-info", {
    props: {
        vipKey: Number,//must have key
        vipSource: Object,
        update: Function,
        //update: async (vipInfo)=>{}
        isNew: {
            type: Boolean,
            default: false
        },
        ageCheckPatten: {
            type: RegExp,
            default: () => {
                return /^\d+$/
            }
        },
        phoneCheckPatten: {
            type: RegExp,
            default: () => {
                return /^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/
            }
        },
        nameCheckPatten: {
            type: RegExp,
            default: () => {
                return /^[\u4e00-\u9fa5]{2,5}$|^[A-Z|a-z]{1,20}$/
            }
        },
    },
    data: function () {
        return {
            isNotEdit: !this.isNew,
            isSumbiting: false,
            vipInfo: {
                id: this.vipKey,
                name: "",
                age: "",
                sex: null,
                phone: "",
                regDate: null,
                points: 0
            },

            //checkState
            empty: null,
            success: null
        };
    },
    created: function () {
        //test
        console.log(this.vipSource)
        console.log(this.update)
        console.log(this.isNew)

        try {
            this.resetVipInfo();
        } catch (error) {
            console.log(error);
        }

        if (this.isNew) {
            //new item
            this.success = this.successTag
        } else {
            //edit exist item
        }
    },
    computed: {
        sextag: {
            get: function () {
                if (this.vipInfo.sex === 0) return "女";
                else if (this.vipInfo.sex === 1) return "男";
                return this.vipInfo.sex;
            },
            set: function (value) {
                if (value == "男") this.vipInfo.sex = 1;
                else if (value == "女") this.vipInfo.sex = 0;
                else this.vipInfo.sex = value;
            }
        },
        cancelButtonV: function () {
            return !this.isNotEdit && !this.isNew;
        },
        editOrSubmit: function () {
            return this.isNotEdit ? "Edit" : "Sumbit";
        },

        //checkState
        successTag: () => {
            return "is-success";
        },
        danger: () => {
            return "is-danger";
        },
        checkSex: function () {
            if (this.vipInfo.sex === 0 || this.vipInfo.sex === 1) return this.success;
            else if (this.vipInfo.sex == null || this.vipInfo.sex == "")
                return this.empty;
            else return this.danger;
        },
        checkAge: function () {
            if (this.vipInfo.age == null || this.vipInfo.age == "") return this.empty;
            var patten = this.ageCheckPatten;
            if (!patten.test(this.vipInfo.age)) return this.danger;
            if (this.vipInfo.age < 0 || this.vipInfo.age > 500) return this.danger;
            return this.success;
        },
        checkPhone: function () {
            if (this.vipInfo.phone == null || this.vipInfo.phone == "")
                return this.empty;
            var patten = this.phoneCheckPatten;
            if (!patten.test(this.vipInfo.phone)) return this.danger;
            return this.success;
        },
        checkName: function () {
            if (this.vipInfo.name == null || this.vipInfo.name == "")
                return this.empty;
            var patten = this.nameCheckPatten;
            if (!patten.test(this.vipInfo.name)) return this.danger;
            return this.success;
        },
        checkAllInput: function () {

            return (
                this.checkSex == this.success &&
                this.checkAge == this.success &&
                this.checkName == this.success &&
                this.checkPhone == this.success
            );
        }
        //
    },
    methods: {
        clickAct: async function () {
            if (this.isNotEdit) {
                //enter to edit
                this.isNotEdit = !this.isNotEdit;
                this.success = this.successTag
            } else {
                //sumbit onclick
                if (!this.checkAllInput) {
                    //invaild
                    this.empty = this.danger;
                    alert("存在非法输入");
                    //最外层 v-on:mousedown="empty = null 恢复输入框状态
                } else {
                    //sumbit()
                    this.isNotEdit = !this.isNotEdit;
                    this.success = null

                    this.isSumbiting = true;
                    let result = await this.sumbit();
                    console.log("sumbit return " + result);
                    this.$emit('sumbited');//isNew=false
                    this.isSumbiting = false;

                }
            }
        },
        sumbit: async function () {
            // let updatePromise = new Promise(this.update);
            // let result = await updatePromise;
            try {
                let result = await this.update(this.vipInfo) //update is a async func
                return result;
            } catch (error) {
                console.log(error)
                return Promise.reject(error)
            }
        },
        cancel: function () {
            try {
                this.resetVipInfo();
                this.isNotEdit = !this.isNotEdit;
                this.success = null
            } catch (error) {
                console.log(error);
            }
        },
        isVipSourceVaild: function () {
            if (this.vipSource == false) return false;
            if (this.vipSource.id == null) return false;
            if (this.vipSource.name == null) return false;
            if (this.vipSource.age == null) return false;
            if (this.vipSource.sex == null) return false;
            if (this.vipSource.phone == null) return false;
            if (this.vipSource.regDate == null) return false;
            if (this.vipSource.points == null) return false;
            return true;
        },
        resetVipInfo: function () {
            if (this.isNew) {
                //new item
                //reset id
                if (this.vipKey == null) {
                    alert("can't get new id")
                    throw("can't get new id")
                }
                this.vipInfo.name = "";
                this.vipInfo.age = "";
                this.vipInfo.sex = "";
                this.vipInfo.phone = "";
                this.vipInfo.regDate = new Date().toLocaleDateString();
                this.vipInfo.points = 0;
            }
            //edit exist item
            if (!this.isVipSourceVaild())
                throw "vipdata invaild";
            this.vipInfo.id = this.vipSource.id;
            this.vipInfo.name = this.vipSource.name;
            this.vipInfo.age = this.vipSource.age;
            this.vipInfo.sex = this.vipSource.sex;
            this.vipInfo.phone = this.vipSource.phone;
            this.vipInfo.regDate = this.vipSource.regDate;
            this.vipInfo.points = this.vipSource.points;
        },
        rightIconClass: function (checkValue) {
            keyName =
                checkValue == this.success ? "fa-check" : "fa-exclamation-triangle";
            return {
                [keyName]: true
            };
        },
        showRightIcon: function (checkValue) {
            return checkValue != this.empty || this.empty == this.danger;
        }
    },
    template:
        '<div v-on:mousedown="empty = null">\
      <div class="columns">\
        <div class="field column is-5">\
          <label class="label">Id</label>\
          <div class="control has-icons-left">\
            <input class="input" type="text" :value="vipInfo.id" readonly />\
            <span class="icon is-small is-left">\
              <i class="fas fa-id-card"></i>\
            </span>\
          </div>\
        </div>\
        <div class="field column is-7">\
          <label class="label">Name</label>\
          <div class="control has-icons-left has-icons-right">\
            <input class="input" :class="{[checkName]:true}" type="text" placeholder="姓名" v-model:value="vipInfo.name" v-bind:readonly="isNotEdit" />\
            <span class="icon is-small is-left">\
              <i class="fas fa-user"></i>\
            </span>\
            <span v-if="showRightIcon(checkName)" class="icon is-small is-right">\
              <i class="fas" :class="rightIconClass(checkName)"></i>\
            </span>\
          </div>\
        </div>\
      </div>\
      <div class="columns">\
        <div class="field column is-2">\
          <label class="label">Age</label>\
          <div class="control has-icons-right">\
            <input class="input" :class="{[checkAge]:true}" type="text" placeholder="年龄" v-model:value="vipInfo.age"\
              v-bind:readonly="isNotEdit" />\
            <span v-if="showRightIcon(checkAge)" class="icon is-small is-right">\
              <i class="fas" :class="rightIconClass(checkAge)"></i>\
            </span>\
          </div>\
        </div>\
        <div class="field column is-2">\
          <label class="label">Sex</label>\
          <div class="control has-icons-right">\
            <input class="input" :class="{[checkSex]:true}" type="text" placeholder="男/女" v-model:value="sextag" \
              v-bind:readonly="isNotEdit" />\
            <span v-if="showRightIcon(checkSex)" class="icon is-small is-right">\
              <i class="fas" :class="rightIconClass(checkSex)"></i>\
            </span>\
          </div>\
        </div>\
        <div class="field column is-8">\
          <label class="label">Phone</label>\
          <div class="field has-addons">\
            <div class="control">\
              <a class="button is-info is-static">\
                +86\
              </a>\
            </div>\
            <div class="control is-expanded  has-icons-right">\
              <input class="input" :class="{[checkPhone]:true}" type="text" placeholder="手机" v-model:value="vipInfo.phone" \
                v-bind:readonly="isNotEdit" />\
                <span v-if="showRightIcon(checkPhone)" class="icon is-small is-right">\
                  <i class="fas" :class="rightIconClass(checkPhone)"></i>\
                </span>\
            </div>\
          </div>\
        </div>\
      </div>\
      <div class="columns">\
        <div class="field column is-7">\
          <label class="label">RegDate</label>\
          <div class="control has-icons-left">\
            <input class="input" type="text" :placeholder="new Date().toLocaleDateString()" :value="vipInfo.regDate" \
              readonly />\
            <span class="icon is-small is-left">\
              <i class="fas fa-calendar-day"></i>\
            </span>\
          </div>\
        </div>\
        <div class="field column is-5">\
          <label class="label">Points</label>\
          <div class="control has-icons-left">\
            <input class="input" type="text" placeholder="0" :value="vipInfo.points" readonly />\
            <span class="icon is-small is-left">\
              <i class="fas fa-star"></i>\
            </span>\
          </div>\
        </div>\
      </div>\
      <div class="field is-grouped is-grouped-centered">\
        <p class="control" v-on:click="clickAct">\
          <button class="button is-primary" :class="{\'is-loading\' : isSumbiting}">\
            {{ editOrSubmit }}\
          </button>\
        </p>\
        <p class="control" v-if="cancelButtonV" v-on:click="cancel">\
          <a class="button is-danger">\
            Cancel\
          </a>\
        </p>\
      </div>\
    </div>'
});
