package cn.melon;

import cn.melon.redis.JedisPoolUtil;
import org.junit.After;
import org.junit.Before;
import redis.clients.jedis.Jedis;

import java.util.Iterator;
import java.util.Set;

public class Test {
    private Jedis jedis = null;

    @Before
    public void before() {
        jedis = JedisPoolUtil.getJedis();
    }

    @org.junit.Test
    public void test() {
//        jedis.multi();

        System.out.println(jedis.get("k1"));

        Set<String> keys = jedis.keys("*");

        for (String key : keys) {
            System.out.println(key);
        }

        System.out.println("删除k1：" + jedis.del("k1"));

        jedis.set("k2", "v2");
        jedis.expire("k2", 5);
        jedis.ttl("k2");
        jedis.persist("k2");
        System.out.println(jedis.type("k2"));
        jedis.flushDB();

    }

    @After
    public void close() {
        jedis.close();
        JedisPoolUtil.close();
    }

}
