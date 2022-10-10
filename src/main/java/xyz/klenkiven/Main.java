package xyz.klenkiven;

import com.sun.source.tree.Tree;
import xyz.klenkiven.collection.BPlusTreeMap.BPlusTreeMap;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        BPlusTreeMap<Integer, Integer> map = new BPlusTreeMap<>(4000);
        // TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < 10000000; i++) {
            data.add(i);
        }
        long startTime = System.currentTimeMillis();
        System.out.println(System.currentTimeMillis() - startTime);
    }
}
