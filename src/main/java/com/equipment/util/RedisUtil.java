package com.equipment.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author: JavaTansanlin
 * @Description: redis操作的工具类
 * @Date: Created in 11:54 2019/10/22
 * @Modified By:
 */
@Slf4j
public class RedisUtil {

    /**
     *
     * @MethodName：cacheValue
     * @param k
     * @param v
     * @param time(单位秒)  <=0 不过期
     * @return
     * @ReturnType：boolean
     * @Description：缓存value操作
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:24:56
     * @Modifier：
     * @ModifyTime：
     */
    public boolean cacheValue(RedisTemplate redisTemplate ,String k, String v, long time) {
        try {
            ValueOperations<String, String> valueOps =  redisTemplate.opsForValue();
            valueOps.set(k, v);
            if (time > 0) redisTemplate.expire(k, time, TimeUnit.SECONDS);
            return true;
        } catch (Throwable t) {
            log.error("缓存[" + k + "]失败, value[" + v + "]", t);
        }
        return false;
    }

    /**
     *
     * @MethodName：cacheValue
     * @param k
     * @param v
     * @return
     * @ReturnType：boolean
     * @Description：缓存value操作
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:24:43
     * @Modifier：
     * @ModifyTime：
     */
    public boolean cacheValue(RedisTemplate redisTemplate ,String k, String v) {
        return cacheValue(redisTemplate ,k, v, -1);
    }

    /**
     *
     * @MethodName：getValue
     * @param k
     * @return
     * @ReturnType：String
     * @Description：获取缓存
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:21:37
     * @Modifier：
     * @ModifyTime：
     */
    public String getValue(RedisTemplate redisTemplate ,String k) {
        try {
            ValueOperations<String, String> valueOps =  redisTemplate.opsForValue();
            return valueOps.get(k);
        } catch (Throwable t) {
            log.error("获取缓存失败key[" + k + ", error[" + t + "]");
        }
        return null;
    }

    /**
     *
     * @MethodName：cacheSet
     * @param k
     * @param v
     * @param time
     * @return
     * @ReturnType：boolean
     * @Description：缓存set操作
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:20:00
     * @Modifier：
     * @ModifyTime：
     */
    public boolean cacheSet(RedisTemplate redisTemplate ,String k, String v, long time) {
        try {
            SetOperations<String, String> valueOps =  redisTemplate.opsForSet();
            valueOps.add(k, v);
            if (time > 0) redisTemplate.expire(k, time, TimeUnit.SECONDS);
        } catch (Throwable t) {
            log.error("缓存[" + k + "]失败, value[" + v + "]", t);
        }
        return true;
    }

    /**
     *
     * @MethodName：cacheSet
     * @param k
     * @param v
     * @return
     * @ReturnType：boolean
     * @Description：缓存set
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:19:00
     * @Modifier：
     * @ModifyTime：
     */
    public boolean cacheSet(RedisTemplate redisTemplate ,String k, String v) {
        return cacheSet(redisTemplate ,k, v, -1);
    }

    /**
     *
     * @MethodName：cacheSet
     * @param k
     * @param v
     * @param time
     * @return
     * @ReturnType：boolean
     * @Description：缓存set
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:18:48
     * @Modifier：
     * @ModifyTime：
     */
    public boolean cacheSet(RedisTemplate redisTemplate , String k, Set<String> v, long time) {
        try {
            SetOperations<String, String> setOps =  redisTemplate.opsForSet();
            setOps.add(k, v.toArray(new String[v.size()]));
            if (time > 0) redisTemplate.expire(k, time, TimeUnit.SECONDS);
            return true;
        } catch (Throwable t) {
            log.error("缓存[" + k + "]失败, value[" + v + "]", t);
        }
        return false;
    }

    /**
     *
     * @MethodName：cacheSet
     * @param k
     * @param v
     * @return
     * @ReturnType：boolean
     * @Description：缓存set
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:18:34
     * @Modifier：
     * @ModifyTime：
     */
    public boolean cacheSet(RedisTemplate redisTemplate ,String k, Set<String> v) {
        return cacheSet(redisTemplate , k, v, -1);
    }

    /**
     *
     * @MethodName：getSet
     * @param k
     * @return
     * @ReturnType：Set<String>
     * @Description：获取缓存set数据
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:17:45
     * @Modifier：
     * @ModifyTime：
     */
    public Set<String> getSet(RedisTemplate redisTemplate ,String k) {
        try {
            SetOperations<String, String> setOps = redisTemplate.opsForSet();
            return setOps.members(k);
        } catch (Throwable t) {
            log.error("获取set缓存失败key[" + k + ", error[" + t + "]");
        }
        return null;
    }

    /**
     *
     * @MethodName：cacheList
     * @param k
     * @param v
     * @param time
     * @return
     * @ReturnType：boolean
     * @Description：list缓存
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:17:23
     * @Modifier：
     * @ModifyTime：
     */
    public boolean cacheList(RedisTemplate redisTemplate ,String k, String v, long time) {
        try {
            ListOperations<String, String> listOps =  redisTemplate.opsForList();
            listOps.rightPush(k, v);
            if (time > 0) redisTemplate.expire(k, time, TimeUnit.SECONDS);
            return true;
        } catch (Throwable t) {
            log.error("缓存[" + k + "]失败, value[" + v + "]", t);
        }
        return false;
    }

    /**
     *
     * @MethodName：cacheList
     * @param k
     * @param v
     * @return
     * @ReturnType：boolean
     * @Description：缓存list
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:17:10
     * @Modifier：
     * @ModifyTime：
     */
    public boolean cacheList(RedisTemplate redisTemplate ,String k, String v) {
        return cacheList(redisTemplate,k, v, -1);
    }

    /**
     *
     * @MethodName：cacheList
     * @param k
     * @param v
     * @param time
     * @return
     * @ReturnType：boolean
     * @Description：缓存list
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:15:47
     * @Modifier：
     * @ModifyTime：
     */
    public boolean cacheList(RedisTemplate redisTemplate , String k, List<String> v, long time) {
        try {
            ListOperations<String, String> listOps =  redisTemplate.opsForList();
            listOps.rightPushAll(k, v);
            if (time > 0) redisTemplate.expire(k, time, TimeUnit.SECONDS);
            return true;
        } catch (Throwable t) {
            log.error("缓存[" + k + "]失败, value[" + v + "]", t);
        }
        return false;
    }

    /**
     *
     * @MethodName：cacheList
     * @param k
     * @param v
     * @return
     * @ReturnType：boolean
     * @Description：缓存list
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:15:05
     * @Modifier：
     * @ModifyTime：
     */
    public boolean cacheList(RedisTemplate redisTemplate ,String k, List<String> v) {
        return cacheList(redisTemplate ,k, v, -1);
    }

    /**
     *
     * @MethodName：getList
     * @param k
     * @param start
     * @param end
     * @return
     * @ReturnType：List<String>
     * @Description：获取list缓存
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:14:45
     * @Modifier：
     * @ModifyTime：
     */
    public List<String> getList(RedisTemplate redisTemplate ,String k, long start, long end) {
        try {
            ListOperations<String, String> listOps =  redisTemplate.opsForList();
            return listOps.range(k, start, end);
        } catch (Throwable t) {
            log.error("获取list缓存失败key[" + k + "]" + ", error[" + t + "]");
        }
        return null;
    }

    /**
     *
     * @MethodName：getListSize
     * @param k
     * @return
     * @ReturnType：long
     * @Description：获取总条数
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:13:39
     * @Modifier：
     * @ModifyTime：
     */
    public long getListSize(RedisTemplate redisTemplate ,String k) {
        try {
            ListOperations<String, String> listOps =  redisTemplate.opsForList();
            return listOps.size(k);
        } catch (Throwable t) {
            log.error("获取list长度失败key[" + k + "], error[" + t + "]");
        }
        return 0;
    }

    /**
     *
     * @MethodName：removeOneOfList
     * @param k
     * @return
     * @ReturnType：boolean
     * @Description：移除list缓存
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:11:16
     * @Modifier：
     * @ModifyTime：
     */
    public boolean removeOneOfList(RedisTemplate redisTemplate ,String k) {
        try {
            ListOperations<String, String> listOps =  redisTemplate.opsForList();
            listOps.rightPop(k);
            return true;
        } catch (Throwable t) {
            log.error("移除list缓存失败key[" + k + ", error[" + t + "]");
        }
        return false;
    }

    /**
     *
     * @MethodName：cacheGeo
     * @param x
     * @param y
     * @param member
     * @param time(单位秒)  <=0 不过期
     * @return
     * @ReturnType：boolean
     * @Description：缓存地理位置信息
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:30:23
     * @Modifier：
     * @ModifyTime：
     */
    public boolean cacheGeo(RedisTemplate redisTemplate ,String k, double x, double y, String member, long time) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            geoOps.add(k, new Point(x, y) , member);
            if (time > 0) redisTemplate.expire(k, time, TimeUnit.SECONDS);
        } catch (Throwable t) {
            log.error("缓存[" + k +"]" + "失败, point["+ x + "," +
                    y + "], member[" + member + "]" +", error[" + t + "]");
        }
        return true;
    }

    /**
     *
     * @MethodName：cacheGeo
     * @param locations
     * @param time(单位秒)  <=0 不过期
     * @return
     * @ReturnType：boolean
     * @Description：
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:31:33
     * @Modifier：
     * @ModifyTime：
     */
    public boolean cacheGeo(RedisTemplate redisTemplate , String k, Iterable<RedisGeoCommands.GeoLocation<String>> locations, long time) {
        try {
            for (RedisGeoCommands.GeoLocation<String> location : locations) {
                cacheGeo(redisTemplate,k, location.getPoint().getX(), location.getPoint().getY(), location.getName(), time);
            }
        } catch (Throwable t) {
            log.error("缓存[" + k + "]" + "失败" +", error[" + t + "]");
        }
        return true;
    }

    /**
     *
     * @MethodName：removeGeo
     * @param k
     * @param members
     * @return
     * @ReturnType：boolean
     * @Description：移除地理位置信息
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午10:53:01
     * @Modifier：
     * @ModifyTime：
     */
    public boolean removeGeo(RedisTemplate redisTemplate ,String k, String...members) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            geoOps.remove(k, members);
        } catch (Throwable t) {
            log.error("移除[" + k +"]" + "失败" +", error[" + t + "]");
        }
        return true;
    }

    /**
     *
     * @MethodName：distanceGeo
     * @param k
     * @param member1
     * @param member2
     * @return Distance
     * @ReturnType：Distance
     * @Description：根据两个成员计算两个成员之间距离
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午10:58:33
     * @Modifier：
     * @ModifyTime：
     */
    public Distance distanceGeo(RedisTemplate redisTemplate , String k, String member1, String member2) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            return geoOps.distance(k, member1, member2);
        } catch (Throwable t) {
            log.error("计算距离[" + k +"]" + "失败, member[" + member1 + "," + member2 +"], error[" + t + "]");
        }
        return null;
    }

    /**
     *
     * @MethodName：getGeo
     * @param k
     * @param members
     * @return
     * @ReturnType：List<Point>
     * @Description：根据key和member获取这些member的坐标信息
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:02:01
     * @Modifier：
     * @ModifyTime：
     */
    public List<Point> getGeo(RedisTemplate redisTemplate ,String k, String...members) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            return geoOps.position(k, members);
        } catch (Throwable t) {
            log.error("获取坐标[" + k +"]" + "失败]" + ", error[" + t + "]");
        }
        return null;
    }

    /**
     *
     * @MethodName：radiusGeo
     * @param key
     * @param x
     * @param y
     * @param distance km
     * @return
     * @ReturnType：GeoResults<GeoLocation<String>>
     * @Description：通过给定的坐标和距离(km)获取范围类其它的坐标信息
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:09:10
     * @Modifier：
     * @ModifyTime：
     */
    public GeoResults<RedisGeoCommands.GeoLocation<String>> radiusGeo(RedisTemplate redisTemplate , String k, double x, double y, double distance, Sort.Direction direction, long limit) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();

            //设置geo查询参数
            RedisGeoCommands.GeoRadiusCommandArgs geoRadiusArgs = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs();
            geoRadiusArgs = geoRadiusArgs.includeCoordinates().includeDistance();//查询返回结果包括距离和坐标
            if (Sort.Direction.ASC.equals(direction)) {//按查询出的坐标距离中心坐标的距离进行排序
                geoRadiusArgs.sortAscending();
            } else if (Sort.Direction.DESC.equals(direction)) {
                geoRadiusArgs.sortDescending();
            }
            geoRadiusArgs.limit(limit);//限制查询数量
            GeoResults<RedisGeoCommands.GeoLocation<String>> radiusGeo = geoOps.radius(k, new Circle(new Point(x, y), new Distance(distance, RedisGeoCommands.DistanceUnit.METERS)), geoRadiusArgs);

            return radiusGeo;
        } catch (Throwable t) {
            log.error("通过坐标[" + x + "," + y +"]获取范围[" + distance + "km的其它坐标失败]" + ", error[" + t + "]");
        }
        return null;
    }

    /**
     *
     * @MethodName：containsListKey
     * @param k
     * @return
     * @ReturnType：boolean
     * @Description：判断缓存是否存在
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:23:37
     * @Modifier：
     * @ModifyTime：
     */
    public boolean containsValueKey(RedisTemplate redisTemplate ,String k) {
        return containsKey(redisTemplate,k);
    }

    /**
     *
     * @MethodName：containsListKey
     * @param k
     * @return
     * @ReturnType：boolean
     * @Description：判断缓存是否存在
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:23:37
     * @Modifier：
     * @ModifyTime：
     */
    public boolean containsSetKey(RedisTemplate redisTemplate ,String k) {
        return containsKey(redisTemplate, k);
    }

    /**
     *
     * @MethodName：containsListKey
     * @param k
     * @return
     * @ReturnType：boolean
     * @Description：判断缓存是否存在
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:23:37
     * @Modifier：
     * @ModifyTime：
     */
    public boolean containsListKey(RedisTemplate redisTemplate ,String k) {
        return containsKey(redisTemplate, k);
    }

    /**
     *
     * @MethodName：containsGeoKey
     * @param k
     * @return
     * @ReturnType：boolean
     * @Description：判断缓存是否存在
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:34:14
     * @Modifier：
     * @ModifyTime：
     */
    public boolean containsGeoKey(RedisTemplate redisTemplate ,String k) {
        return containsKey(redisTemplate, k);
    }

    private boolean containsKey(RedisTemplate redisTemplate ,String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Throwable t) {
            log.error("判断缓存存在失败key[" + key + ", error[" + t + "]");
        }
        return false;
    }

    /**
     *
     * @MethodName：remove
     * @param k
     * @return
     * @ReturnType：boolean
     * @Description：移除key中所有缓存
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:20:19
     * @Modifier：
     * @ModifyTime：
     */
    public boolean removeValue(RedisTemplate redisTemplate ,String k) {
        return remove(redisTemplate,k);
    }

    /**
     *
     * @MethodName：remove
     * @param k
     * @return
     * @ReturnType：boolean
     * @Description：移除key中所有缓存
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:20:19
     * @Modifier：
     * @ModifyTime：
     */
    public boolean removeSet(RedisTemplate redisTemplate ,String k) {
        return remove(redisTemplate,k);
    }

    /**
     *
     * @MethodName：remove
     * @param k
     * @return
     * @ReturnType：boolean
     * @Description：移除key中所有缓存
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:20:19
     * @Modifier：
     * @ModifyTime：
     */
    public boolean removeList(RedisTemplate redisTemplate ,String k) {
        return remove(redisTemplate, k);
    }

    /**
     *
     * @MethodName：removeGeo
     * @param k
     * @return
     * @ReturnType：boolean
     * @Description：移除key中所有缓存
     * @Creator：chenchuanliang
     * @CreateTime：2017年5月18日上午11:36:23
     * @Modifier：
     * @ModifyTime：
     */
    public boolean removeGeo(RedisTemplate redisTemplate ,String k) {
        return remove(redisTemplate, k);
    }

    private boolean remove(RedisTemplate redisTemplate ,String key) {
        try {
            redisTemplate.delete(key);
            return true;
        } catch (Throwable t) {
            log.error("移除缓存失败key[" + key + ", error[" + t + "]");
        }
        return false;
    }

    /**
     * 缓存一个hash键值对到hash表
     * @MethodName：cacheHash
     * @param key
     * @param hashKey
     * @param value
     * @param time
     * @return
     * @ReturnType：boolean
     * @Description：
     * @Creator：chenchuanliang
     * @CreateTime：2017年6月27日上午10:43:25
     * @Modifier：
     * @ModifyTime：
     */
    public boolean cacheHash(RedisTemplate redisTemplate ,String key, String hashKey, String value, long time){
        try {
            HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
            opsForHash.put(key, hashKey, value);
            if (time > 0) redisTemplate.expire(key, time, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("缓存失败key[" + key + ", error[" + e + "]");
        }
        return false;
    }

    /**
     * 缓存一个map到hash表
     * @MethodName：cacheHash
     * @param key
     * @param map
     * @param time
     * @return
     * @ReturnType：boolean
     * @Description：
     * @Creator：chenchuanliang
     * @CreateTime：2017年6月27日上午10:45:27
     * @Modifier：
     * @ModifyTime：
     */
    public boolean cacheHash(RedisTemplate redisTemplate , String key, Map<String, String> map, long time){
        try {
            HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
            opsForHash.putAll(key, map);
            if (time > 0) redisTemplate.expire(key, time, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("缓存失败key[" + key + ", error[" + e + "]");
        }
        return false;
    }

    /**
     * 通过key获取一个map
     * @MethodName：getHash
     * @param key
     * @return
     * @ReturnType：Map<String,String>
     * @Description：
     * @Creator：chenchuanliang
     * @CreateTime：2017年6月27日上午10:48:21
     * @Modifier：
     * @ModifyTime：
     */
    public Map<String, String> getHash(RedisTemplate redisTemplate ,String key){
        try {
            HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
            return opsForHash.entries(key);
        } catch (Exception e) {
            log.error("获取缓存失败key[" + key + ", error[" + e + "]");
        }
        return null;
    }

    /**
     * 获取key对应map中所有的keys
     * @MethodName：getHashKey
     * @param key
     * @return
     * @ReturnType：Set<String>
     * @Description：
     * @Creator：chenchuanliang
     * @CreateTime：2017年6月27日上午10:49:16
     * @Modifier：
     * @ModifyTime：
     */
    public Set<String> getHashKey(RedisTemplate redisTemplate ,String key){
        try {
            HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
            return opsForHash.keys(key);
        } catch (Exception e) {
            log.error("获取缓存失败key[" + key + ", error[" + e + "]");
        }
        return null;
    }

    /**
     * 获取key对应map中所有的values
     * @MethodName：getHashValues
     * @param key
     * @return
     * @ReturnType：List<String>
     * @Description：
     * @Creator：chenchuanliang
     * @CreateTime：2017年6月27日上午10:49:55
     * @Modifier：
     * @ModifyTime：
     */
    public List<String> getHashValues(RedisTemplate redisTemplate ,String key){
        try {
            HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
            return opsForHash.values(key);
        } catch (Exception e) {
            log.error("获取缓存失败key[" + key + ", error[" + e + "]");
        }
        return null;
    }

    /**
     * 删除key对应hashMap中key的值.或删除整个key对应map
     * @MethodName：delete
     * @param key
     * @param hashKeys
     * @return
     * @ReturnType：List<String>
     * @Description：
     * @Creator：chenchuanliang
     * @CreateTime：2017年6月27日上午10:51:22
     * @Modifier：
     * @ModifyTime：
     */
    public boolean deleteHash(RedisTemplate redisTemplate ,String key, String... hashKeys){
        try {
            if (hashKeys == null || hashKeys.length == 0) {
                redisTemplate.delete(key);
            } else {
                HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
                opsForHash.delete(key, hashKeys);
            }
            return true;
        } catch (Exception e) {
            log.error("获取缓存失败key[" + key + ", error[" + e + "]");
        }
        return false;
    }

    /**
     * 获取key对应的过期时间, 秒
     * @MethodName：getExpireTime
     * @param key
     * @return
     * @ReturnType：Long
     * @Description：
     * @Creator：yangbiao
     * @CreateTime：2017年7月10日上午9:51:22
     * @Modifier：
     * @ModifyTime：
     */
    public Long getExpireTimeValue(RedisTemplate redisTemplate ,String key){
        Long expire = -2L;
        try {
            expire = redisTemplate.getExpire(key);

        }catch (Exception e){
            log.error("获取缓存剩余时间失败key[" + key + ", error[" + e + "]");
        }
        return expire;
    }
}
