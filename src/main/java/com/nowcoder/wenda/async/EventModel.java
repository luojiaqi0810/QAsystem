package com.nowcoder.wenda.async;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LuoJiaQi
 * @Date 2019/10/16
 * @Time 11:51
 */
public class EventModel {

    //比如评论，那么type就是1也就是COMMENT，actorId就是评论的用户id，entityType和entityId唯一指定是哪一条问题
    private EventType type;//事件类型
    private int actorId;//触发者
    //触发载体
    private int entityType;
    private int entityId;
    private int entityOwnerId;//跟这个entity相关的用户id

    private Map<String, String> exts = new HashMap<>();

    public Map<String, String> getExts() {
        return exts;
    }

    public void setExts(Map<String, String> exts) {
        this.exts = exts;
    }

    public String getExt(String key) {
        return exts.get(key);
    }

    public EventModel setExt(String key, String value) {
        exts.put(key, value);
        return this;
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public EventModel(){}

    public EventModel(EventType type) {
        this.type = type;
    }
}