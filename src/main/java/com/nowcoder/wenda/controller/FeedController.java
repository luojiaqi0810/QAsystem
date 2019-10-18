package com.nowcoder.wenda.controller;

import com.nowcoder.wenda.model.EntityType;
import com.nowcoder.wenda.model.Feed;
import com.nowcoder.wenda.model.HostHolder;
import com.nowcoder.wenda.service.FeedService;
import com.nowcoder.wenda.service.FollowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LuoJiaQi
 * @Date 2019/10/18
 * @Time 15:53
 */

@Controller
public class FeedController {
    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    @Autowired
    FeedService feedService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FollowService followService;

    @RequestMapping(path = {"/pullfeeds"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String getPullFeeds(Model model) {
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();

        List<Integer> followees = new ArrayList<>();
        if (localUserId != 0) {
            // 关注的人
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER, Integer.MAX_VALUE);
        }
        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees, 10);
        model.addAttribute("feeds", feeds);
        return "feeds";
    }
}