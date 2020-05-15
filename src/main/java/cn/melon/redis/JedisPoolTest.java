package cn.melon.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolTest {
    public static void main(String[] args) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig() ;
        // 设置最大闲置数
        jedisPoolConfig.setMaxIdle(10);
        // 设置最小闲置数
        jedisPoolConfig.setMinIdle(5);
        // 设置最大连接数
        jedisPoolConfig.setMaxTotal(20);
        // 创建连接池对象
        JedisPool jedisPool = new JedisPool(jedisPoolConfig,"106.14.92.95", 6379, 3000, "dwj123##");

        // 获取连接资源
        Jedis jedis = jedisPool.getResource();

        jedis.set("k1", "hello world");

        String value = jedis.get("k1");

        System.out.println(value);

        jedis.close();
        jedisPool.close();
    }
}
