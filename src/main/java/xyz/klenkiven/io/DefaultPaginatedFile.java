package xyz.klenkiven.io;

import xyz.klenkiven.io.alloc.FileAllocator;
import xyz.klenkiven.io.exception.FileCreateException;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static xyz.klenkiven.io.Page.PAGE_SIZE;

public class DefaultPaginatedFile implements PaginatedFile, Closeable {

    private final File file;
    private final FileAllocator allocator;

    public DefaultPaginatedFile(String filename) {
        try {
            file = new File(filename);
            if (!file.exists() && !file.createNewFile())
                throw new FileCreateException();

            allocator = new FileAllocator(file, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page getPage(int pageNo) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            // 定位到指定位置
            raf.seek((long) PAGE_SIZE * pageNo);
            // 读取页面数据
            byte[] data = new byte[PAGE_SIZE];
            int len = raf.read(data, 0, PAGE_SIZE);
            // 返回页面数据
            if (len != -1)
                return new DefaultPage(pageNo, data);
        } catch (IOException e) {
            // 无法找到对应位置的数据
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Page getNewPage() {
        int pageNo = allocator.allocate();
        return new DefaultPage(pageNo, new byte[0]);
    }

    @Override
    public void writePage(Page page) {
        try(RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            raf.seek((long) page.getPageNo() * PAGE_SIZE);
            raf.write(page.getPageData(), 0, PAGE_SIZE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dropPage(Page page) {
        allocator.drop(page.getPageNo());
    }

    @Override
    public void close() throws IOException {
        allocator.close();
    }
}
