package zzj.klenkiven.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class StringAndZipList {
    public static void main(String[] args) {
        JedisPool jedisPool = new JedisPool("localhost", 6379);
        Jedis resource = jedisPool.getResource();

        // Set String 的方式插入键值对
        // 722328 -> 65111352 增加了 64389024
        long usedMemory = currentUsedMemory(resource);
        for (int i = 0; i < 1_000_000; i++) {
            resource.set(String.valueOf(10000000L + i), String.valueOf(30000000L + i));
        }
        System.out.println("使用String消耗内存：" + (currentUsedMemory(resource) - usedMemory)/1024.0/1024.0 + "MB");

        // 使用压缩表来有效压缩空间
        // 解决方案：还是按照字符串的形式进行存储，但是，具体存储的时候可以将key拆解，然后使用 zset 来存储
        // 1. 拆解key "10000001" -> "10000" "001"
        // 2. 设置相关参数：hash-max-ziplist-entries 设置为 1000 保证拆解后的 zset 可以全部存储进ziplist
        //      config set hash-max-ziplist-entries  1000
        //      config set hash-max-ziplist-value    64
        // 3. 将结果放入 redis 中
        //      zset set 10000 001 30000001
        usedMemory = currentUsedMemory(resource);
        resource.configSet("hash-max-ziplist-entries", "1000");
        resource.configSet("hash-max-ziplist-value", "64");
        for (int i = 0; i < 1_000_000; i++) {
            String key = String.valueOf(10000000L + i);
            resource.hset(
                    key.substring(0, key.length() - 3),
                    key.substring(key.length() - 3),
                    String.valueOf(30000000L + i)
            );
        }
        System.out.println("使用Hash消耗内存：" + (currentUsedMemory(resource) - usedMemory)/1024.0/1024.0 + "MB");

        // 清理缓存
        resource.flushAll();

        resource.close();
        jedisPool.close();
    }

    private static long currentUsedMemory(Jedis resource) {
        String usedMemoryStr = resource.info("memory")
                .split("\r\n")[1]
                .substring(12);
        return Long.parseLong(usedMemoryStr);
    }
}
