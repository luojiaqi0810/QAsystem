package com.nowcoder.wenda.controller;

import com.nowcoder.wenda.async.EventModel;
import com.nowcoder.wenda.async.EventProducer;
import com.nowcoder.wenda.async.EventType;
import com.nowcoder.wenda.model.Comment;
import com.nowcoder.wenda.model.EntityType;
import com.nowcoder.wenda.model.HostHolder;
import com.nowcoder.wenda.service.CommentService;
import com.nowcoder.wenda.service.LikeService;
import com.nowcoder.wenda.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author LuoJiaQi
 * @Date 2019/10/15
 * @Time 22:05
 */

@Controller
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = RequestMethod.POST)
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId) {

        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        int likeStatus = likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        long likeCount;
        if (likeStatus == 0 || likeStatus == -1) { //没有点赞的情况：1.都没点，2.点了踩
            Comment comment = commentService.getCommentById(commentId);
            eventProducer.fireEvent(new EventModel(EventType.LIKE)
                    .setActorId(hostHolder.getUser().getId())
                    .setEntityId(commentId)
                    .setEntityType(EntityType.ENTITY_COMMENT)
                    .setEntityOwnerId(comment.getUserId())
                    .setExt("questionId", String.valueOf(comment.getEntityId())));
            likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        } else  {//已经点了赞，再点就取消
            likeCount = likeService.undoLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        }
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }


    @RequestMapping(path = {"/dislike"}, method = RequestMethod.POST)
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId) {

        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        int likeStatus = likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        long likeCount;
        if (likeStatus == 0 || likeStatus == 1) {
            likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        } else {
            likeCount = likeService.undoDisLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        }

        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }

}