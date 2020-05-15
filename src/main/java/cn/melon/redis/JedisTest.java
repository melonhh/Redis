package cn.melon.redis;

import redis.clients.jedis.Jedis;

public class JedisTest {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("106.14.92.95", 6379);
        jedis.auth("dwj123##");
        String re = jedis.ping();
        System.out.println(re);

        jedis.close();
    }
}
