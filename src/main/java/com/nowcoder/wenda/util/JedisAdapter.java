package com.nowcoder.wenda.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.wenda.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.util.List;
import java.util.Set;

/**
 * @author LuoJiaQi
 * @Date 2019/10/15
 * @Time 19:19
 */
@Service
public class JedisAdapter implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool;

    public static void print(int index, Object object) {
        System.out.println(String.format("%d, %s", index, object.toString()));
    }

    //打包时去掉多余main函数，故改名
    public static void miain(String[] args) {
        Jedis jedis = new Jedis("redis://localhost:6379/9");
        jedis.flushDB();

        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "newhello");
        print(1, "newhello");

        //可以设置超时失效，15秒后hello2就没了
        jedis.setex("hello2", 15, "world");

        jedis.set("pv", "100");
        jedis.incr("pv");
        print(2, jedis.get("pv"));
        jedis.incrBy("pv", 5);
        print(2, jedis.get("pv"));
        jedis.decrBy("pv", 3);
        print(2, jedis.get("pv"));

        print(3, jedis.keys("*"));

        String listName = "list";
        jedis.del(listName);
        for (int i = 0; i < 10; i++) {
            jedis.lpush(listName, "a" + String.valueOf(i));
        }
        print(4, jedis.lrange(listName, 0, 12));
        print(4, jedis.lrange(listName, 0, 3));
        print(4, jedis.lrange(listName, 0, 3));
        print(5, jedis.llen(listName));
        print(6, jedis.lpop(listName));
        print(7, jedis.llen(listName));
        print(8, jedis.lrange(listName, 2, 6));
        print(9, jedis.lindex(listName, 3));
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "xx"));
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "bb"));
        print(11, jedis.lrange(listName, 0, 12));

        //hash
        String userKey = "userxx";
        jedis.hset(userKey, "name", "jim");
        jedis.hset(userKey, "age", "12");
        jedis.hset(userKey, "phone", "13012345678");
        print(12, jedis.hget(userKey, "name"));
        print(13, jedis.hgetAll(userKey));
        jedis.hdel(userKey, "phone");
        print(14, jedis.hgetAll(userKey));
        print(15, jedis.hexists(userKey, "email"));
        print(16, jedis.hexists(userKey, "age"));
        print(17, jedis.keys(userKey));
        print(18, jedis.hvals(userKey));
        jedis.hsetnx(userKey, "school", "hit");
        jedis.hsetnx(userKey, "name", "ljq");
        print(19, jedis.hgetAll(userKey));

        //set，可以用来存好友，方便求共同好友
        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";
        for (int i = 0; i < 10; i++) {
            jedis.sadd(likeKey1, String.valueOf(i));
            jedis.sadd(likeKey2, String.valueOf(i * i));
        }
        print(20, jedis.smembers(likeKey1));
        print(21, jedis.smembers(likeKey2));
        print(22, jedis.sunion(likeKey1, likeKey2));
        print(23, jedis.sdiff(likeKey1, likeKey2));
        print(24, jedis.sinter(likeKey1, likeKey2));
        print(25, jedis.sismember(likeKey1, "12"));
        print(26, jedis.sismember(likeKey2, "16"));
        jedis.srem(likeKey1, "5");
        print(27, jedis.smembers(likeKey1));
        jedis.smove(likeKey2, likeKey1, "25");
        print(28, jedis.smembers(likeKey1));
        print(29, jedis.smembers(likeKey2));
        print(30, jedis.scard(likeKey1));
        //随机取值，可以做抽奖
        print(31, jedis.srandmember(likeKey1, 3));

        //sorted set
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "Jim");
        jedis.zadd(rankKey, 60, "Ben");
        jedis.zadd(rankKey, 90, "Lee");
        jedis.zadd(rankKey, 75, "Lucy");
        jedis.zadd(rankKey, 80, "Mei");
        print(32, jedis.zcard(rankKey));
        print(33, jedis.zcount(rankKey, 61, 100));
        print(34, jedis.zscore(rankKey, "Jim"));
        jedis.zincrby(rankKey, 2, "Lucy");
        print(35, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 2, "Luc");
        print(36, jedis.zscore(rankKey, "Luc"));
        print(37, jedis.zrange(rankKey, 0, 100));
        print(38, jedis.zrevrange(rankKey, 1, 3));

        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, "60", "100")) {
            print(39, tuple.getElement() + ":" + tuple.getScore());
        }

        print(40, jedis.zrank(rankKey, "Ben"));
        print(41, jedis.zrevrank(rankKey, "Ben"));

        String setKey = "zset";
        jedis.zadd(setKey, 1, "a");
        jedis.zadd(setKey, 1, "b");
        jedis.zadd(setKey, 1, "c");
        jedis.zadd(setKey, 1, "d");
        jedis.zadd(setKey, 1, "e");

        print(42, jedis.zlexcount(setKey, "-", "+"));
        print(43, jedis.zlexcount(setKey, "[b", "[d"));
        print(44, jedis.zlexcount(setKey, "(b", "[d"));
        print(45, jedis.zlexcount(setKey, "[d", "[b"));

        print(46, jedis.get("pv"));


        JedisPool pool = new JedisPool("redis://localhost:6379/9");

        /*
        for (int i = 0; i < 100; i++) {
            Jedis j = pool.getResource();
            print(46, j.get("pv"));
            j.close();
        }
        */


        User user = new User();
        user.setName("xx");
        user.setPassword("ppp");
        user.setHeadUrl("a.jpg");
        user.setSalt("salt");
        user.setId(1);
        print(47, JSONObject.toJSONString(user));
        jedis.set("user1", JSONObject.toJSONString(user));

        String value = jedis.get("user1");
        User user2 = JSON.parseObject(value, User.class);
        print(48, user2);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://localhost:6379/10");
    }

    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            LOGGER.error("sadd方法发生异常：" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            LOGGER.error("scard方法发生异常：" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long srem(String key,String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            LOGGER.error("srem方法发生异常：" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public boolean sismember(String key,String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            LOGGER.error("sismember方法发生异常：" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            LOGGER.error("lpush方法发生异常：" + e.getMessage());
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            LOGGER.error("brpop方法发生异常：" + e.getMessage());
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }


    public Jedis getJedis() {
        return pool.getResource();
    }

    public Transaction multi(Jedis jedis) {
        try {
            return jedis.multi();
        } catch (Exception e) {
            LOGGER.error("multi方法发生异常"+e.getMessage());
        }
        return null;
    }

    public List<Object> exec(Transaction tx, Jedis jedis) {
        try {
            return tx.exec();
        } catch (Exception e) {
            LOGGER.error("exec方法发生异常"+e.getMessage());
        }finally {
            if (tx != null) {
                try {
                    tx.close();
                } catch (Exception ioe) {
                    LOGGER.error("事务关闭发生异常"+ ioe.getMessage());
                }
            }
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long zadd(String key, double score, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key, score, value);
        } catch (Exception e) {
            LOGGER.error("zadd方法发生异常：" + e.getMessage());
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long zrem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrem(key, value);
        } catch (Exception e) {
            LOGGER.error("zrem方法发生异常：" + e.getMessage());
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Set<String> zrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            LOGGER.error("zrange方法发生异常：" + e.getMessage());
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Set<String> zrevrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            LOGGER.error("zrevrange方法发生异常：" + e.getMessage());
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }


    public long zcard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            LOGGER.error("zcard方法发生异常：" + e.getMessage());
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Double zscore(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key,member);
        } catch (Exception e) {
            LOGGER.error("zscore方法发生异常：" + e.getMessage());
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public List<String> lrange(String key, long start,long end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            LOGGER.error("lrange方法发生异常：" + e.getMessage());
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }


}