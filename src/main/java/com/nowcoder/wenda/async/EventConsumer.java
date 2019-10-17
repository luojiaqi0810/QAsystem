package com.nowcoder.wenda.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.wenda.util.JedisAdapter;
import com.nowcoder.wenda.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LuoJiaQi
 * @Date 2019/10/16
 * @Time 12:37
 */

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    JedisAdapter jedisAdapter;

    //根据EventType，找到与之相关的Handler
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        //启动时把所有继承了EventHandler接口的Handler都找出来
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                //找到某一个Handler关心的EventType
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();

                for (EventType type : eventTypes) {
                    if (!config.containsKey(type)) {
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //不断地去队列中取元素
                while (true) {
                    String key = RedisKeyUtil.getEventQueueKey();
                    //Brpop 命令移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止,timeout设为0表示只要没有元素就会一直阻塞
                    List<String> events = jedisAdapter.brpop(0, key);
                    for (String event : events) {
                        //brpop返回值里第一个就是本身的key，所以要过滤掉
                        if (event.equals(key)) {
                            continue;
                        }

                        //在Producer中event被序列化，到了Consumer中就要反序列化
                        EventModel eventModel = JSON.parseObject(event, EventModel.class);
                        if (!config.containsKey(eventModel.getType())) {
                            LOGGER.error("不能识别的事件类型");
                            continue;
                        }

                        for (EventHandler handler : config.get(eventModel.getType())) {
                            handler.doHandle(eventModel);

                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}