Vue.component('func-purhis', {
    data: function () {
        return {
            //URL
            getAllPHURL: '/comvipsys_war/getAllPurHis',
            searchPHDURL: '/comvipsys_war/searchPHD',

            //UI Animation
            searchIsLoad: false,

            //postData
            postTables: null,

            purHisEmptyEXp: {
                id: null,
                vipid: null,
                pointsChange: null,
                purDate: null
            },
            purHisHead: {
                id: "ID",
                vipid: "VIPID",
                name: "姓名",
                pointsChange: "本消费获得积分",
                points: "当前积分",
                purDate: "消费日期"
            },
            purHisSearchHints: {
                all: '',
                id: '请输入消费记录编号',
                vipid: '请输入vip编号',
                purDate: '年/月/日 格式:yyyy/mm/dd',
                pointsChange: '请输入要查询的积分变换'
            }
        }
    },
    created: function () {
        console.log("pHEXP", this.purHisSearchHints)
        postTables = this.getAllPurHis()
    },
    methods: {
        getAllPurHis: async function () {
            this.searchIsLoad = true
            try {
                let response = await axios.get(this.getAllPHURL)
                console.log("getAllPh response: ", response)
                this.postTables = response.data
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
        searchMethod: function (val) {
            console.log("search bar emit: ", val)
            if (val.selected == 'all') {
                this.getAllPurHis()
            } else {
                this.searchAspect(async () => {
                    let key = val.selected
                    let value = val.value
                    let postOj = {key: key, [key]: value}
                    console.log("postOj", postOj)
                    let response = await axios.post(this.searchPHDURL, postOj)
                    return response
                })
            }
            //TODO get diff PurHis
            console.log("searchMethod")
        },
        searchAspect: async function (request) {
            //before
            this.searchIsLoad = true
            try {
                let response = await request()
                //after
                console.log("response:", response)
                this.postTables = response.data
            } catch (error) {
                if (error.response)
                    this.handleError(error.response.data)
                else
                    alert("加载失败")
                this.logError(error)
            } finally {
                this.searchIsLoad = false
            }
        }
    },
    template: '<div>\
    <search-bar :is-load="searchIsLoad" :text-placeholder="purHisSearchHints" :options="purHisEmptyEXp" :new-button="false" @search="searchMethod">\
    </search-bar>\
    <div class="is-size-5 notification has-background-white box">\
    <com-table :tables="postTables" :title-head="purHisHead"></com-table>\
    </div>\
        </div>'
})