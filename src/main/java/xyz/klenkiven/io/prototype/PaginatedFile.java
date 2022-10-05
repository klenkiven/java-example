package xyz.klenkiven.io.prototype;

import java.io.Closeable;

/**
 * 分页的文件
 */
public interface PaginatedFile extends Closeable {

    /**
     * 获取文件中页面ID的页面
     * @return 页面
     */
    Page getPage(int pageNo);

    /**
     * 获取一个新页面（分配一个可用页面）
     * @return 新页面
     */
    Page getNewPage();

    /**
     * 将页面内容刷入磁盘中
     * @param page 页面
     */
    void writePage(Page page);

    /**
     * 释放一个页面的空间
     * @param page 页面
     */
    void dropPage(Page page);
}
