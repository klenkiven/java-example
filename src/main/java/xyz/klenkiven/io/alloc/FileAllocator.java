package xyz.klenkiven.io.alloc;

import xyz.klenkiven.io.AbstractPage;
import xyz.klenkiven.io.Page;
import xyz.klenkiven.io.PaginatedFile;

import java.io.*;

import static xyz.klenkiven.io.Page.INVALID_PAGE_NO;
import static xyz.klenkiven.io.Page.PAGE_SIZE;

public class FileAllocator implements Allocator {

    static class FileAllocPage extends AbstractPage {
        int headAvailablePageNo = INVALID_PAGE_NO;
        int tailAvailablePageNo = INVALID_PAGE_NO;

        public FileAllocPage(Page firstPage) { super(firstPage); }

        @Override
        protected void constructData(DataInputStream dis) throws IOException {
            headAvailablePageNo = dis.readInt();
            tailAvailablePageNo = dis.readInt();
        }

        @Override
        protected void writePageContent(DataOutputStream dos) throws IOException {
            dos.writeInt(headAvailablePageNo);
            dos.writeInt(tailAvailablePageNo);
        }
    }

    static class AvailablePage extends AbstractPage {
        int nextPage;

        public AvailablePage(Page page) { super(page); }

        public AvailablePage(int pageNo) {
            super(pageNo, new byte[0]);
            this.nextPage = INVALID_PAGE_NO;
        }

        @Override
        protected void constructData(DataInputStream dis) throws IOException {
            nextPage = dis.readInt();
        }

        @Override
        protected void writePageContent(DataOutputStream dos) throws IOException {
            dos.writeInt(nextPage);
        }
    }

    private final File file;
    private final PaginatedFile paginatedFile;
    private int head;
    private int tail;
    private AvailablePage tailPage;

    public FileAllocator(File file, PaginatedFile paginatedFile) {
        this.file = file;
        this.paginatedFile = paginatedFile;

        initializeAllocator();
    }

    /**
     * 初始化文件分配器
     */
    private void initializeAllocator() {
        Page firstPage = ensureFirstPage();

        // 初始化分配器
        FileAllocPage fileAllocPage = new FileAllocPage(firstPage);
        head = fileAllocPage.headAvailablePageNo;
        tail = fileAllocPage.tailAvailablePageNo;

        // 保证始终有一个可用于分配的页面
        if (tail != INVALID_PAGE_NO)
            tailPage = new AvailablePage(paginatedFile.getPage(tail));

        ensureAvailablePage();
    }

    private Page ensureFirstPage() {
        Page firstPage;
        if ((firstPage = paginatedFile.getPage(0)) != null) return firstPage;
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            // 初始化一个空文件信息页面
            firstPage = new FileAllocPage(null);
            raf.seek(0);
            raf.write(firstPage.getPageData(), 0, PAGE_SIZE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return firstPage;
    }

    @Override
    public synchronized int allocate() {
        ensureAvailablePage();

        AvailablePage availablePage = new AvailablePage(paginatedFile.getPage(head));
        head = availablePage.nextPage;
        return head;
    }

    /**
     * 始终保证有一个可分配的空闲页面
     */
    private void ensureAvailablePage() {
        if (head == INVALID_PAGE_NO) head = appendNewPage();
    }

    /**
     * 追加节点（因为尾部节点已经耗尽需要创建新的节点）
     * 更新尾部页面指针 和 尾部页面
     */
    private int appendNewPage() {
        int nextPageNo = (int) (file.length() / PAGE_SIZE);
        AvailablePage newAvailablePage = new AvailablePage(nextPageNo);

        int newPageFp = (int) (file.length() - 1);
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            // 尾节点处理
            tailPage.nextPage = nextPageNo;
            raf.seek((long) tail * PAGE_SIZE);
            raf.read(tailPage.getPageData());

            // 新节点处理 (不必真的写入)
            tail = newPageFp;
            tailPage = newAvailablePage;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nextPageNo;
    }

    @Override
    public synchronized void drop(int pageNo) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            // 尾节点处理
            tailPage.nextPage = pageNo;
            raf.seek((long) tail * PAGE_SIZE);
            raf.read(tailPage.getPageData());

            // 新节点处理 (不必真的写入)
            tail = pageNo;
            tailPage = new AvailablePage(pageNo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
