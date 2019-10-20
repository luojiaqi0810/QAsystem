package com.nowcoder.wenda.controller;

import com.nowcoder.wenda.async.EventProducer;
import com.nowcoder.wenda.model.EntityType;
import com.nowcoder.wenda.model.HostHolder;
import com.nowcoder.wenda.model.Question;
import com.nowcoder.wenda.model.ViewObject;
import com.nowcoder.wenda.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LuoJiaQi
 * @Date 2019/10/15
 * @Time 13:41
 */

@Controller
public class SearchController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    HostHolder hostHolder;
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    SearchService searchService;

    /**
     * 添加对问题的评论
     * @param model
     * @param keyword
     * @param offset
     * @param count
     * @return
     */
    @RequestMapping(path = {"/search"}, method = RequestMethod.GET)
    public String search(Model model, @RequestParam("q") String keyword,
                         @RequestParam(value = "offset", defaultValue = "0") int offset,
                         @RequestParam(value = "count", defaultValue = "10") int count) {

        try {
            // 获取查询结果列表，这里高亮前后缀加的是<em>，也可以加别的
            List<Question> questionList = searchService.searchQuestion(keyword, offset, count, "<p style=\"color:red\">", "</p>");

            // 跟首页/index的不同在于首页的question是从数据库读取的，这里是从solr读取的
            // solr里获取的question只设置了id，questionId，questionContent
            List<ViewObject> vos = new ArrayList<>();
            for (Question question : questionList) {
                //根据id获得该question，然后把solr获取到的加了高亮的title和content设置进去
                Question q = questionService.getById(question.getId());
                if (question.getTitle() != null) {
                    q.setTitle(question.getTitle());
                }
                if (question.getContent() != null) {
                    q.setContent(question.getContent());
                }

                ViewObject vo = new ViewObject();
                vo.set("question", q);
                vo.set("followcount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, q.getId()));
                vo.set("user", userService.getUser(q.getUserId()));
                vos.add(vo);
            }
            model.addAttribute("vos", vos);
        } catch (Exception e) {
            LOGGER.error("搜索评论失败" + e.getMessage());

        }
        return "result";
    }
}