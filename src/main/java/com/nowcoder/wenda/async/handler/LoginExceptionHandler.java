package com.nowcoder.wenda.async.handler;

import com.nowcoder.wenda.async.EventHandler;
import com.nowcoder.wenda.async.EventModel;
import com.nowcoder.wenda.async.EventType;
import com.nowcoder.wenda.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**用户异常登录处理，比如IP地址异常等
 * @author LuoJiaQi
 * @Date 2019/10/16
 * @Time 18:25
 */

@Component
public class LoginExceptionHandler implements EventHandler {

    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {
        //xxxxxx判断发现用户登陆异常，那就给他发一封邮件
        Map<String, Object> map = new HashMap<>();
        //
        map.put("username", model.getExt("username"));
        mailSender.sendWithHTMLTemplate(model.getExt("email")
                , "登陆IP异常"
                , "mails/login_exception.html"
                , map);
        int i = 0;
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}