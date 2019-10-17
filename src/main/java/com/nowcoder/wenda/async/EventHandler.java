package com.nowcoder.wenda.async;

import java.util.List;

/**
 * @author LuoJiaQi
 * @Date 2019/10/16
 * @Time 12:31
 * @Descripton 用来处理Event
 */

public interface EventHandler {

    //处理event
    void doHandle(EventModel eventModel);

    //关注哪些eventType，相当于注册一下，让别人知道你关注哪些
    List<EventType> getSupportEventTypes();

}
