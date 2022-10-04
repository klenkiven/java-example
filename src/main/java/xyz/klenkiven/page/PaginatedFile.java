package xyz.klenkiven.page;

import java.io.*;

public class PaginatedFile {
    private final File file;

    public PaginatedFile(String filename) {
        file = new File(filename);
        try {
            if (!file.exists() && !file.createNewFile())
                throw new RuntimeException("文件创建或者打开异常");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Page getPage(int pageNo) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(pageNo * 4096L);
            byte[] data = new byte[4096];
            int len = raf.read(data, 0, 4096);

            if (len != -1) return new Page(pageNo, data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void writePage(Page page) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            raf.seek(page.getPageNo() * 4096L);
            raf.write(page.getData(), 0, 4096);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
