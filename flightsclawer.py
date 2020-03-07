# -*- coding: UTF-8 -*-
import json
import os
import io
import re
import numpy as np
import pandas as pd
import requests
import lxml
import time
import argparse
from bs4 import BeautifulSoup

TIME_STA = time.strftime("%Y-%m-%d-%H%M", time.localtime())
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
LOG_DIR = os.path.join(BASE_DIR, 'log')
DATA_DIR = os.path.join(BASE_DIR, 'data')
DATA_CITY_DIR = os.path.join(DATA_DIR, 'city')
if not os.path.isdir(DATA_CITY_DIR): os.makedirs(DATA_CITY_DIR)
if not os.path.isdir(LOG_DIR): os.makedirs(LOG_DIR)
LOG_FOUT = open(os.path.join(LOG_DIR, 'log-{0}.txt').format(TIME_STA), 'w')

root_domin = "https://flights.ctrip.com"
start_url = "https://flights.ctrip.com/schedule"

def log_string(out_str:str, log_fout=LOG_FOUT):
    log_fout.write(out_str)
    log_fout.write('\n')
    log_fout.flush()
    print(out_str)

def get_soup(url:str)-> BeautifulSoup:
    heads = {'User-Agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36'}
    html = requests.get(url,heads)
    return BeautifulSoup(html.text,"lxml")

def get_json(filename:str)->dict:
    with open(filename, 'r') as f:
        return json.load(f)

def write_json(filename:str,collect:dict):
    with open(filename,"w+",encoding="utf-8")as f:
        json.dump(collect,f,ensure_ascii=False)

def append_json(filename:str,collect:dict):
    with open(filename,"a+",encoding="utf-8")as f:
        json.dump(collect,f,ensure_ascii=False)

def areas_crawler(url:str)->dict:
    sp = get_soup(url)
    collect = {}
    for item in sp.select('div[class="m"]>a'):
        sval = str(item.string)
        collect[sval] = item['href']
    return collect

def airlines_crawler(url:str)->dict:
    collect = {}
    try:
        sp = get_soup(url)
        for item in sp.find(id='ulD_Domestic').select('div[class="m"]>a'):
            sval = str(item.string)
            collect[sval] = item['href']
    except BaseException as err:
        print(err)
    return collect

def pagelist(sp:BeautifulSoup)->list:
    val = sp.find(class_='schedule_page_list')
    if(val!=None):
        atags = val.find_all('a')
        links = []
        for atag in atags:
            links.append(atag['href'])
        return links[1:]

def flights_crawler(url:str,firstpage:bool,da={})->list:
    collect = []
    try:
        sp = get_soup(url)
        if(firstpage):
            title = sp.find('h4',class_='result_type').string
            match = re.search("(\S+)到(\S+)",title)
            da['depart']=match.group(1)
            da['arrive']=match.group(2)

            patten = "/([a-z]+).([a-z]+).html"
            match = re.search(patten,url)
            da['departcode']=match.group(1)
            da['arrivecode']=match.group(2)

        flights = sp.find(id='flt1').find_all('tr')
        for item in flights:
            obj = {}
            obj['航空公司']=item.td.a.strong['data-description']
            obj['航班号']=item.td.a.strong.string
            obj['出发时间']=item.find('td',class_='depart').strong.string
            obj['出发机场']=item.find('td',class_='depart').div['title']
            obj['出发城市']=da['depart']
            obj['出发城市代码']=da['departcode']

            stopspan = item.find('td',class_='stop').find('span',class_='gray')
            obj['经停']= ("None" if stopspan==None else stopspan.string)

            obj['到达时间']=item.find('td',class_='arrive').strong.string
            obj['到达机场']=item.find('td',class_='arrive').div['title']
            obj['到达城市']=da['arrive']
            obj['到达城市代码']=da['arrivecode']

            punc=item.find('td',class_='punctuality').string
            obj['准点率']= (np.NaN if punc=='-' else punc)

            pricespan = item.find('td',class_='price_col').span
            obj['价格(￥)']=(np.NaN if pricespan==None else pricespan.contents[1].string)

            obj['URL']=root_domin+item.td.a['href']
            collect.append(obj)
        if(firstpage):
            pages = pagelist(sp)
            if(pages!=None):
                for link in pages:
                    collect.extend(flights_crawler(link,False,da))
    except BaseException as err:
        raise err
    return collect

def save_cityflight_urls_byfolder(citys:dict):
    for key,val in citys.items():
        print("数据已爬取：{0}".format(key))
        cityflight = areas_crawler(root_domin+val)
        write_json('data//city//{0}.json'.format(key),cityflight)

def save_cityflight_urls_inone(citys:dict)->dict:
    collect = {}
    for key,val in citys.items():
        print("数据已爬取：{0}".format(key))
        flight_routes = airlines_crawler(root_domin+val)
        collect.update(flight_routes)
    write_json('data//allcity.json',collect)
    return collect

def get_cityflight_urls(citys:dict)->dict:
    if(os.path.exists('data//allcity.json')):
        return get_json('data//allcity.json')
    return save_cityflight_urls_inone(citys)

def get_citys_urls()->dict:
    if(os.path.exists('data//root.json')):
        return get_json('data//root.json')
    citys = areas_crawler(start_url)
    write_json('data//root.json',citys)
    return citys
def log_df(csvname:str):
    try:
        df = pd.read_csv(csvname)
        df['准点率'] = df['准点率'].str.strip('%').astype(float)/100
        
        buf = io.StringIO()
        df.info(buf = buf)
        log_string('-----数据结构信息-----')
        log_string(buf.getvalue(),LOG_FOUT)

        
        des = df.describe().to_string()
        print(type(des))
        log_string('-----数据统计信息-----')
        log_string(des,LOG_FOUT)
    except BaseException as err:
        log_string(err)

parser = argparse.ArgumentParser()
parser.add_argument('--maxnum',type=int, default=9999)
parser.add_argument('--filename',type=str, default='{0}.csv'.format(TIME_STA))
FLAGS = parser.parse_args()

MAX_NUM = FLAGS.maxnum
FILE_NAME = os.path.join(DATA_DIR, FLAGS.filename)
if __name__=='__main__':
    log_string('参数:')
    log_string('\t爬取航线最大值:{0}'.format(MAX_NUM))
    log_string('\t文件路径:{0}'.format(FILE_NAME))
    citys = get_citys_urls()
    log_string("数据已爬取：{0}".format("城市列表"))
    airlines = get_cityflight_urls(citys)
    log_string("数据已爬取：{0}".format("航线列表"))
    cot = 1
    ind = 0
    for name,url in airlines.items():
        if(MAX_NUM!=-1 and cot>MAX_NUM):break
        try:
            if(cot==1):
                dict_ = flights_crawler(url,True)
                df = pd.DataFrame(dict_)
                df.to_csv(FILE_NAME,encoding="utf-8-sig")
                ind += len(dict_)
            else:
                dict_ = flights_crawler(url,True)
                # df =df.append(dict_)
                df = pd.DataFrame(dict_,index = list(range(ind,ind+len(dict_))))
                df.to_csv(FILE_NAME,mode='a+',header = False,encoding="utf-8-sig")
                ind += len(dict_)
            log_string("{0}.航线数据已爬取：{1}".format(cot,name))
            cot+=1
        except BaseException as err:
            log_string("{0}.航线数据爬取错误：{1}:".format(cot,name))
            log_string("/t{0}".format(err))
    log_df(FILE_NAME)
    # df.to_csv(FILE_NAME,encoding="utf-8-sig")

log_df('data//2019-11-11-1354.csv')

# save_cityflight_urls_inone(citys)
# print(root_domin+suf)

# try:
#     df = pd.DataFrame()
#     i = 1
#     for url in areas_crawler(root_url).values():
#         val = areas_crawler(root+url).items()
#         if(i==0):df = pd.DataFrame(val)
#         else:df.append(val)
#         print(i)
#         if(i>3) :break
#     print(df)
# except Exception as err:
#     print(err)


# urlb = "http://flights.ctrip.com/schedule/bjs.rlk.html"
# resb = flights_crawler(urlb,True)
# urlc = "http://flights.ctrip.com/schedule/bjs.aku.html"
# resc = flights_crawler(urlc,True)
# dfb = pd.DataFrame(resb)
# # dfb = dfb.append(resc,ignore_index = True)
# # dfb.to_csv('ccc.csv',encoding="utf-8-sig")

# # urlc = "http://flights.ctrip.com/schedule/bjs.aku.html"
# # resc = flights_crawler(urlc)
# l = len(resb)
# dfc = pd.DataFrame(resc,index=list(range(l,l+len(resc))))
# dfb.to_csv('ccc.csv',encoding="utf-8-sig")
# dfc.to_csv('ccc.csv',mode='a+',header = False,encoding="utf-8-sig")


# print(html.text)
# fileoj = open("exp.html","wb+")
# fileoj.write(html.content)


# print(sp.find_all(class_="m"))





