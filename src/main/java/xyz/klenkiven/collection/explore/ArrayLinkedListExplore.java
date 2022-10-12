package xyz.klenkiven.collection.explore;

import java.util.ArrayList;
import java.util.LinkedList;

public class ArrayLinkedListExplore {
    public static void main(String[] args) {
        Object obj= new Object();

        // ArrayList
        ArrayList<Object> arrayList = new ArrayList<>();
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < 500000; i++) {
            arrayList.add(0, obj);
        }
        System.out.println("ArrayList insert cost time: " + (System.currentTimeMillis() - t1) + "ms");
        arrayList = new ArrayList<>();
        t1 = System.currentTimeMillis();
        for (int i = 0; i < 500000; i++) {
            arrayList.add(obj);
        }
        System.out.println("ArrayList append cost time: " + (System.currentTimeMillis() - t1) + "ms");

        // LinkedList
        LinkedList<Object> linkedList = new LinkedList<>();
        t1 = System.currentTimeMillis();
        for (int i = 0; i < 500000; i++) {
            linkedList.add(0, obj);
        }
        System.out.println("LinkedList insert cost time: " + (System.currentTimeMillis() - t1) + "ms");
        linkedList = new LinkedList<>();
        t1 = System.currentTimeMillis();
        for (int i = 0; i < 500000; i++) {
            linkedList.add(obj);
        }
        System.out.println("LinkedList append cost time: " + (System.currentTimeMillis() - t1) + "ms");
    }
}
