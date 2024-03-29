package com.nowcoder.wenda.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.wenda.async.EventHandler;
import com.nowcoder.wenda.async.EventModel;
import com.nowcoder.wenda.async.EventType;
import com.nowcoder.wenda.model.EntityType;
import com.nowcoder.wenda.model.Feed;
import com.nowcoder.wenda.model.Question;
import com.nowcoder.wenda.model.User;
import com.nowcoder.wenda.service.FeedService;
import com.nowcoder.wenda.service.FollowService;
import com.nowcoder.wenda.service.QuestionService;
import com.nowcoder.wenda.service.UserService;
import com.nowcoder.wenda.util.JedisAdapter;
import com.nowcoder.wenda.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author LuoJiaQi
 * @Date 2019/10/16
 * @Time 13:59
 * @Description 点赞处理地Handler
 */

@Component
public class FeedHandler implements EventHandler {


    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FeedService feedService;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdapter jedisAdapter;

    private String buildFeedData(EventModel model) {
        Map<String, String> map = new HashMap<>();
        // 触发用户是通用的
        User actor = userService.getUser(model.getActorId());
        if (actor == null) {
            return null;
        }
        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());

        //调式查看用
        EventType e = model.getType();

        //评论了一个问题或关注了一个问题
        if (model.getType() == EventType.COMMENT ||
                (model.getType() == EventType.FOLLOW && model.getEntityType() == EntityType.ENTITY_QUESTION)) {
            Question question = questionService.getById(model.getEntityId());
            if (question == null) {
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }

    @Override
    public void doHandle(EventModel model) {
        //方便测试，把model的userId随机一下
        Random r = new Random();
        model.setActorId(1 + r.nextInt(10));

        // 构造一个新鲜事
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setUserId(model.getActorId());
        feed.setType(model.getType().getValue());//注意这里是getType也就是事件类型，而不是getEntityType
        feed.setData(buildFeedData(model));
        if (feed.getData() == null) {// 不支持的feed
            return;
        }
        feedService.addFeed(feed);

        //存储在redis
        //给事件的粉丝推，这里取得数量写的是Integer.MAX_VALUE，因为现在数据量不大，所以都取出来了
        //如果数据量大，可以一段一段取，用offset分开
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, model.getActorId(), Integer.MAX_VALUE);
        followers.add(0);
        for (int follwer : followers) {
            String timelineKey = RedisKeyUtil.getTimelineKey(follwer);
            jedisAdapter.lpush(timelineKey, String.valueOf(feed.getId()));
        }
    }


    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.FOLLOW, EventType.COMMENT});
    }
}