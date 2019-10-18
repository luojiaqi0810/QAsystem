package com.nowcoder.wenda.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

/**
 * @author LuoJiaQi
 * @Date 2019/10/18
 * @Time 14:44
 */
public class Feed {
    private int id;
    private int type;//比如评论，关注等。。
    private int userId;
    private Date createdDate;

    //JSON，比如A关注了B，所以数据里要记录A，B的id；或者A评论了B的一个问题，就需要记录A，B是谁，问题是什么，评论内容是什么。
    //因为不同类型的新鲜事，传输的数据不同，所以用JSON
    private String data;

    private JSONObject dataJSON = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        dataJSON = JSONObject.parseObject(data);
    }

    public String get(String key) {
        return dataJSON == null ? null : dataJSON.getString(key);
    }
}