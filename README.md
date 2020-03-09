# 虚拟文件系统
 [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) 

模拟了外存与内存两级硬件的文件系统，模拟实现了文件系统的数据块，文件索引结点，设备超级块等数据结构以及数据块的分配回收等底层操作。提供简单的CLI,提供了初级的文件增删、文件目录、文本编辑、写入权限控制等功能。

## 使用

程序会在初次使用的时候生成一个DEVF，作为虚拟外存文件(类似于vhd)，默认的大小为500个数据块,200个文件索引节点，详见源码Dev类的构造函数。`:devInfo`和`:ramInfo`并非存取命令而是输出当前外存与RAM的设备状态。

命令一概以:开头

```c
:dir			//显示当前目录所有文件
:q			//退出系统
:/			//返回根目录
:open			//打开文件/目录 文件将读入到RAM
:close			//关闭文件	会将文件从ram存回dev
:back			//返回上一级目录 关闭当前文件夹
:read			//读取已经打开的文件
:login			//登录
:check			//检查文件拥有者
:write			//往已经打开的文件中写入 只有文件拥有者可写入 写入以#结束
:newFile		//在当前目录新建文件
:deleteFile		//在当前目录删除文件
:newFolder		//在当前目录新建文件夹
:deleteFolder	        //在当前目录删除文件夹		
:devInfo		//输出设备信息到txt文件
:ramInfo		//输出内存信息到txt文件
```

## 样例

```
>:login
user name:a
>help
type ':help' to get help
>:help
:dir    :q      :\
:open   :close  :back
:read   :write  :login
:newFile        :deleteFile     :check
:newFolder      :deleteFolder
:devInfo        :ramInfo
>:dir
        -bb.t            idx:1
        -howIwork.t              idx:0
>:write
file:howIwork.t
Failed: file cannot be found in RAM
>:open
file/folder:howIwork.t
opened
>:write
file:howIwork.t
Failed: no premisson
>:check
file:howIwork.t
user
>:read
file:howIwork.t


 +-------+         +-------+         +-------+
 |       +--open-->+       +--read-->+       |
 |  DEV  |         |  RAM  |         |  CLI  |
 |       +<-close--+       +<-write--+       |
 +-------+         +-------+         +-------+

>:newFolder
folder:tmp
>:open
file/folder:tmp
opened
>:newFile
file:justfortest.t
success
>:open
file/folder:justfortest.t
opened
>:write
file:justfortest.t
   _           _      __             _            _
  (_)         | |    / _|           | |          | |
   _ _   _ ___| |_  | |_ ___  _ __  | |_ ___  ___| |_
  | | | | / __| __| |  _/ _ \| '__| | __/ _ \/ __| __|
  | | |_| \__ \ |_  | || (_) | |    | ||  __/\__ \ |_
  | |\__,_|___/\__| |_| \___/|_|     \__\___||___/\__|
 _/ |
|__/
#
>:read
file:justfortest.t

   _           _      __             _            _
  (_)         | |    / _|           | |          | |
   _ _   _ ___| |_  | |_ ___  _ __  | |_ ___  ___| |_
  | | | | / __| __| |  _/ _ \| '__| | __/ _ \/ __| __|
  | | |_| \__ \ |_  | || (_) | |    | ||  __/\__ \ |_
  | |\__,_|___/\__| |_| \___/|_|     \__\___||___/\__|
 _/ |
|__/

>:close
file:justfortest.t
closed
>:/
>:dir
        -bb.t            idx:1
        -howIwork.t              idx:0
        -tmp
                -justfortest.t           idx:2
>:q
```

