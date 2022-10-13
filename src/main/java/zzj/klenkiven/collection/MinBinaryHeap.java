package zzj.klenkiven.collection;

public class MinBinaryHeap {
    public static void main(String[] args) {
        int[] arr = new int[]{5,4,2,7,8,1,0,34,78,23,76,1,1,1,1,1,1};
        MinBinaryHeap binaryHeap = new MinBinaryHeap(arr);
        int size = binaryHeap.size();
        for (int i = 0; i < size; i++) {
            System.out.println("Heap Top: " + binaryHeap.poll() + ", Size: " + binaryHeap.size());
        }
    }

    private int[] heap;
    private int size;
    private final int capacity;

    private MinBinaryHeap(int[] array) {
        this.heap = array;
        this.size = array.length;
        this.capacity = array.length;

        binarify(array);
        ensureSize();
    }

    /**
     * 插入一个数字
     * @param x 数字
     */
    public void offer(int x) {
        ensureSize();
        heap[size++] = x;
        floatUp(size);
    }

    /**
     * 查看堆中的最小值
     * @return 最小值
     */
    public int peek() {
        return size >= 1 ? heap[0] : Integer.MAX_VALUE;
    }

    /**
     * 删除堆顶节点
     */
    public int poll() {
        int top = heap[0];
        swap(0, --size);
        sinkDown(0);
        return top;
    }

    public int size() {
        return size;
    }

    /**
     * 初始化一个堆
     * @param array 待堆化
     */
    private void binarify(int[] array) {
        for (int i = 0; i < size; i++) {
            floatUp(i);
        }
    }

    /**
     * 二叉树上浮
     * @param index 上浮位置
     */
    private void floatUp(int index) {
        if (index == 0) return;
        if (heap[index] < heap[index/2]) {
            swap(index, index/2);
            floatUp(index/2);
        }
    }

    /**
     * 二叉树下沉
     * @param index 下沉位置
     */
    private void sinkDown(int index) {
        if (index >= size || index * 2 >= size) return;

        // 下沉找子树最小的，如果比最小的还小，那就交换
        int childMinIndex = minChildIndex(index);
        if (heap[index] > heap[childMinIndex]) {
            swap(index, childMinIndex);
            sinkDown(childMinIndex);
        }
    }

    /**
     * 返回子树中较大的一个索引
     * @param index 索引
     * @return 子树中较大的一个索引
     */
    private int minChildIndex(int index) {
        if (index * 2 + 1 >= size) return index * 2;
        return heap[index * 2] < heap[index * 2 + 1] ? index * 2 : index * 2 + 1;
    }

    /**
     * 保证容量
     */
    private void ensureSize() {
        if (size == capacity) {
            int newCapacity = capacity + capacity / 2;
            int[] newHeap = new int[newCapacity];
            System.arraycopy(heap, 0, newHeap, 0, size);
            this.heap = newHeap;
        }
    }

    private void swap(int i, int j) {
        heap[i] ^= heap[j];
        heap[j] ^= heap[i];
        heap[i] ^= heap[j];
    }

}
