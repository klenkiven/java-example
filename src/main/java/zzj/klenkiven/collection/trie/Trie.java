package zzj.klenkiven.collection.trie;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Trie {

    static class TrieNode {
        int codePoint;
        boolean isEnd;
        Map<Integer, TrieNode> children;

        public TrieNode(int codePoint, boolean isEnd) {
            this.codePoint = codePoint;
            this.isEnd = isEnd;
            this.children = new HashMap<>();
        }
    }

    private int size;
    private final TrieNode head;

    public Trie() {
        this.head = new TrieNode(-1, false);
        this.size = 0;
    }

    /**
     * 插入一个单词，如果存在那么插入，并且容量增加；如果不存在，那么不做操作
     * @param word 单词
     * @return 如果完成插入，返回 true；否则返回 false
     */
    public boolean insert(String word) {
        final int len = word.length();
        TrieNode node = head;
        for (int i = 0; i < len; i++) {
            int codePoint = word.codePointAt(i);
            node = findOrAddChildNode(node, codePoint);
        }
        // 如果已经存在，那么返回 False
        if (node.isEnd)
            return false;

        // 否则，增加字典树数量，并返回 True
        node.isEnd = true;
        ++size;
        return true;
    }


    public boolean contains(String word) {
        final int len = word.length();
        TrieNode node = head;
        for (int i = 0; i < len; i++) {
            int codePoint = word.codePointAt(i);
            TrieNode child = findChildNode(node, codePoint);
            if (child == null) return false;
            else node = child;
        }
        return node.isEnd;
    }

    /**
     * 将单词树中的一个单词删除
     * @param word 单词
     * @return 如果删除成功，返回true；如果单词不存在，那么删除失败，返回false
     */
    public boolean remove(String word) {
        final int len = word.length();
        TrieNode node = head;
        ArrayDeque<TrieNode> stack = new ArrayDeque<>(len);
        for (int i = 0; i < len; i++) {
            int codePoint = word.codePointAt(i);
            TrieNode child = findChildNode(node, codePoint);
            if (child == null) return false;
            node = child;
            stack.push(node);
        }

        if (!stack.isEmpty() && !stack.peek().isEnd)
            return false;

        // 删除节点
        if (!stack.isEmpty() && stack.peek().isEnd)
            stack.peek().isEnd = false;
        --size;
        return true;
    }

    /**
     * @return 字典树单词数量
     */
    public int size() {
        return size;
    }

    private int trieChildrenNum(TrieNode node) {
        return node.children.size();
    }

    private TrieNode findChildNode(TrieNode node, int codePoint) {
        return node.children.get(codePoint);
    }

    private TrieNode findOrAddChildNode(TrieNode node, int codePoint) {
        TrieNode child;

        if ((child = findChildNode(node, codePoint)) == null) {
            child = new TrieNode(codePoint, false);
            node.children.put(codePoint, child);
        }

        return child;
    }

}
