package xyz.klenkiven.io;

import java.util.HashMap;
import java.util.Map;

public class BufferPool {

    /** 默认的缓冲池大小 */
    private static final int DEFAULT_BUFFER_SIZE = 50;

    /** LRU缓存节点 */
    static class Node {
        long pageNo;
        AbstractPage abstractPage;
        int nextNode;

        Node(AbstractPage abstractPage, int nextNode) {
            this.pageNo = abstractPage.getPageNo();
            this.abstractPage = abstractPage;
            this.nextNode = nextNode;
        }

        @Override
        public int hashCode() { return (int) pageNo; }

        @Override
        public boolean equals(Object obj) { return obj instanceof Node && ((Node) obj).pageNo == pageNo; }
    }

    private final int capacity;
    private final Node[] buffer;
    private final Map<Long, Node> bufferMap;
    private int size;

    public BufferPool() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public BufferPool(int capacity) {
        this.capacity = capacity;
        this.buffer = new Node[capacity];

        this.bufferMap = new HashMap<>(capacity);
        this.size = 0;
    }

}
