Vue.component('func-vip', {
    props: {
        tables: Array
    },
    data: function () {
        return {
            //URLs
            getAllVipURL: '/comvipsys_war/getAllVip',
            searchVipURL: '/comvipsys_war/searchVip',
            advSearchURL: '/comvipsys_war/advancedSearch',
            updateURL: '/comvipsys_war/updateVip',
            addNewURL: '/comvipsys_war/addNewVip',
            getNewIdURL: '/comvipsys_war/getNewId',
            deleteVipURL: '/comvipsys_war/deleteVip',

            //ComponentField
            currentTabComponent: "vip-table",
            //Component-MountedEvent
            v_on_event: "", //Html会转换大小写
            vOnFunc: null,
            //Component-MountedProps
            vProps: null,

            //postTable
            postTables: null,
            //postVipInfo
            postVip: null,
            postId: 1,
            postNew: true,
            postUpdateFuc: null,

            //UI Animation
            searchIsLoad: false,

            //vipEmptyExp
            vipEmptyExp: {
                id: null,
                name: null,
                age: null,
                sex: null,
                regDate: null,
                phone: null,
                points: 0
            },

            vipCheckPatten: {
                id: "\\d+",
                name: "([\\u4e00-\\u9fa5]{2,5})|([A-Z|a-z]{1,20})",
                age: "\\d+",
                sex: "男|女",
                regDate: "(19|20)\\d\\d[/](0[1-9]|1[012])[/](0[1-9]|[12][0-9]|3[01])",
                phone: "1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}",
                points: "\\d+"
            }
        }
    },
    created: function () {
        // this.postTables = this.tables
        // this.mountVipTable()
        this.getAllVip()

    },
    computed: {
        postVipInfo: function () {
            return {
                vipKey: this.postId,
                vipSource: this.postVip,
                update: this.postUpdateFuc,
                isNew: this.postNew
            }
        },
        postVipTable: function () {
            return {
                vips: this.postTables,
                isSlot: true
            }
        },
        allPatten: function () {
            let s = ""
            s += "^(id:(" + this.vipCheckPatten.id + "),?)"
            for (let key in this.vipCheckPatten) {
                if (key != 'id') {
                    if (key != 'age' && key != 'points')
                        s += "|(" + key + ":\"(" + this.vipCheckPatten[key] + ")\",?)"
                    else
                        s += "|(" + key + ":(" + this.vipCheckPatten[key] + "),?)"
                }
            }
            s += "$"
            return new RegExp(s)
        }
    },
    methods: {
        //return a AsyncFunc {return a Promise}
        updateFucFactory: function (vipSource) {
            return async (vipInfo) => {
                let re = this.sumbitAspect(async () => {
                    let response = await axios.post(this.updateURL, vipInfo)
                    return response.data
                })
                console.log(vipInfo)
                vipSource.id = vipInfo.id
                vipSource.name = vipInfo.name
                vipSource.age = vipInfo.age
                vipSource.sex = vipInfo.sex
                vipSource.phone = vipInfo.phone
                vipSource.regDate = vipInfo.regDate
                vipSource.points = vipInfo.points
                return re
            }
        },
        searchMethod: function (val) {
            console.log("search bar emit: ", val)
            if (val.selected == 'all' && val.value == "") {
                this.getAllVip()
            } else if (!this.vipChecker(val.selected, val.value)) {
                val.inputInvaild()
                alert("非法输入")
                val.clearInput()
            } else if (val.selected == 'all') {
                this.searchAspect(async () => {
                    let jsVal = eval('({' + val.value + '})')
                    // let jsVal = JSON.parse('[{'+val.value+'}]')
                    if ('sex' in jsVal) {
                        console.log("sexTag in input")
                        jsVal.sex = jsVal.sex == "男" ? 1 : 0
                    }

                    console.log("jsVal: ", jsVal)
                    let response = await axios.post(this.advSearchURL, jsVal)
                    return response
                })
            } else {
                this.searchAspect(async () => {
                    let key = val.selected
                    let value = val.value
                    console.log(key, value)
                    if (key == 'sex') {
                        if (value == '男')
                            value = 1;
                        else if (value == '女')
                            value = 0
                        else
                            throw "sex expect 男/女"
                    }
                    let response = await axios.post(this.searchVipURL, {
                        key: key,
                        [key]: value,
                    })
                    return response
                })
            }
        },
        //Promise sumbitAspect(asyncFunc request)
        sumbitAspect: async function (request) {
            try {
                let response = await request()
                console.log("response:", response)
                if (response.code == 200) {
                    alert("提交成功")
                    return response
                } else
                    throw "unknow code"
            } catch (e) {
                alert("提交失败")
                this.logError(e)
            }
        },
        //void searchAspect(asyncFunc request)
        searchAspect: async function (request) {
            //before
            this.searchIsLoad = true
            try {
                let response = await request()
                //after
                console.log("response:", response)
                this.postTables = response.data
                this.mountVipTable()
            } catch (error) {
                if (error.response)
                    this.handleError(error.response.data)
                else
                    alert("加载失败")
                this.logError(error)
            } finally {
                this.searchIsLoad = false
            }
        },
        getAllVip: async function () {
            this.searchIsLoad = true
            try {
                let response = await axios.get(this.getAllVipURL)
                console.log("getAllVip response: ", response)
                this.postTables = response.data
                this.mountVipTable()
            } catch (error) {
                if (error.response)
                    this.handleError(error.response.data)
                else
                    alert("加载失败")
                this.logError(error)
            } finally {
                this.searchIsLoad = false
            }
        },
        handleError: function (errResponse) {
            switch (errResponse.errCode) {
                case 404:
                    alert("找不到结果");
                    break;
                default:
                    alert("加载失败,错误信息:" + errResponse.errMsg)
            }
        },
        logError: function (error) {
            if (error.response) {
                console.log("response.status: ", error.response.status);
                console.log("response.data: ", error.response.data);
                console.log("response.header: ", error.response.headers);
            } else if (error.request) {
                console.log("request: ", error.request);
            } else {
                console.log('Error: ', error.message);
            }
            console.log('config: ', error.config);
        },
        vipChecker: function (key, value) {
            if (key == 'all') {
                return this.allPatten.test(value)
            }
            var patten = new RegExp("^" + this.vipCheckPatten[key] + "$")
            console.log("patten: ", patten)
            if (!patten.test(value))
                return false
            return true
        },
        mountVipTable: function () {
            //ComponentField
            this.currentTabComponent = "vip-table"
            //Component-MountedEvent
            this.vOnEvent = "tr-on-click" //vip-table点击条目抛出的事件tr-on-click会抛出参数vipClicked给vOnFunc
            // this.vOnFunc = (vipClicked) => { //this 指向外层
            //     this.mountVipInfo(vipClicked)
            //     // console.log(this)
            // }
            //Component-MountedProps
            this.vProps = this.postVipTable
        },
        mountVipInfo: function (vipClicked) {
            //postVipInfo
            this.postId = vipClicked.id
            this.postVip = vipClicked
            this.postNew = false
            this.postUpdateFuc = this.updateFucFactory(this.postVip)

            //ComponentField
            this.currentTabComponent = "vip-info"
            //Component-MountedEvent
            this.vOnEvent = "submited"
            this.vOnFunc = () => {
                this.postNew = false
                //
            }
            //Component-MountedProps
            this.vProps = this.postVipInfo
        },
        mountNewInfo: async function () {
            this.$emit('new-click')
            //postVipInfo
            this.postId = await this.getNewid()
            this.postVip = this.vipEmptyExp
            this.postNew = true
            this.postUpdateFuc = async (vipInfo) => {
                let re = await this.sumbitAspect(async () => {
                    let response = await axios.post(this.addNewURL, vipInfo)
                    return response.data
                })
                this.postTables.push(vipInfo)
                this.mountVipTable();
                return re
            }

            //ComponentField
            this.currentTabComponent = "vip-info"
            //Component-MountedEvent
            this.vOnEvent = "submited"
            this.vOnFunc = () => {
                this.postNew = false
                //
            }
            //Component-MountedProps
            this.vProps = this.postVipInfo
        },
        buttonDetail: function (vipClicked) {
            this.mountVipInfo(vipClicked)
        },
        buttonDelete: async function (index, vip) {
            console.log("index: ", index)
            console.log("id: ", vip.id)
            try {
                var confirmDel = window.confirm("确定要删除此项目？")
                if (confirmDel) {
                    let post = {id: vip.id};
                    let re = await axios.post(this.deleteVipURL, post)
                    this.postTables.splice(index, 1)
                    // this.mountVipTable()
                }
            } catch (e) {
                this.logError(e)
            }
        },
        getNewid: async function () {
            //request a id from serve
            try {
                let re = await axios.get(this.getNewIdURL)
                console.log("newid", re.data)
                return re.data
            } catch (e) {
                this.logError(e)
            }
        },
        setVipSource: function (vipSource, id) {
            return async function (vipInfo) {

                return Promise.resolve("vipSource updated")
            }
        }
    },
    template: '<div>\
    <search-bar v-if="currentTabComponent==\'vip-table\'" :is-load="searchIsLoad" :options="vipEmptyExp" @search="searchMethod">\
    <a v-if="currentTabComponent==\'vip-table\'" class="button is-primary is-pulled-right" @click="mountNewInfo">New</a>\
    </search-bar>\
    <div id="inputField" class="is-size-5 notification has-background-white" :class="{box:currentTabComponent==\'vip-table\'}">\
        <button v-if="currentTabComponent==\'vip-info\'" class="delete" v-on:click="mountVipTable"></button>\
        <component class="has-background-white" v-bind:is="currentTabComponent" v-bind="vProps"\
            v-on:[v_on_event]="vOnFunc">\
            <template v-slot="slotProps">\
            <div class="is-pulled-right">\
                <button class="button is-medium is-info" @click="buttonDetail(slotProps.trVip.vip)">detail</button>\
                <button class="button is-medium is-danger" @click="buttonDelete(slotProps.trVip.index,slotProps.trVip.vip)">delete</button>\
            </div>\
            </template>\
        </component>\
    </div>\
</div>'
})