package xyz.klenkiven.io.prototype;

import java.io.IOException;
import java.util.Random;

import static xyz.klenkiven.io.prototype.Page.PAGE_SIZE;

public class Main {
    private static final Random rand = new Random();

    public static void main(String[] args) throws IOException {
        try (DefaultPaginatedFile paginatedFile = new DefaultPaginatedFile("test.dat")) {
            paginatedFile.getPage(0);

            // 创建 100个 页面
            long start = System.currentTimeMillis();
            for (int i = 0; i < (100); i++) {
                Page newPage = paginatedFile.getNewPage();
                newPage = new DefaultPage(newPage.getPageNo(), genRandomData(PAGE_SIZE));
                paginatedFile.writePage(newPage);
            }
            System.out.println("创建 100页 数据：" + (System.currentTimeMillis() - start) + "ms");
            paginatedFile.dumpPaginateFile();

            // 回收 33% 页面数据
            int recycledPageCount = 0;
            for (int i = 1; i < (100); i++) {
                try {
                    if (i % 3 == 0) {
                        paginatedFile.dropPage(new DefaultPage(i, new byte[0]));
                        recycledPageCount++;
                    }
                } catch (Exception e) {
                    System.err.println("Exception: " + e.getClass().getName() + ", Message: " + e.getMessage());
                }
            }
            System.out.println("共回收了 " + recycledPageCount + " 页数据");
            paginatedFile.dumpPaginateFile();

            // 重新使用页面数据
            for (int i = 0; i < recycledPageCount; i++) {
                Page newPage = paginatedFile.getNewPage();
                newPage = new DefaultPage(newPage.getPageNo(), genRandomData(PAGE_SIZE));
                paginatedFile.writePage(newPage);
            }
            // 使用回收的页面数据
            System.out.println("使用上面回收的页面空间：" + recycledPageCount + "页面");
            paginatedFile.dumpPaginateFile();

            // 使用新页面空间
            for (int i = 0; i < 50; i++) {
                Page newPage = paginatedFile.getNewPage();
                newPage = new DefaultPage(newPage.getPageNo(), genRandomData(PAGE_SIZE));
                paginatedFile.writePage(newPage);
            }
            System.out.println("使用新页面数据");
            paginatedFile.dumpPaginateFile();
        }
    }

    public static byte[] genRandomData(int pageSize) {
        byte[] data = new byte[pageSize];
        rand.nextBytes(data);
        return data;
    }
}
