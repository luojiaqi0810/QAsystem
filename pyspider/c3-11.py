# -*- encoding=UTF-8 -*-

import requests
from bs4 import BeautifulSoup


def qiushibaike():
    content = requests.get('http://www.yicommunity.com/').content
    soup = BeautifulSoup(content, 'html.parser')

    for div in soup.find_all('div', {'class': 'content'}):
        print div.text.strip()


def demo_string():
    stra = 'hello world'
    print stra.capitalize()
    print stra.replace('world', 'nowcoder')
    strb = '   \n\rhello nowcode \r\n'
    print 0, strb
    print 1, strb.lstrip()
    print 2, strb.rstrip(), "xx"
    print 3, stra.startswith("hel")


def demo_list():
    lista = [1, 2, 3]
    print 1, lista
    print dir(list)
    listb = ['a', 1, 1, 1]
    print 2, listb
    lista.extend(listb)
    print 3, lista
    print 4, len(lista)
    print 5, 'a' in lista, 'b' in listb
    lista = lista + listb
    print lista

    # tuple

    t = (1, 1, 3)
    print t
    print t.count(1)


def add(a, b):
    return a + b


def sub(a, b):
    return a - b


def demo_dict():
    dicta = {4: 16, 1: 1, 2: 4, 3: 9, 'a': 'b'}
    print 1, dicta
    print 2, dicta.keys(), dicta.values()
    for key, value in dicta.items():
        print 3, key, value
    for key in dicta.keys():
        print 4, key
    print 5, dicta.has_key(1), dicta.has_key(11)

    dictb = {'+': add, '-': sub}
    print 6, dictb['+'](1, 2)
    print 7, dictb.get('-')(6, 2)

    del dictb['+']
    dictb.pop('-')
    print 9, dictb

def demo_set():
    lista = (1,2,3)
    seta = set(lista)
    print 1, seta
    setb = set((2,3,4))
    print 2, seta.intersection(setb)
    print 3, seta & setb
    print 4, seta | setb, seta.union(setb)
    print 5, seta-setb, setb - seta
    seta.add('xxx')
    print 6, seta
    print 7, len(seta)
    print seta.isdisjoint(set(('a','b')))


if __name__ == '__main__':
    # print 'hello'
    # demo_string()
    # qiushibaike()
    # demo_list()
    #demo_dict()
    demo_set()
