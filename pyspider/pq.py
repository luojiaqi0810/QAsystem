#!/usr/bin/env python
# -*- encoding: utf-8 -*-

import io
from pyquery import PyQuery

if __name__ == '__main__':
    with io.open('v2ex.html', encoding="utf-8") as f:
        content = f.read()
    q = PyQuery(content)
    print q('title').text()

    # p = PyQuery(open('v2ex.html').read())
    # print p('title').text()
    #
    # q = PyQuery('https://www.v2ex.com/', encoding="utf-8")
    # print q('title').text()

    # for each in q('div.inner>a').items():
    #     if each.attr.href.find('tab') > 0:
    #         print 1, each.attr.href
    #
    # for each in q('#Tabs>a').items():
    #     print 2, each.attr.href
    #
    # for each in q('.cell>a[href^="/go/"]').items():
    #     print 3, each.attr.href
    # # 空格和>的区别，>只进到下一层，空格可以进到多层
    # for each in q('.cell a[href^="/go/"]').items():
    #     print 4, each.attr.href
    #
    #
    # for each in q('span.item_title>a').items():
    #     print 5, each.text()


    for each in q('a>img.avatar').items():
        print 6, each.attr.src