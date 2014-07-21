package qa.qcri.aidr.collector.redis;

import qa.qcri.aidr.collector.utils.Config;

import qa.qcri.aidr.collector.logging.Loggable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 *
 * @author Imran
 */
public class JedisConnectionPool extends Loggable {

    static JedisPool jedisPool;

    public static Jedis getJedisConnection() throws Exception {
        try {
            if (jedisPool == null) {
                JedisPoolConfig poolConfig = new JedisPoolConfig();
                poolConfig.setMaxIdle(10);
                poolConfig.setMinIdle(1);
                poolConfig.setNumTestsPerEvictionRun(10);
                poolConfig.setTestOnBorrow(true);
                poolConfig.setTestOnReturn(true);
                poolConfig.setTestWhileIdle(true);
                poolConfig.setTimeBetweenEvictionRunsMillis(30000);

//                config.setTestWhileIdle(true);
//                config.setMinEvictableIdleTimeMillis(60000);
//                config.setTimeBetweenEvictionRunsMillis(30000);
//                config.setNumTestsPerEvictionRun(-1);
//                config.setMaxIdle(50000);
                jedisPool = new JedisPool(poolConfig, Config.REDIS_HOST);

            }
            return jedisPool.getResource();
        } catch (Exception e) {
            System.out.println("Could not establish Redis connection. Is the Redis running?");
            throw e;
        }
    }

    public static void close(Jedis resource) {
        jedisPool.returnResource(resource);
    }
}
