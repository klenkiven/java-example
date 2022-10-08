package xyz.klenkiven.collection.BPlusTreeMap;

import xyz.klenkiven.collection.BPlusTreeMap.utils.PageUtil;

import java.security.spec.RSAOtherPrimeInfo;
import java.util.*;

public class BPlusTreeMap<K extends Comparable<K>, V> {
    public static HashMap<Integer, NodePage<?, ?>> pageCache = new HashMap<>();
    int order;
    int topPageId;
    int headPageId;

    public BPlusTreeMap(int order) {
        this.order = order;
        NodePage<K, V> page = createPage();
        topPageId = page.id;
        headPageId = page.id;
    }

    public NodePage<K, V> getNodePageById(int id) {
        return (NodePage<K, V>) pageCache.get(id);
    }

    public NodePage<K, V> createPage() {
        int newPageId = PageUtil.getNewPageId();
        NodePage<K, V> temp = new NodePage<>(newPageId, -1, -1, -1);
        pageCache.put(newPageId, temp);
        return temp;
    }

    public V put(K key, V value) {
        return putVal(key, value);
    }

    public List<NodeIndex<K, V>> getList() {
        System.out.println(this.getNodePageById(this.topPageId).nodeList);
        List<NodeIndex<K, V>> res = new ArrayList<>();
        int headPageId = this.headPageId;
        while (headPageId != -1) {
            NodePage<K, V> nodePageById = getNodePageById(headPageId);
            res.addAll(nodePageById.nodeList);
            headPageId = nodePageById.nextPageId;
        }
        return res;
    }

    private V putVal(K key, V value) {
        Node<K, V> kvNode;
        NodePage<K, V> current = this.getNodePageById(this.topPageId);
        boolean splitMode = false;
        if (current.nodeList.size() == 0) {
            kvNode = new Node<>(key, current.id, value);
            current.nodeList.add(kvNode);
            return null;
        }
        for(;;) {
            NodeIndex<K, V> nodeIndex;
            // 二分查找插入的位置
            int i = findLeft(current.nodeList, key);
            // 如果超过b+树中最大的
            if (i >= current.nodeList.size()) {
                splitMode = true;
                // 如果是叶子节点
                if (current.nodeList.get(0) instanceof Node<K, V>) {
                    kvNode = new Node<>(key, current.id, value);
                    // 添加节点
                    current.nodeList.add(kvNode);
                    // 尝试分裂节点
                    solvePageSplit(current.id, true);
                    // 退出循环
                    return null;
                }
                // 修改最大值
                nodeIndex = current.nodeList.get(current.nodeList.size() - 1);
                nodeIndex.key = key;
                // 切换下一页
                current = this.getNodePageById(nodeIndex.bottomPageId);
                continue;
            }
            // 获取当前索引
            nodeIndex = current.nodeList.get(i);
            // 如果是叶子节点
            if (nodeIndex instanceof Node<K, V>) {
                int tempI = i;
                // 寻找相等的
                while (key.compareTo(nodeIndex.key) == 0) {
                    // 如果有完全相同的
                    if (Objects.equals(key, nodeIndex.key)) {
                        Node<K, V> findAns = (Node<K, V>) nodeIndex;
                        V temp = findAns.value;
                        findAns.value = value;
                        return temp;
                    }
                    // 没有完全相同的
                    if (++tempI >= current.nodeList.size()) break;
                    nodeIndex = current.nodeList.get(tempI);
                }
                kvNode = new Node<>(key, current.id, value);
                // 添加元素，分裂节点
                current.nodeList.add(i, kvNode);
                solvePageSplit(current.id, splitMode);
                return null;
            }
            // 切换下一页
            current = this.getNodePageById(nodeIndex.bottomPageId);
        }
    }

    // 二分搜索！！！！（搜索最左匹配项）！！！！！！！！！！！
    private int findLeft(List<NodeIndex<K, V>> data, K target) {
        int left = 0;
        int right = data.size();
        while (left < right) {
            int mid = (left + right) >> 1;
            NodeIndex<K, V> nodeIndex = data.get(mid);
            if (target.compareTo(nodeIndex.key) <= 0) right = mid;
            else left = mid + 1;
        }
        return right;
    }

    // 分裂节点！！！！！！，从底部向上分裂！！！！！！！！
    private void solvePageSplit(int pageId, boolean splitMode) {
        NodePage<K, V> current = this.getNodePageById(pageId);
        NodePage<K, V> splitPage = null;
        for(;;) {
            if (splitPage != null) {
                // 不是第一次！！！！！
                K pageMaxKey = splitPage.getPageMaxKey();
                int i = findLeft(current.nodeList, pageMaxKey);
                NodeIndex<K, V> kvNodeIndex = new NodeIndex<>(pageMaxKey, current.id, splitPage.id);
                current.nodeList.add(i, kvNodeIndex);
            }
            // 分裂！！！！！！！！！！！
            if (current.nodeList.size() > order) {
                // 创建新的节点！！！！！！！！！！
                NodePage<K, V> page = this.createPage();
                // 更新parent指针
                page.parentPageId = current.parentPageId;
                page.nextPageId = current.id;
                page.prePageId = current.prePageId;
                if (current.prePageId != -1) {
                    this.getNodePageById(page.prePageId).nextPageId = page.id;
                }
                current.prePageId = page.id;
                splitPage = page;
                // 切换底部链表头指针！！！！！！！
                if (current.id == headPageId) headPageId = page.id;
                if (splitMode) {
                    // 优化分裂！！！！！
                    page.nodeList = current.nodeList;

                    current.nodeList = new ArrayList<>();
                    current.nodeList.add(page.nodeList.remove(page.nodeList.size()- 1));
                } else {
                    // 百分之五十分裂！！！
                    List<NodeIndex<K, V>> temp = current.nodeList;
                    int mid = temp.size() >> 1;
                    for (int i = 0; i < mid; i++) {
                        page.nodeList.add(temp.get(i));
                    }
                    current.nodeList = new ArrayList<>();
                    for (int i = mid; i < temp.size(); i++) {
                        current.nodeList.add(temp.get(i));
                    }
                }
                // 更新parent指针
                for (NodeIndex<K, V> kvNodeIndex : page.nodeList) {
                    if (kvNodeIndex.bottomPageId != -1) this.getNodePageById(kvNodeIndex.bottomPageId).parentPageId = page.id;
                }
            } else {
                splitPage = null;
            }
            if (current.parentPageId == -1 || splitPage == null) break;
            current = getNodePageById(current.parentPageId);
        }
        // 产生新的一层
        if (splitPage != null) {
            NodePage<K, V> page = createPage();
            splitPage.parentPageId = page.id;
            current.parentPageId = page.id;
            NodeIndex<K, V> left = new NodeIndex<>(splitPage.getPageMaxKey(), page.id, splitPage.id);
            NodeIndex<K, V> right = new NodeIndex<>(current.getPageMaxKey(), page.id, topPageId);
            // 切换头部指针
            topPageId = page.id;
            page.nodeList.add(left);
            page.nodeList.add(right);
        }
    }

}

class NodeIndex<K extends Comparable<K>, V> {
    K key;
    int nodePageId;
    int bottomPageId;

    public NodeIndex(K key, int nodePageId, int bottomPageId) {
        this.key = key;
        this.nodePageId = nodePageId;
        this.bottomPageId = bottomPageId;
    }

    @Override
    public String toString() {
        return key.toString();
    }
}

class Node<K extends Comparable<K>, V> extends NodeIndex<K, V> {
    V value;

    public Node(K key, int nodePageId, V value) {
        super(key, nodePageId, -1);
        this.value = value;
    }

}

class NodePage<K extends Comparable<K>, V> {

    int id;
    int parentPageId;
    int prePageId;
    int nextPageId;
    List<NodeIndex<K, V>> nodeList;
    public NodePage(int id, int parentPageId, int prePageId, int nextPageId) {
        this.id = id;
        this.parentPageId = parentPageId;
        this.nodeList = new ArrayList<>();
        this.prePageId = prePageId;
        this.nextPageId = nextPageId;
    }


    public K getPageMaxKey() {
        return this.nodeList.get(this.nodeList.size() - 1).key;
    }


}