package com.nowcoder.wenda.controller;

import com.nowcoder.wenda.async.EventModel;
import com.nowcoder.wenda.async.EventProducer;
import com.nowcoder.wenda.async.EventType;
import com.nowcoder.wenda.model.*;
import com.nowcoder.wenda.service.CommentService;
import com.nowcoder.wenda.service.FollowService;
import com.nowcoder.wenda.service.QuestionService;
import com.nowcoder.wenda.service.UserService;
import com.nowcoder.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LuoJiaQi
 * @Date 2019/10/15
 * @Time 13:41
 * @Description 关注模块
 */

@Controller
public class FollowController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FollowController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    //关注用户
    @RequestMapping(path = {"/followUser"}, method = RequestMethod.POST)
    @ResponseBody
    public String follow(@RequestParam("userId") int userId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }


        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER)
                .setEntityOwnerId(userId));
        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }

    //取消关注用户
    @RequestMapping(path = {"/unfollowUser"}, method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(@RequestParam("userId") int userId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER)
                .setEntityOwnerId(userId));
        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }


    //关注问题
    @RequestMapping(path = {"/followQuestion"}, method = RequestMethod.POST)
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }
        Question question = questionService.selectById(questionId);
        if (question == null) {
            return WendaUtil.getJSONString(1, "问题不存在");
        }
        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, questionId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityOwnerId(question.getUserId()));

        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));


        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    //取消关注问题
    @RequestMapping(path = {"/unfollowQuestion"}, method = RequestMethod.POST)
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }
        Question question = questionService.selectById(questionId);
        if (question == null) {
            return WendaUtil.getJSONString(1, "问题不存在");
        }
        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, questionId);
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityOwnerId(question.getUserId()));


        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));


        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    @RequestMapping(path = {"/user/{uid}/followees"}, method = RequestMethod.POST)
    public String followees(Model model, @PathVariable("uId") int userId) {
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER, 10);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followees", getUsersinfo(hostHolder.getUser().getId(), followeeIds));
        } else {
            model.addAttribute("followees", getUsersinfo(0, followeeIds));
        }
        return "followees";

    }

    @RequestMapping(path = {"/user/{uid}/followers"}, method = RequestMethod.POST)
    public String followers(Model model, @PathVariable("uId") int userId) {
        List<Integer> followerIds = followService.getFollowers(userId, EntityType.ENTITY_USER, 10);
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER, 10);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followers", getUsersinfo(hostHolder.getUser().getId(), followerIds));
        } else {
            model.addAttribute("followers", getUsersinfo(0, followerIds));
        }
        return "followers";
    }

    private List<ViewObject> getUsersinfo(int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos = new ArrayList<>();
        for (Integer uid : userIds) {
            User user = userService.getUser(uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
            vo.set("followeeCount", followService.getFolloweeCount(EntityType.ENTITY_USER, uid));
            if (localUserId != 0) {
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            } else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }
}