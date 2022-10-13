package zzj.klenkiven;

import zzj.klenkiven.collection.BPlusTreeMap.BPlusTreeMap;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        BPlusTreeMap<Integer, Integer> map = new BPlusTreeMap<>(1000);
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            data.add(i);
        }
        Collections.shuffle(data);
        long startTime = System.currentTimeMillis();
        data.forEach(item -> map.put(item, item - 1));
        System.out.println(System.currentTimeMillis() - startTime);
        Collections.shuffle(data);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000 - 20; i++) {
            map.remove(data.get(i));
        }
        System.out.println(System.currentTimeMillis() - startTime);
        System.out.println(map.getList().toString());
    }
}
