package com.nowcoder.wenda.model;

import org.springframework.stereotype.Component;

/**
 * @author LuoJiaQi
 * @Date 2019/10/14
 * @Time 13:12
 */

@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }
}