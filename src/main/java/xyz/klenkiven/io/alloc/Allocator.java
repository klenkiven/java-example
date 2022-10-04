package xyz.klenkiven.io.alloc;

import xyz.klenkiven.io.Page;

import java.io.Closeable;

/**
 * 空间分配器
 */
public interface Allocator extends Closeable {

    /**
     * 获取一个可以使用的空间（空间大小为 PAGE_SIZE）
     * @return 文件指针
     */
    int allocate();

    /**
     * 释放这个页面的存储空间
     * @param pageNo 页面
     */
    void drop(int pageNo);
}
