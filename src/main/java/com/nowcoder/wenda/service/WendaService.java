package com.nowcoder.wenda.service;

import org.springframework.stereotype.Service;

/**
 * @author LuoJiaQi
 * @Date 2019/10/13
 * @Time 13:36
 */


@Service
public class WendaService {
    public String getMessage(int userId) {
        return "Hello Message:" + String.valueOf(userId);
    }
}