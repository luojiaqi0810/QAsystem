package com.nowcoder.wenda.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LuoJiaQi
 * @Date 2019/10/13
 * @Time 19:16
 */
public class ViewObject {

    private Map<String, Object> objs = new HashMap<>();

    public void set(String key, Object value) {
        objs.put(key, value);
    }

    public Object get(String key) {
        return objs.get(key);
    }
}