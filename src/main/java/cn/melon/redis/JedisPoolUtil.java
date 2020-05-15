package cn.melon.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JedisPoolUtil {
    private static JedisPool pool = null;

    static {
        InputStream in = JedisPoolUtil.class.getClassLoader().getResourceAsStream("Redis.properties");
        Properties properties = new Properties();

        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(Integer.parseInt(properties.get("redis.maxTotal").toString()));
        poolConfig.setMaxIdle(Integer.parseInt(properties.get("redis.maxIdle").toString()));
        poolConfig.setMinIdle(Integer.parseInt(properties.get("redis.minIdle").toString()));

        pool = new JedisPool(poolConfig,properties.get("redis.url").toString(),
                Integer.parseInt(properties.get("redis.port").toString()),
                Integer.parseInt(properties.get("redis.timeout").toString()),
                properties.get("redis.auth").toString());
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void close() {
        pool.close();
    }
}
