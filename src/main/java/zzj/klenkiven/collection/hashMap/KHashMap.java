package zzj.klenkiven.collection.hashMap;

import java.util.Map;
import java.util.Objects;

public class KHashMap<K, V> {

    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    private static final int MAX_CAPACITY = 1 << 30;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * HashMap 的键值对（链表）
     */
    static class Node<K, V> implements Map.Entry<K, V> {
        final int hash;
        K key;
        V value;
        Node<K, V> next;

        Node(K key, V value, Node<K, V> next) {
            this.hash = hash(key);
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() { return key; }

        @Override
        public V getValue() { return value; }

        @Override
        public V setValue(V value) {
            V oldVal = this.value;
            this.value = value;
            return oldVal;
        }
    }

    /* ---------------- Static utilities -------------- */
    // 计算对象哈希值
    static int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    // 获取容量的最高位
    // cap >= 0
    static int tableSizeFor(int cap) {
        // 0000 0000 0001 0000 0000 1001 0000 1001 1101
        // numberOfLeadingZeros = 11
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        // Shift n bit
        // 0000 0000 0001 1111 1111 1111 1111 1111 1111
        return cap <= DEFAULT_INITIAL_CAPACITY ? DEFAULT_INITIAL_CAPACITY : n + 1;
    }
    /* ---------------- Static utilities -------------- */

    // 当前 HashMap 的插槽容量
    private int threshold;
    // 装填因子
    private final float loadFactor;
    // 当前HashMap的大小
    private int size;
    private Node<K, V>[] table;

    public KHashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }

    public KHashMap(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    public KHashMap(int capacity, float loadFactor) {
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(capacity);
    }

    // 获取值
    public V get(K key) {
        Node<K, V> node;
        return (node = getNode(key)) != null ? node.getValue() : null;
    }

    public Node<K, V> getNode(K key) {
        Node<K, V> node; Node<K, V>[] tab; int cap;
        int hash = hash(key);
        // 此节点有值的情况
        if ((tab = table) != null && (cap = tab.length) != 0 &&
                (node = tab[hash & (cap - 1)]) != null) {
            // 大部分情况不会发生 Hash 碰撞，优先检查第一个
            // HASH && EQUALS
            if (node.hash == hash && Objects.equals(key, node.key))
                return node;

            // 检查剩余的节点
            while ((node = node.next) != null) {
                // HASH && EQUALS
                if (node.hash == hash && Objects.equals(key, node.key))
                    return node;
            }
        }

        return null;
    }

    // 放入新的键值对，如果发生的是更新，那么返回原来的值（或者 NULL）
    public V put(K key, V value) {
        Node<K, V>[] tab; int cap = 0, hash = hash(key);

        // 如果哈希表尚未初始化，那么先完成初始化
        if ((tab = table) == null || (cap = tab.length) == 0) {
            cap = (tab = resize()).length;
        }

        // 查找哈希表中的值
        Node<K, V> node = getNode(key);
        V oldValue = null;
        if (node == null) {
            tab[hash & (cap - 1)] = new Node<>(key, value, null);
        } else {
            oldValue = node.value;
            node.value = value;
        }

        // 判断是否需要重新 resize
        if (++size > threshold)
            resize();

        return oldValue;
    }

    // resize 主要用于 Rehash 或者 初始化这个HashMap的时候使用
    final Node<K, V>[] resize() {
        Node<K, V>[] oldTable = table;
        int oldCapacity = (oldTable == null) ? 0 : oldTable.length;
        int oldThreshold = threshold;

        // 参数扩容
        int newCapacity, newThreshold = 0;
        if (oldCapacity > 0) {          // [EXPAND] 为 HashMap 扩容
            newCapacity = oldCapacity << 1;
        } else if(oldThreshold > 0) {   // [INITIALIZING] 在初始化 HashMap，在初始化之处就设定了 capacity
            newCapacity = oldThreshold;
        } else {                        // [INITIALIZING] 构造无参数初始化 HashMap
            newCapacity = DEFAULT_INITIAL_CAPACITY;
            newThreshold = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        // 初始化 Threshold
        if (newThreshold == 0) {
            float tempThreshold = newCapacity * loadFactor;
            newThreshold = (newCapacity <= MAX_CAPACITY && tempThreshold <= MAX_CAPACITY ?
                    (int) tempThreshold : MAX_CAPACITY);
        }

        // 容器扩容
        threshold = newThreshold;
        @SuppressWarnings({"unchecked"})
        Node<K,V>[] newTable = (Node<K,V>[])new Node[newCapacity];
        table = newTable;

        // Rehash
        if (oldTable != null) rehash(oldTable, newTable);

        return newTable;
    }

    // 扩容过程中 Rehash 处理
    private void rehash(Node<K, V>[] oldTable, Node<K, V>[] newTable) {
        int oldCapacity = oldTable.length;
        int newCapacity = newTable.length;
        for (int i = 0; i < oldCapacity; i++) {
            Node<K, V> entry;
            if ((entry = oldTable[i]) != null) {
                // 释放 旧哈希表 空间
                oldTable[i] = null;
                if (entry.next == null) {   // 单节点处理
                    newTable[entry.hash & (newCapacity-1)] = entry;
                } else {                    // 拉链处理
                    // 拉两条链表，一条是高位，一条是低位
                    Node<K, V> loHead = null, loTail = null;
                    Node<K, V> hiHead = null, hiTail = null;

                    while (entry != null) {
                        // 扩展链表新位置 0 尾插法
                        if ((entry.hash & (newCapacity >> 1)) == 0) {
                            if (loHead == null) loHead = entry; // 高位初始化
                            else loTail.next = entry;           // 拉链
                            loTail = entry;                     // 调整尾指针位置
                        }
                        // 扩展链表新位置 1 尾插法
                        else {
                            if (hiHead == null) hiHead = entry; // 高位初始化
                            else hiTail.next = entry;           // 拉链
                            hiTail = entry;                     // 调整尾指针位置
                        }

                        entry = entry.next;
                    }

                    // 两个位置处理
                    if (hiHead != null) newTable[hiHead.hash & (newCapacity - 1)] = hiHead;
                    if (loHead != null) newTable[loHead.hash & (newCapacity - 1)] = loHead;
                }
            }
        }
    }
}
