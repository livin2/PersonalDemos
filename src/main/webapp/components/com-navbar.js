//用法示例
{/* <com-navbar sysname="超市会员管理系统" username="User">
    <template #menus>
        <a class="navbar-item has-text-link">会员信息</a>
        <a class="navbar-item ">消费记录</a>
    </template>
    <template #dropdwon>
        <a class="navbar-item">
            修改密码
        </a>
        <a class=" navbar-item ">
            <span>登出</span>
            <span class="icon is-small has-text-danger">
                <i class="fas fa-power-off"></i>
            </span>
        </a>
    </template>
</com-navbar> */
}

Vue.component('com-navbar', {
    props: {
        sysname: String,
        username: String
    },
    data: function () {
        return {
            isActive: false
        }
    },
    template: '<nav class="navbar has-shadow is-spaced" role="navigation">\
    <div class="navbar-brand ">\
        <p class="navbar-item  title">{{sysname}}</p>\
        <a role="button" class="navbar-burger" :class="{\'is-active\':isActive}" @click="isActive=!isActive" aria-label="menu" aria-expanded="false">\
            <span aria-hidden="true"></span>\
            <span aria-hidden="true"></span>\
            <span aria-hidden="true"></span>\
        </a>\
    </div>\
    <div class="navbar-menu" :class="{\'is-active\':isActive}">\
        <div class="navbar-start">\
        <slot name="menus"></slot>\
        </div>\
    </div>\
    </div>\
    <div class="navbar-menu" :class="{\'is-active\':isActive}">\
        <div class="navbar-end">\
            <div class="navbar-item has-dropdown is-hoverable">\
                <figure class="navbar-link">\
                    <span class=" is-arrowless">Hello, <strong>{{username}}</strong></span>\
                </figure>\
                <div class="navbar-dropdown is-right">\
                    <slot name="dropdwon"></slot>\
                </div>\
            </div>\
        </div>\
    </div>\
    </div>\
  </nav>'
})