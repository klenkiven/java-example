package xyz.klenkiven.io;

/**
 * 页面接口
 */
public interface Page {

    /** 默认的页面大小 */
    int PAGE_SIZE = 4096;

    int INVALID_PAGE_NO = -1;

    /**
     * 获取页面标号
     * @return 页面标号
     */
    int getPageNo();

    /**
     * 获取页面的原始数据信息（已对齐）
     * @return 页面原始数据信息
     */
    byte[] getOriginData();

    /**
     * 获取页面的最新数据
     * @return 最新数据
     */
    byte[] getPageData();
}
