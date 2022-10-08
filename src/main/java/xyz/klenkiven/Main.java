package xyz.klenkiven;

import xyz.klenkiven.collection.BPlusTreeMap.BPlusTreeMap;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        BPlusTreeMap<Integer, Integer> map = new BPlusTreeMap<>(4000);
        TreeSet<Integer> sy = new TreeSet<>();
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            data.add(i);
        }
        Collections.shuffle(data);
        long startTime = System.currentTimeMillis();
        for (Integer item : data) {
            map.put(item, item - 1);
        }
        System.out.println(System.currentTimeMillis() - startTime);
        Collections.shuffle(data);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000 - 20; i++) {
            map.remove(data.get(i));
        }
        System.out.println(System.currentTimeMillis() - startTime);
        System.out.println(map.getList());
        System.out.println(map.getFirst());
        System.out.println(map.getLast());
    }
}
