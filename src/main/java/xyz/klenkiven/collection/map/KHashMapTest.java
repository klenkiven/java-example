package xyz.klenkiven.collection.map;

import java.util.HashMap;
import java.util.Random;

public class KHashMapTest {
    public static void main(String[] args) {
        KHashMap<Integer, Integer> kHashMap = new KHashMap<>(16);
        HashMap<Integer, Integer> hashMap = new HashMap<>(16);
        Random random = new Random(1234);

        long start = System.currentTimeMillis();
        for (int i = 0; i < (1 << 20); i++) {
            hashMap.put(random.nextInt(1 << 20), 1234);
        }
        System.out.println("HashMap Put: " + (System.currentTimeMillis() - start) + "ms");
        start = System.currentTimeMillis();
        for (int i = 0; i < 1 << 25; i++) {
            hashMap.get(random.nextInt(1 << 20 + 10));
        }
        System.out.println("HashMap Get: " + (System.currentTimeMillis() - start) + "ms");


        start = System.currentTimeMillis();
        for (int i = 0; i < (1 << 20); i++) {
            kHashMap.put(random.nextInt(1 << 20), 1234);
        }
        System.out.println("KHashMap Put: " + (System.currentTimeMillis() - start) + "ms");
        start = System.currentTimeMillis();
        for (int i = 0; i < 1 << 25; i++) {
            kHashMap.get(random.nextInt(1 << 20 + 10));
        }
        System.out.println("KHashMap Get: " + (System.currentTimeMillis() - start) + "ms");
    }
}
