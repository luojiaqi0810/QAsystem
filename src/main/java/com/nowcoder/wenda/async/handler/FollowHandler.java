package com.nowcoder.wenda.async.handler;

import com.nowcoder.wenda.async.EventHandler;
import com.nowcoder.wenda.async.EventModel;
import com.nowcoder.wenda.async.EventType;
import com.nowcoder.wenda.model.EntityType;
import com.nowcoder.wenda.model.Message;
import com.nowcoder.wenda.model.User;
import com.nowcoder.wenda.service.MessageService;
import com.nowcoder.wenda.service.UserService;
import com.nowcoder.wenda.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author LuoJiaQi
 * @Date 2019/10/16
 * @Time 13:59
 * @Description 点赞处理地Handler
 */

@Component
public class FollowHandler implements EventHandler {

    //当收到关注的时候，就给对方发一个站内信，所以需要用到MessageService
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        //假如我给用户A的关注了某个问题，那么这条站内信的发送方是系统管理员，接收方是提问者
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(model.getActorId());
        if (model.getEntityType() == EntityType.ENTITY_QUESTION) {
            message.setContent("用户" + user.getName() + "关注了你的问题。" +
                    "http://127.0.0.1:8080/question/" + model.getEntityId());
        } else if (model.getEntityType() == EntityType.ENTITY_USER) {
            message.setContent("用户" + user.getName() + "关注了你。" +
                    "http://127.0.0.1:8080/user/" + model.getActorId());
        }
        messageService.addMessage(message);

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}