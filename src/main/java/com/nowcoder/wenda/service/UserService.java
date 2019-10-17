package com.nowcoder.wenda.service;

import com.nowcoder.wenda.dao.LoginTicketDAO;
import com.nowcoder.wenda.dao.UserDAO;
import com.nowcoder.wenda.model.LoginTicket;
import com.nowcoder.wenda.model.User;
import com.nowcoder.wenda.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.krb5.internal.Ticket;

import java.util.*;

/**
 * @author LuoJiaQi
 * @Date 2019/10/13
 * @Time 19:09
 */
@Service
public class UserService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;

    public User selectByName(String name) {
        return userDAO.selectByName(name);
    }

    /**
     * 注册
     *
     * @param username
     * @param password
     * @return
     */
    //char[] valid = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '*', '#', '-'};
    public Map<String, String> register(String username, String password) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("msg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空");
            return map;
        }
/*
        1) 密码控制只能输入字母、数字、特殊符号(~!@#$%^&*()_+[]{}|\;:'",./<>?)
        2) 长度 6-16 位，必须包括字母、数字、特殊符号中的2种
        3) 密码不能包含用户名信息
*/

        //判断密码是否包含数字：包含返回1，不包含返回0
        int i = password.matches(".*\\d+.*") ? 1 : 0;

        //判断密码是否包含字母：包含返回1，不包含返回0
        int j = password.matches(".*[a-zA-Z]+.*") ? 1 : 0;

        //判断密码是否包含特殊符号(~!@#$%^&*()_+|<>,.?/:;'[]{}\)：包含返回1，不包含返回0
        if (password.matches(".*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"]+.*")) {
            map.put("msg", "密码中不能包含特殊符号");
            return map;
        }

        //判断密码长度是否在6-16位
        if (password.length() < 6 || password.length() > 16) {
            map.put("msg", "密码长度需为6到16位");
        }

        //判断密码中是否包含用户名
        if (password.contains(username)) {
            map.put("msg", "密码中不能包含用户名");
            return map;
        }


        User user = userDAO.selectByName(username);
        if (user != null) {
            map.put("msg", "用户名已被注册");
            return map;
        }

        user = new User();
        user.setName(username);
        //设置用户头像
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",
                new Random().nextInt(1000)));
        //密码加salt
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        //密码加密，先给原密码加salt，然后用MD5加密
        user.setPassword(WendaUtil.MD5(password + user.getSalt()));

        userDAO.addUser(user);

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);

        return map;

    }

    /**
     * 用户登陆
     * @param username
     * @param password
     * @return
     */
    public Map<String, Object> login (String username, String password) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("msg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空");
            return map;
        }


        User user = userDAO.selectByName(username);
        //判断用户名是否存在
        if (user == null) {
            map.put("msg", "用户名不存在");
            return map;
        }

        //判读密码是否正确
        if (!WendaUtil.MD5(password + user.getSalt()).equals(user.getPassword())) {
            map.put("msg", "密码错误");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        map.put("userId", user.getId());//传递userId，方便后面调用
        return map;

    }

    /**
     * 登陆时为用户加上ticket
     * @param userId
     * @return
     */
    public String addLoginTicket(int userId) {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date now = new Date();
        now.setTime(3600 * 24 * 100 + now.getTime());//有效期设为100天
        loginTicket.setExpired(now);
        loginTicket.setStatus(0);//0表示状态有效
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    /**
     * 退出
     * @param ticket
     */
    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
    }

    public User getUser(int id) {
        return userDAO.selectById(id);
    }
}