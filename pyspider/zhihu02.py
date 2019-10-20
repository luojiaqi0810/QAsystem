#!/usr/bin/env python
# -*- encoding: utf-8 -*-
# Created on 2019-03-01 17:55:37
# Project: newtest

from pyspider.libs.base_handler import *
import random
import MySQLdb


class Handler(BaseHandler):
    # 基础配置
    crawl_config = {
        'itag': 'v1',
        'headers': {
            # 伪装成Google爬虫
            'User-Agent': 'GoogleBot',
            'Host': 'www.zhihu.com',
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
        }
    }

    # 配置数据库基本参数
    def __init__(self):
        self.db = MySQLdb.connect('localhost', 'root', 'admin', 'pyspider', charset='utf8')

    # 将爬取结果插入数据库
    def add_question(self, title, content):
        try:
            cursor = self.db.cursor()
            SQL = 'insert into question(title, content, user_id, created_date) values ("%s","%s",%d, %s)' % (
            title, content, random.randint(1, 10), 'now()');
            print SQL
            cursor.execute(SQL)
            qid = cursor.lastrowid
            self.db.commit()
        except Exception, e:
            print e
            self.db.rollback()
        return 0

    # 每天执行一次        validate_cert=False是省略ssl安全检查
    @every(minutes=24 * 60)
    def on_start(self):
        self.crawl('https://www.zhihu.com/topic/19561132/top-answers', callback=self.index_page, validate_cert=False)

    # 十天以内不重复爬
    @config(age=10 * 24 * 60 * 60)
    def index_page(self, response):
        # 之前页面选取的爬取的条目 这里用css选择器写法 注意的是留空格的class样式 类选择器中间有空格代表后代选择，这里写法是同级，所以把空格换成点    也就是说pyspider的子元素是用空格空开，css的子元素和它不一样
        for each in response.doc('a[data-za-detail-view-element_name="Title"]').items():
            self.crawl(each.attr.href, callback=self.detail_page, validate_cert=False)

    @config(priority=2)
    def detail_page(self, response):
        items = response.doc('div.RichText.ztext').items()
        # detal页面的话题标题
        title = response.doc('h1.QuestionHeader-title').text()
        # 话题内容
        html = response.doc('span.RichText.ztext.CopyrightRichText-richText').html()
        if html == None:
            html = ''
        # 避免转义错误 合法的规则插入数据库
        html = html.replace('"', '\\"')
        qid = self.add_question(title, html)
        return {
            "url": response.url,
            "title": response.doc('title').text(),
        }
