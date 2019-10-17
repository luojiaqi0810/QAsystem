package com.nowcoder.wenda.interceptor;

import com.nowcoder.wenda.dao.LoginTicketDAO;
import com.nowcoder.wenda.dao.UserDAO;
import com.nowcoder.wenda.model.HostHolder;
import com.nowcoder.wenda.model.LoginTicket;
import com.nowcoder.wenda.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author LuoJiaQi
 * @Date 2019/10/14
 * @Time 12:59
 * @Description 拦截器，验证用户有效性
 */

@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    LoginTicketDAO loginTicketDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    HostHolder hostHolder;

    //请求开始之前调用，如果返回false，那么请求直接结束
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中获取ticket
        String ticket = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }

        if (ticket != null) {
            LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
            //判断ticket是否存在，是否过期，是否有效
            if (loginTicket == null
                    || loginTicket.getExpired().before(new Date())
                    || loginTicket.getStatus() != 0) {
                return true;

            }

            //如果ticket存在且有效，那么就把user信息取出来
            User user = userDAO.selectById(loginTicket.getUserId());

            //先把用户放到threadlocal里，保证后面所有服务都能访问用户
            hostHolder.setUser(user);

        }
        return true;
    }

    //渲染之前调用
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        //把user信息关联到velocity，使得写html时也能使用user变量
        if (modelAndView != null) {
            modelAndView.addObject("user", hostHolder.getUser());
        }

    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        hostHolder.clear();
    }
}