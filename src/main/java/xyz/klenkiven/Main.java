package xyz.klenkiven;

import xyz.klenkiven.collection.BPlusTreeMap.BPlusTreeMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        BPlusTreeMap<Integer, Integer> bPlusTreeMap = new BPlusTreeMap<>(5000);
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            data.add(i);
        }
        Collections.shuffle(data);
        long startTime = System.currentTimeMillis();
        for (Integer d : data) {
            bPlusTreeMap.put(d, 1);
        }
        System.out.println(System.currentTimeMillis() - startTime);
    }
}