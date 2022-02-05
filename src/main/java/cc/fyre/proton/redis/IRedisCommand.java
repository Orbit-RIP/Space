package cc.fyre.proton.redis;

import cc.fyre.proton.Proton;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class IRedisCommand {

    @Getter private JedisPool localJedisPool;
    @Getter private JedisPool backboneJedisPool;

    @Getter private long localRedisLastError;
    @Getter private long backboneRedisLastError;

    public IRedisCommand() {
        try {
            this.localJedisPool = new JedisPool(new JedisPoolConfig(), Proton.getInstance().getConfig().getString("Redis.Host"), 6379, 20000, (Proton.getInstance().getConfig().getString("Redis.Password").equals("") ? null : Proton.getInstance().getConfig().getString("Redis.Password")), Proton.getInstance().getConfig().getInt("Redis.DbId", 0));
        }
        catch (Exception e) {
            this.localJedisPool = null;
            e.printStackTrace();
            Proton.getInstance().getLogger().warning("Couldn't connect to a Redis instance at " + Proton.getInstance().getConfig().getString("Redis.Host") + ".");
        }
        try {
            this.backboneJedisPool = new JedisPool(new JedisPoolConfig(), Proton.getInstance().getConfig().getString("BackboneRedis.Host"), 6379, 20000, (Proton.getInstance().getConfig().getString("BackboneRedis.Password").equals("") ? null : Proton.getInstance().getConfig().getString("BackboneRedis.Password")), Proton.getInstance().getConfig().getInt("BackboneRedis.DbId", 0));
        }
        catch (Exception e) {
            this.backboneJedisPool = null;
            e.printStackTrace();
            Proton.getInstance().getLogger().warning("Couldn't connect to a Backbone Redis instance at " + Proton.getInstance().getConfig().getString("BackboneRedis.Host") + ".");
        }
    }

    public <T> T runRedisCommand(RedisCommand<T> redisCommand) {
        Jedis jedis = this.localJedisPool.getResource();
        T result = null;
        try {
            result = redisCommand.execute(jedis);
        }
        catch (Exception e) {
            e.printStackTrace();
            this.localRedisLastError = System.currentTimeMillis();
            if (jedis != null) {
                this.localJedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally {
            if (jedis != null) {
                this.localJedisPool.returnResource(jedis);
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T runBackboneRedisCommand(RedisCommand<T> redisCommand) {
        Jedis jedis = this.backboneJedisPool.getResource();
        T result = null;
        try {
            result = redisCommand.execute(jedis);
        }
        catch (Exception e) {
            e.printStackTrace();
            this.backboneRedisLastError = System.currentTimeMillis();
            if (jedis != null) {
                this.backboneJedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally {
            if (jedis != null) {
                this.backboneJedisPool.returnResource(jedis);
            }
        }
        return result;
    }

}
