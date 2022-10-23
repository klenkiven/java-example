package zzj.klenkiven.collection.BPlusTreeMap;

import zzj.klenkiven.collection.BPlusTreeMap.utils.PageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BPlusTreeMap<K extends Comparable<K>, V> {
    public static HashMap<Integer, NodePage<?, ?>> pageCache = new HashMap<>();
    int order;
    int topPageId;
    int headPageId;

    int tailPageId;

    public BPlusTreeMap(int order) {
        this.order = order;
        NodePage<K, V> page = createPage();
        topPageId = page.id;
        headPageId = page.id;
        tailPageId = page.id;
    }

    public NodePage<K, V> getNodePageById(int id) {
        return (NodePage<K, V>) pageCache.get(id);
    }

    private NodePage<K, V> createPage() {
        int newPageId = PageUtil.getNewPageId();
        NodePage<K, V> temp = new NodePage<>(newPageId, -1, -1, -1);
        pageCache.put(newPageId, temp);
        return temp;
    }

    public V put(K key, V value) {
        return putVal(key, value);
    }

    public V floor(K key) {
        SearchRes<K, V> nodePage = getNodePage(key);
        if (nodePage == null) return null;
        NodeIndex<K, V> nodeIndex = nodePage.nodePage.nodeList.get(nodePage.pos);
        if (nodeIndex instanceof Node<K, V> node) {
            if (key.compareTo(node.key) == 0) return node.value;
            if (nodePage.pos > 0) {
                NodeIndex<K, V> nodeIndexTemp = nodePage.nodePage.nodeList.get(nodePage.pos - 1);
                if (nodeIndexTemp instanceof Node<K, V> n) return n.value;
            } else {
                if (nodePage.nodePage.prePageId == -1) return null;
                List<NodeIndex<K, V>> nodeList = getNodePageById(nodePage.nodePage.prePageId).nodeList;
                if (nodeList.get(nodeList.size() - 1) instanceof Node<K, V> n) return n.value;
            }
        }
        return null;
    }
    public V ceiling(K key) {
        SearchRes<K, V> searchRes = getNodePage(key);
        if (searchRes == null) return null;
        NodeIndex<K, V> nodeIndex = searchRes.nodePage.nodeList.get(searchRes.pos);
        if (nodeIndex instanceof Node<K, V> node) return node.value;
        return null;
    }


    public V getFirst() {
        NodePage<K, V> nodePageById = getNodePageById(this.headPageId);
        if (nodePageById == null) return null;
        if (nodePageById.nodeList.get(0) instanceof Node<K, V> node) {
            return node.value;
        }
        return null;
    }

    public V getLast() {
        NodePage<K, V> nodePageById = getNodePageById(this.tailPageId);
        if (nodePageById == null) return null;
        if (nodePageById.nodeList.get(nodePageById.nodeList.size() - 1) instanceof Node<K, V> node) {
            return node.value;
        }
        return null;
    }

    public V get(K key) {
        return getVal(key);
    }

    private V getVal(K key) {
        SearchRes<K, V> searchRes = getNodePage(key);
        if (searchRes == null) return null;
        NodeIndex<K, V> nodeIndex = searchRes.nodePage.nodeList.get(searchRes.pos);
        if (!(nodeIndex instanceof Node<K,V> node)) return null;
        if (key.compareTo(node.key) != 0) return null;
        return node.value;
    }


    public List<NodeIndex<K, V>> getList() {
        List<NodeIndex<K, V>> res = new ArrayList<>();
        int headPageId = this.headPageId;
        while (headPageId != -1) {
            NodePage<K, V> nodePageById = getNodePageById(headPageId);
            res.addAll(nodePageById.nodeList);
            headPageId = nodePageById.nextPageId;
        }
        return res;
    }

    private SearchRes<K, V> getNodePage(K key) {
        NodePage<K, V> current = this.getNodePageById(this.topPageId);
        if (current.nodeList.size() == 0) return null;
        for(;;) {
            int i = findLeft(current.nodeList, key);
            if (i >= current.nodeList.size()) return null;
            NodeIndex<K, V> nodeIndex = current.nodeList.get(i);
            if (nodeIndex instanceof Node<K, V>) {
                return new SearchRes<>(current, i);
            }
            current = this.getNodePageById(nodeIndex.bottomPageId);
        }
    }

    public V remove(K key) {
        return removeVal(key);
    }

    private V removeVal(K key) {
        SearchRes<K, V> searchRes = getNodePage(key);
        if (searchRes == null) return null;
        NodePage<K, V> nodePage = searchRes.nodePage;
        NodeIndex<K, V> nodeIndex = nodePage.nodeList.get(searchRes.pos);
        if (!(nodeIndex instanceof Node<K,V> node)) return null;
        if (key.compareTo(node.key) != 0) return null;
        solveRemove(nodePage, searchRes.pos);
        return node.value;
    }

    private void solveRemove(NodePage<K, V> nodePage, int pos) {
        int minOrder = order >> 1;
        for(;;) {
            NodeIndex<K, V> remove = nodePage.nodeList.remove(pos);
            if (nodePage.id == topPageId) {
                if (remove instanceof Node<K,V>) {
                    return ;
                }
                if (nodePage.nodeList.size() == 1) {
                    NodeIndex<K, V> nodeIndex = nodePage.nodeList.get(0);
                    NodePage<K, V> newTop = this.getNodePageById(nodeIndex.bottomPageId);
                    newTop.parentPageId = -1;
                    pageCache.remove(this.topPageId);
                    this.topPageId = newTop.id;
                }
                return ;
            }
            if (nodePage.nodeList.size() >= minOrder) return;
            NodePage<K, V> mergePage;
            int temp = 1;
            if (nodePage.nextPageId == -1 || getNodePageById(nodePage.nextPageId).parentPageId != nodePage.parentPageId) {
                temp = -1;
                mergePage = getNodePageById(nodePage.prePageId);
            } else {
                mergePage = getNodePageById(nodePage.nextPageId);
            }
            NodePage<K, V> parent = this.getNodePageById(nodePage.parentPageId);
            int posTemp = findLeft(parent.nodeList, remove.key);
            if (mergePage.nodeList.size() + nodePage.nodeList.size() > order) {
                NodeIndex<K, V> pad;
                // 如果向后寻求填充
                if (temp == 1) pad = mergePage.nodeList.remove(0);
                else pad = mergePage.nodeList.remove(mergePage.nodeList.size() - 1);
                if (temp == 1) nodePage.nodeList.add(pad);
                else nodePage.nodeList.add(0, pad);
                pad.nodePageId = nodePage.id;
                // 修改parent指针
                if (pad.bottomPageId != -1) this.getNodePageById(pad.bottomPageId).parentPageId = nodePage.id;
                // 向上传导
                if (temp == 1) parent.nodeList.get(posTemp).key = pad.key;
                else parent.nodeList.get(posTemp - 1).key = mergePage.getPageMaxKey();
                return ;
            } else {
                for (NodeIndex<K, V> nodeIndex : mergePage.nodeList) {
                    if (nodeIndex.bottomPageId != -1) this.getNodePageById(nodeIndex.bottomPageId).parentPageId = nodePage.id;
                }
                if (temp == 1) {
                    // 修改
                    nodePage.nodeList.addAll(mergePage.nodeList);
                    parent.nodeList.get(posTemp).key = nodePage.getPageMaxKey();
                    nodePage.nextPageId = mergePage.nextPageId;
                    if (mergePage.nextPageId != -1) {
                        this.getNodePageById(nodePage.nextPageId).prePageId = nodePage.id;
                    }
                } else {
                    mergePage.nodeList.addAll(nodePage.nodeList);
                    nodePage.nodeList = mergePage.nodeList;
                    nodePage.prePageId = mergePage.prePageId;
                    if (mergePage.prePageId != -1) {
                        this.getNodePageById(nodePage.prePageId).nextPageId = nodePage.id;
                    }
                }
                if (mergePage.id == headPageId) headPageId = nodePage.id;
                if (mergePage.id == tailPageId) tailPageId = nodePage.id;
                pageCache.remove(mergePage.id);
            }
            nodePage = parent;
            pos = posTemp + temp;
        }
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
                // 判断是否相等
                if (key.compareTo(nodeIndex.key) == 0) {
                    Node<K, V> findAns = (Node<K, V>) nodeIndex;
                    V temp = findAns.value;
                    findAns.value = value;
                    return temp;
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
                        NodeIndex<K, V> kvNodeIndex = temp.get(i);
                        page.nodeList.add(kvNodeIndex);
                        // 更新parent指针
                        if (kvNodeIndex.bottomPageId != -1) this.getNodePageById(kvNodeIndex.bottomPageId).parentPageId = page.id;
                    }
                    current.nodeList = new ArrayList<>();
                    for (int i = mid; i < temp.size(); i++) {
                        current.nodeList.add(temp.get(i));
                    }
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
            // 维护parent指针
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

    public K getPageMinKey() {
        return this.nodeList.get(0).key;
    }

}

class SearchRes<K extends Comparable<K>, V> {
    NodePage<K, V> nodePage;
    int pos;

    SearchRes(NodePage<K, V> nodePage, int pos) {
        this.nodePage = nodePage;
        this.pos = pos;
    }
}