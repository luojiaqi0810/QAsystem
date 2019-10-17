package com.nowcoder.wenda.async;

/**
 * @author LuoJiaQi
 * @Date 2019/10/16
 * @Time 11:48
 */
public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3),
    FOLLOW(4),
    UNFOLLOW(5);

    private int value;
    EventType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
