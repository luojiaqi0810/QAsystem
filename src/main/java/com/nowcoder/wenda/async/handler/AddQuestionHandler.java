package com.nowcoder.wenda.async.handler;

import com.nowcoder.wenda.async.EventHandler;
import com.nowcoder.wenda.async.EventModel;
import com.nowcoder.wenda.async.EventType;
import com.nowcoder.wenda.controller.QuestionController;
import com.nowcoder.wenda.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author LuoJiaQi
 * @Date 2019/10/20
 * @Time 15:31
 */

@Component
public class AddQuestionHandler implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    SearchService searchService;

    @Override
    public void doHandle(EventModel eventModel) {
        try {
            searchService.indexQuestion(eventModel.getEntityId(), eventModel.getExt("title"), eventModel.getExt("content"));
        } catch (Exception e) {
            LOGGER.error("增加题目索引失败 "+e.getMessage());
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.ADD_QUESTION);
    }
}