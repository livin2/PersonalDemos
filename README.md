

# 国内航班数据爬虫



本爬虫的目标是从携程爬取所有国内航班的信息，清洗并整理为结构化数据。本文档是以0基础教程的形式写的.

据爬取过程中可能请求的链接的域名与目录为:

- flights.ctrip.com/schedule/ 
- flights.ctrip.com/schedule/{出发城市代码}..html 
- flights.ctrip.com/domestic/schedule/{出发城市代码}.{到达城市代码}.{页码}.html

 对照 https://flights.ctrip.com/robots.txt 确认上述链接网站允许爬。

最后确认的时间为2019-11-11。



## 请求与结构化内容

​	本爬虫程序采用python的requests库来获取请求，采用BeautifulSoup与lxml库来将提取请求内容，按照页面文档树将页面内容结构化并构建为python对象。为了避免爬虫被网站所屏蔽，请求还要设置浏览器请求所使用的User-Agent。以下为chrome浏览器请求目标网址所使用的User-Agent:

```
Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36
```

对于任意URL链接，便可以通过以下代码结构化为python对象:

```python
heads = {'User-Agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36'}
return BeautifulSoup(requests.get(url,heads).text,"lxml")
```

## 获取所有国内城市页面的链接

本功能模块主要从国内出发航班查询的目录页面 https://flights.ctrip.com/schedule/ 爬取该页面所有的指向包含有某地区出发航线信息的链接，并结构化为键值对，保存为json文件。

### 分析HTML

![1.png](https://s2.ax1x.com/2020/03/07/3jQKt1.png)

如上图为 https://flights.ctrip.com/schedule/ 页面，我们希望得到是图中所有[xx航班]及其对应的链接。以安庆航班为例,最后期望的输出是:

```json
{"安庆航班": "https://flights.ctrip.com/schedule/aqg..html"}
```

我们使用chrome的开发工具定位到A这一行的html源码，其html源码如下:

```html
<ul class="letter_list">
  <li>
    <span class="letter current" id="A">A</span>
    <div class="m">
      <a href="/schedule/aqg..html">安庆航班</a>
      <a href="/schedule/ava..html">安顺航班</a>
      <a href="/schedule/aka..html">安康航班</a>
      <a href="/schedule/aku..html">阿克苏航班</a>
      <a href="/schedule/aat..html">阿勒泰航班</a>
      <a href="/schedule/aog..html">鞍山航班</a>
      <a href="/schedule/ngq..html">阿里航班</a>
      <a href="/schedule/yie..html">阿尔山航班</a>
      <a href="/schedule/axf..html">阿拉善左旗航班</a>
      <a href="/schedule/rht..html">阿拉善右旗航班</a>
    </div>
  </li>
</ul>
```

其中单个a标签的路径大致为`ul[class="letter_list"]>li>div[class="m"]>a`

从html源码中我们可以发现直接提取并结构化的链接缺少主域名，故后续业务步骤使用时要进行相应处理。

### 提取结构化数据

我们了解html的结构之后,那么我们就可以通过各类标签从BeautifulSoup对象选择我们要的标签,并结构化为键值对,python伪代码如下：

```python
dict = {}
for a in soup_object.select('div[class="m"]>a'):
    dict[a.string] = a['href']
    return dict
```

## 获取从某城市出发的航线的链接

本功能模块主要从某城市航班查询的目录页面 https://flights.ctrip.com/schedule/xxx..html(其中xxx一般为出发城市代码) 爬取该页面所有的指向包含有某航线具体信息的链接，并结构化为键值对，保存为json文件。

### 分析HTML

![2.png](https://s2.ax1x.com/2020/03/07/3jQrjS.png)

如上图为 https://flights.ctrip.com/schedule/aqg..html 页面，我们希望得到是图中所有[安庆-xx]及其对应的链接。以安庆-北京为例,最后期望的输出是:

```json
{"安庆-北京": "http://flights.ctrip.com/schedule/aqg.bjs.html"}
```

我们使用chrome的开发工具定位到安庆-北京这一行的html源码，其html源码如下:

```html
<ul class="letter_list" id="ulD_Domestic" style="display:">
  <li>
    <span class="letter" id="B">B</span>
    <div class="m">
      <a href="http://flights.ctrip.com/schedule/aqg.bjs.html">安庆-北京</a>
    </div>
  </li>
  <li>
    <span class="letter" id="F">F</span>
    <div class="m">
      <a href="http://flights.ctrip.com/schedule/aqg.foc.html">安庆-福州</a>
    </div>
  </li>
</ul>

```

其中单个a标签的路径大致为`ul[id="ulD_Domestic"]>li>div[class="m"]>a`

### 提取结构化数据

我们了解html的结构之后,那么我们就可以通过各类标签从BeautifulSoup对象选择我们要的标签,并结构化为键值对,python伪代码如下：

```python
dict = {}
for a in soup_object.find(id='ulD_Domestic').select('div[class="m"]>a'):
    dict[a.string] = a['href']
return dict
```

## 获取某航线的所有航班信息

本功能模块主要从某航线的页面 https://flights.ctrip.com/domestic/schedule/aaa.bbb.p?.html(其中aaa一般为出发城市代码,bbb为一般为到达城市代码,?为页码) 爬取该页面所有航班以及相应信息，结构化为键值对或表格行数据，导出为csv或json。

### 分析HTML

![03.png](https://s2.ax1x.com/2020/03/07/3jQWhq.png)

如上图为  https://flights.ctrip.com/domestic/schedule/aqg.kwe.p1.html 页面，我们希望得到是图中所有航班相关的信息，输出大致如下:

| 航空公司 | 航班号 | 出发时间 | 出发机场 | 出发城市 | 经停 | 到达时间 | 到达机场 | 到达城市 | 准点率 | 价格(￥) |
| -------- | ------ | -------- | -------- | -------- | ---- | -------- | -------- | -------- | ------ | -------- |
| 海南航空 | HU1564 | 22:05 | 安庆天柱山机场 | 安庆 | 无 | 0:40 | 贵阳龙洞堡国际机场T2 | 贵阳 | 100% | 560  |
| 金鹏航空 | Y87564 | 22:05 | 安庆天柱山机场 | 安庆 | 无 | 0:40 | 贵阳龙洞堡国际机场T2 | 贵阳 | 100% | 203  |

我们使用chrome的开发工具获取包含航班信息的表格的html源码，其html源码如下:

```html
<div class="result_m">
  <table class="result_m_content">
    <tbody param="07:30|SHA|08:40|BJS|MU|M|100" id="flt1">
      <tr data-defer="form" data-submit-type="line">
        <td align="left" class="flight_logo">
          <a href="/schedule/hu1564.html" target="_blank">
            <strong
              class="pubFlights_hu "
              data-defer="jmpPlugin"
              data-description="海南航空"
              >HU1564</strong
            ></a
          ><br />
          <span class="gray" code="" data-defer="fltTypeJmp"
            >机型:<span class="abbr" style="color:black;"></span
          ></span>
        </td>
        <td align="right" class="depart">
          <strong class="time">22:05</strong>
          <div class="airport" title="安庆天柱山机场">
            安庆天柱山机场
          </div>
        </td>
        <td align="center" valign="top" class="stop">
          <div class="arrow"></div>
        </td>
        <td align="left" class="arrive">
          <strong class="time">00:40</strong>
          <div class="airport" title="贵阳龙洞堡国际机场T2">
            贵阳龙洞堡国际机场T2
          </div>
        </td>
        <td class="punctuality" align="center">100%</td>
        <td class="price_col" align="center">
          <span class="price"><dfn>&yen;</dfn>560<em>起</em></span>
        </td>
      </tr>
      <tr data-defer="form" data-submit-type="line">
        <td align="left" class="flight_logo">
          <a href="/schedule/y87564.html" target="_blank">
            <strong
              class="pubFlights_y8 "
              data-defer="jmpPlugin"
              data-description="金鹏航空"
              >Y87564</strong
            ></a
          ><br />
          <span class="gray" code="" data-defer="fltTypeJmp"
            >机型:<span class="abbr" style="color:black;"></span
          ></span>
        </td>
        <td align="right" class="depart">
          <strong class="time">22:05</strong>
          <div class="airport" title="安庆天柱山机场">
            安庆天柱山机场
          </div>
        </td>
        <td align="center" valign="top" class="stop">
          <div class="arrow"></div>
        </td>
        <td align="left" class="arrive">
          <strong class="time">00:40</strong>
          <div class="airport" title="贵阳龙洞堡国际机场T2">
            贵阳龙洞堡国际机场T2
          </div>
        </td>
        <td class="punctuality" align="center">100%</td>
        <td class="price_col" align="center">
          <span class="price"><dfn>&yen;</dfn>203<em>起</em></span>
        </td>
      </tr>
    </tbody>
  </table>
</div>

```

其中我们需要的数据所在标签相应的路径为:

- 表格行:`div[class="result_m"]>table>tbody[id="flt1"]>tr`
- 航空公司:`tr>td[class="flight_logo"]>a>strong["data-description"]`
- 航班号:`tr>td[class="flight_logo"]>a>strong.string`
- 出发时间:`tr>td[class="depart"]>strong[class="time"].string`
- 出发机场: `tr>td[class="depart"]>div[class="airport"].string` 
- 经停:`tr>td[class="stop"]>` 
- 到达时间:`tr>td[class="arrive"]>strong[class="time"].string`
- 到达机场:`tr>td[class="arrive"]>div[class="airport"].string` 
- 准点率:`tr>td[class="punctuality"].string`
- 价格:`tr>td[class="price_col"]>span[class="price"].contents[1].string`

### 提取结构化数据

我们了解html的结构之后,那么我们就可以通过各类标签从BeautifulSoup对象选择我们要的标签,并将数据结构化为键值对,python伪代码如下：

```python
list = []
trs = soup_object.find(id='flt1').find_all('tr')
for tr in trs:
    dict = {}
    dict['航空公司']=tr.td.a.strong['data-description']
    dict['航班号']=tr.td.a.strong.string
    dict['出发时间']=tr.find('td',class_='depart').strong.string
    dict['出发机场']=tr.find('td',class_='depart').div['title']
    dict['经停']= tr.find('td',class_='stop').span.string
    dict['到达时间']=tr.find('td',class_='arrive').strong.string
    dict['到达机场']=tr.find('td',class_='arrive').div['title']
    dict['准点率']=tr.find('td',class_='punctuality').string
    dict['价格(￥)']=tr.find('td',class_='price_col').span.contents[1].string
    list.append(dict)
return list
```

## 获取某航线页面的分页链接

本功能模块主要从某航线的第一页 https://flights.ctrip.com/domestic/schedule/aaa.bbb.p1.html(其中aaa一般为出发城市代码,bbb为一般为到达城市代码) 爬取该航线其它页面，并结构化为链接列表。

### 分析HTML

![05.png](https://s2.ax1x.com/2020/03/07/3jQqE9.png)

如上图为  https://flights.ctrip.com/domestic/schedule/bjs.ctu.p1.html  页面，我们希望得到是图中所有数字页码对应的链接，返回的是包含除第一页外所有链接的字符串列表。

我们使用chrome的开发工具获取页码列表的html源码，其html源码如下:

```python
<div class="schedule_page_list clearfix">
  <a href="http://flights.ctrip.com/schedule/bjs.ctu.html" class="current">1</a>
  <a href="http://flights.ctrip.com/schedule/bjs.ctu.p2.html">2</a>
  <a href="http://flights.ctrip.com/schedule/bjs.ctu.p3.html">3</a>
  <a href="http://flights.ctrip.com/schedule/bjs.ctu.p4.html">4</a>
  <a href="http://flights.ctrip.com/schedule/bjs.ctu.p5.html">5</a>
  <a href="http://flights.ctrip.com/schedule/bjs.ctu.p6.html">6</a>
  <a href="http://flights.ctrip.com/schedule/bjs.ctu.p7.html">7</a>
</div>
```

其结构简单，只需要定位到`div[class="schedule_page_list"]`迭代所有子节点`<a>`即可获取链接。

### 提取结构化数据

提取分页链接数据需要注意去掉当前页面的链接，不然可能会导致分析页面的过程无限递归。还需要注意不是所有页面都有分页，要判断是否存在分页或者做异常处理。

其python伪代码大致如下：

```python
try:
    links = []
    for a in soup_object.find(class_='schedule_page_list').find_all('a'):
        links.append(a['href'])
	return links[1:] #假设当前需要排除的是第一页
except Exception as err:
    log(err)
```

# 代码注解

初始化常量:

```python
TIME_STA = time.strftime("%Y-%m-%d-%H%M", time.localtime())
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
LOG_DIR = os.path.join(BASE_DIR, 'log')
DATA_DIR = os.path.join(BASE_DIR, 'data')
DATA_CITY_DIR = os.path.join(DATA_DIR, 'city')
LOG_FOUT = open(os.path.join(LOG_DIR, 'log-{0}.txt').format(TIME_STA), 'w')
root_domin = "https://flights.ctrip.com"
start_url = "https://flights.ctrip.com/schedule"
```

辅助的工具函数定义:

```python
def log_string(out_str:str, log_fout=LOG_FOUT) #打印输出到标准流与log_fout文件流
def get_soup(url:str)-> BeautifulSoup #根据url请求页面并构造BeautifulSoup对象
def get_json(filename:str)->dict #从json文件读取数据为字典对象
def write_json(filename:str,collect:dict) #将字典对象序列化并写入json文件
def append_json(filename:str,collect:dict) #往json文件追加序列化后的字典对象
```

主要功能实现的函数定义与解释:

```python
def areas_crawler(url=start_url:str)->dict
"""
获取所有国内城市页面的链接 依照设计实现
@url:str 默认值为上面定义start_url 
@return:dict 返回{'xx航班':'...html',...}形式的字典对象
"""

def airlines_crawler(url:str)->dict
"""
获取从某城市出发的航线的链接 在设计的基础上添加异常处理
@url:str 某个城市的链接 由areas_crawler(start_url)返回的字典中的值
@return:dict 返回{'xxx-xxx':'...html',...}形式的字典对象
"""

def pagelist(sp:BeautifulSoup)->list
"""
获取某航线页面的分页链接 由flights_crawler()调用 
@sp:BeautifulSoup 当前页面结构化后BeautifulSoup对象
@return:list 返回除当前页外所有链接的列表
"""

def flights_crawler(url:str,firstpage:bool,da={})->list
"""
获取某航线的所有航班信息  
若有多个分页 调用pagelist()获取其它页面链接
递归调用flights_crawler()返回其它页面的数据
最后将多个页面的数据整合进一个list对象中返回
@url:str 某航线的页面链接 由airlines_crawler()返回的字典中的值
@firstpage:bool 当前页面是否是第一页
@da:dict 包含航线出发城市与到达城市信息的字典
@return:list 返回包含若干字典的列表 每个字典对象包含一条航班信息
"""
    
def save_cityflight_urls_byfolder(citys:dict)
"""
保存某城市出发或到达的航线的链接为json文件 
保存在/data/city/文件夹 以[城市名]航班.json命名
@citys:dict 某个城市的链接 由areas_crawler(start_url)返回的字典中的值
"""

def save_cityflight_urls_inone(citys:dict)->dict
"""
保存所有城市从该城市出发航线的链接为一个json文件 
保存为/data/allcity.json 
@citys:dict 某个城市的链接 由areas_crawler(start_url)返回的字典中的值
@return:dict 返回包含所有航线的{'xxx-xxx':'...html',...}形式的字典对象
"""

def get_citys_urls()->dict
"""
获取所有国内城市页面的链接 如果本地有保存的json文件则从本地读取
否则调用areas_crawler(start_url)爬取并保存为本地json文件
@return:dict 返回{'[城市名]航班':'...html',...}形式的字典对象
"""

def get_cityflight_urls(citys:dict)->dict
"""
获取从某城市出发的航线的链接 如果本地有保存的json文件则从本地读取
否则调用save_cityflight_urls_inone(citys)爬取并保存为本地json文件
@citys:dict 某个城市的链接 由get_citys_urls()返回的字典中的值
"""

def log_df(csvname:str)
"""
对最后爬取完成并已经保存为csv的文件通过pandas库进行数据统计
@csvname:str csv文件路径
"""
```

入口函数main主要逻辑:

```python
#模块入口main定义两个参数
#--maxnum 用来指定最多爬取多少航线的数据 默认9999
#--filename 用来指定航班数据要保存的文件名 默认是当前日期-时间.csv
#main大致流程(省略输出日志与异常处理):
citys = get_citys_urls()
airlines = get_cityflight_urls(citys)
cot = 1
for name,url in airlines.items():
    if(cot>MAX_NUM):break
    if(cot==1):
        pd.DataFrame(flights_crawler(url,True))\
        .to_csv(FILE_NAME)
	else:
        pd.DataFrame(flights_crawler(url,True))\
        .to_csv(FILE_NAME,mode='a+',header = False)
	cot+=1
log_df(FILE_NAME)
```

# 运行结果

以默认参数爬取网站所有数据的结果见附近data文件中的csv文件已经log文件夹中对应的日志。

以下为了方便展示统计信息最大航线树设置为5:

`python flightsclawer.py --maxnum 5 --filename five`

![cmd1](https://s2.ax1x.com/2020/03/07/3jlk4I.png)

![cmd2](https://s2.ax1x.com/2020/03/07/3jlJ2V.png)