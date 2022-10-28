package zzj.klenkiven.collection.trie;

public class TrieTest {
    public static void main(String[] args) {
        // 功能测试
        functionalTest();
    }

    public static void functionalTest() {
        Trie trie = new Trie();
        System.out.println("第一次插入 Hello：" + trie.insert("Hello"));
        System.out.println("第二次插入 Hello：" + trie.insert("Hello"));

        trie.insert("你好");
        trie.insert("Hell");
        trie.insert("HelloHi");
        trie.insert("World");

        System.out.println("当前 Trie 树的大小：" + trie.size());
        System.out.println("查询 Hello：" + trie.contains("Hello"));
        System.out.println("删除 Hello, 树的大小：" + trie.size());
        trie.remove("Hello");
        System.out.println("删除后查询 Hello：" + trie.contains("Hello"));
    }
}
