package ek.zhou.cart.jedis;

import java.util.Map;

import org.quartz.utils.StringKeyDirtyFlagMap;

public interface JedisClient {

	String set(String key, String value);
	String get(String key);
	Long del(String key);
	Boolean exists(String key);
	Long expire(String key, int seconds);
	Long ttl(String key);
	Long incr(String key);
	Map<String, String > hgetAll(String key);
	Long hset(String key, String field, String value);
	String hget(String key, String field);	
	Long hdel(String key,String... field);//删除hkey
	
}
