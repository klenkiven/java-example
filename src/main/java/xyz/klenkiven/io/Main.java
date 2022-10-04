package xyz.klenkiven.io;

import java.io.IOException;
import java.util.Random;

import static xyz.klenkiven.io.Page.PAGE_SIZE;

public class Main {
    private static final Random rand = new Random();

    public static void main(String[] args) throws IOException {
        PaginatedFile paginatedFile = new DefaultPaginatedFile("test.dat");
        Page page = paginatedFile.getPage(0);

        // 创建 1M 页面
        long start = System.currentTimeMillis();
        for (int i = 0; i < (1 << 10); i++) {
            Page newPage = paginatedFile.getNewPage();
            newPage = new DefaultPage(newPage.getPageNo(), genRandomData(PAGE_SIZE));
            paginatedFile.writePage(newPage);
        }
        System.out.println("创建 1MiB 数据：" + (System.currentTimeMillis() - start) + "ms");

        // 回收页面数据
        for (int i = 0; i < (1 << 10); i++) {
            if (i % 3== 0)
                paginatedFile.dropPage(new DefaultPage(i, new byte[0]));
        }

        paginatedFile.close();
    }

    public static byte[] genRandomData(int pageSize) {
        byte[] data = new byte[pageSize];
        rand.nextBytes(data);
        return data;
    }
}
