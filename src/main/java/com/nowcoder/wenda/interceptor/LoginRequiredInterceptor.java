package com.nowcoder.wenda.interceptor;

import com.nowcoder.wenda.model.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author LuoJiaQi
 * @Date 2019/10/14
 * @Time 16:25
 * @Description 未登录跳转
 */

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //如果用户没有登陆，则跳转到登陆页面
        if (hostHolder.getUser() == null) {
            response.sendRedirect("/reglogin?next=" + request.getRequestURI());//request.getRequestURI()是当前访问页面URL，这样写可以使得登陆之后跳转回原页面

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}