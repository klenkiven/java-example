package zzj.klenkiven.io.prototype;

/**
 * 页面接口
 */
public interface Page {

    /** 默认的页面大小 */
    int PAGE_SIZE = 1024;

    int INVALID_PAGE_NO = -1;

    /**
     * 获取页面标号
     * @return 页面标号
     */
    int getPageNo();

    /**
     * 判断这个页面是否为垃圾页面
     */
    boolean isTrash();

    /**
     * 取消标记为垃圾页
     */
    void unmarkTrash();


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
