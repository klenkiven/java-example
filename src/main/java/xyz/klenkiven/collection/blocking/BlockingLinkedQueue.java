package xyz.klenkiven.collection.blocking;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingLinkedQueue {

    public static void main(String[] args) throws InterruptedException {
        BlockingLinkedQueue que = new BlockingLinkedQueue();

        System.out.println("========== take 阻塞 ==========");
        BlockingLinkedQueue takeQue = que;
        new Thread(() -> {
            System.out.println("[take-thread] 开始阻塞");
            int x = takeQue.take();
            System.out.println("[take-thread] 获取到值：" + x);
            System.out.println("============================");
        }).start();
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                System.out.println("[put-thread] 队列中添加元素：0");
                takeQue.put(0);
            } catch (InterruptedException e) {
                // Do Nothing
            }
        }).start();

        Thread.sleep(3000);

        que = new BlockingLinkedQueue(1);
        System.out.println("========== put 阻塞 ==========");
        BlockingLinkedQueue putQue = que;
        new Thread(() -> {
            try {
                putQue.put(1);
                System.out.println("[put-thread] 开始 put 阻塞");
                putQue.put(2);
                System.out.println("[put-thread] 结束 put 阻塞");
                System.out.println("============================");
            } catch (InterruptedException e) {
                // Do Nothing
            }
        }).start();
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                System.out.print("[take-thread] 获取到：");
                int x = putQue.take();
                System.out.println(x);
            } catch (InterruptedException e) {
                // Do Nothing
            }
        }).start();
    }

    private final AtomicInteger size;
    private final int capacity;
    private final Node head;
    private Node tail;

    private final ReentrantLock putLock = new ReentrantLock();
    private final ReentrantLock takeLock = new ReentrantLock();
    private final Condition notEmpty = takeLock.newCondition();
    private final Condition notFull = putLock.newCondition();

    /**
     * 构造方法是线程安全的
     */
    public BlockingLinkedQueue() {
        this(Integer.MAX_VALUE);
    }

    /**
     * 构造方法是线程安全的
     */
    public BlockingLinkedQueue(int capacity) {
        this.size = new AtomicInteger();
        this.capacity = capacity;
        this.head = new Node(-1);
        this.tail = head;
    }

    /**
     * 入队操作 -- 临界区
     * @param node 入队节点
     */
    private void enqueue(Node node) {
        tail = tail.next = node;
    }

    /**
     * 出队操作 -- 临界区
     */
    private int dequeue() {
        int x = head.next.val;
        head.next = head.next.next;
        return x;
    }

    /**
     * 唤醒一个 满队列 等待中的线程
     * 只有 take 线程会调用
     */
    private void signalNotFull() {
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            notFull.signal();
        } finally {
            putLock.unlock();
        }
    }

    /**
     * 唤醒一个 空队列 等待中的线程
     */
    private void signalNotEmpty() {
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }

    /**
     * 获取大小
     * @return 获取队列大小
     */
    public int size() {
        return this.size.get();
    }

    /**
     * 添加到队列
     * @param x 添加到队列一个值
     */
    public void put(int x) throws InterruptedException {
        final AtomicInteger size = this.size;
        final int s;
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            // 等待非满状态 -- await() 释放锁
            if (size.get() == capacity)
                notFull.await();

            // 入队
            Node node = new Node(x);
            enqueue(node);
            s = size.getAndIncrement();

            // 如果发现队列没有满，那么通知等待的 put 线程
            if (s + 1 < capacity)
                notFull.signal();
        } finally {
            putLock.unlock();
        }

        // 如果新增了一个元素，那么通知所有的 take 线程
        if (s == 0)
            signalNotEmpty();
    }

    /**
     * 拉取队列首的一个值
     * @return 队列首部一个值
     */
    public int take() {
        final int x;
        final int s;
        final AtomicInteger size = this.size;
        final ReentrantLock takeLock = this.takeLock;

        takeLock.lock();
        try {
            // 等待非空状态 -- 释放锁
            if (size.get() == 0)
                notEmpty.await();

            // 弹出一个元素
            x = dequeue();
            s = size.getAndDecrement();

            // 如果队列原来是满的，那么就唤醒一个等待 put 的线程
            if (s == capacity)
                signalNotFull();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            takeLock.unlock();
        }

        return x;
    }

    private static class Node {
        int val;
        Node next;

        Node(int val) {
            this.val = val;
        }
    }
}
