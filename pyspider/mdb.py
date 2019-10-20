# -*- coding: utf-8 -*-
import MySQLdb
import random

if __name__ == '__main__':
    # 这里只能写utf8，写utf-8报错
    db = MySQLdb.connect('localhost','root','admin','wenda',charset='utf8')
    try:

        cursor = db.cursor()
        '''
        sql = 'insert into question(title,content,user_id,created_date,comment_count)'\
              +' values("xxx","xxx",1,now(),0)'
        cursor.execute(sql)
        qid = cursor.lastrowid
        db.commit()
        print qid
        '''

        sql = 'select * from question order by id desc limit 2'
        cursor.execute(sql)
        for each in cursor.fetchall():
            for row in each:
                print row

        db.commit()

    except Exception, e:
        print e
        db.rollback()
    db.close()