package com.nowcoder.wenda.service;

import com.nowcoder.wenda.dao.FeedDAO;
import com.nowcoder.wenda.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LuoJiaQi
 * @Date 2019/10/18
 * @Time 15:03
 */

@Service
public class FeedService {

    @Autowired
    FeedDAO feedDAO;

    public List<Feed> getUserFeeds(int maxId, List<Integer> userIds, int count) {
        return feedDAO.selectUserFeeds(maxId, userIds, count);
    }

    public boolean addFeed(Feed feed) {
        feedDAO.addFeed(feed);
        return feed.getId() > 0;
    }

    public Feed getById(int id) {
        return feedDAO.getFeedById(id);
    }
}