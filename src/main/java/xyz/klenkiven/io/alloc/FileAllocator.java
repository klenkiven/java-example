package xyz.klenkiven.io.alloc;

import xyz.klenkiven.io.AbstractPage;
import xyz.klenkiven.io.Page;
import xyz.klenkiven.io.PaginatedFile;

import java.io.*;

import static xyz.klenkiven.io.Page.INVALID_PAGE_NO;
import static xyz.klenkiven.io.Page.PAGE_SIZE;

public class FileAllocator implements Allocator {

    private static int FILE_ALLOC_PAGE = 0;

    static class FileAllocPage extends AbstractPage {
        int headAvailablePageNo = INVALID_PAGE_NO;
        int tailAvailablePageNo = INVALID_PAGE_NO;

        public FileAllocPage(int pageNo) {
            super(pageNo, new byte[0]);
        }

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

        /**
         * 初始化一个空白的可用节点
         * @param pageNo 可用节点页面ID
         */
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

        int availablePageNo = INVALID_PAGE_NO;

        // 每次首先检查首节点是是否和尾节点一致（减少一次I/O）
        if (head == tail && tail != INVALID_PAGE_NO) {
            head = INVALID_PAGE_NO; tail = INVALID_PAGE_NO;
            availablePageNo = tailPage.getPageNo();
            tailPage = null;
        }
        // 非首节点处理
        else {
            AvailablePage availablePage = new AvailablePage(paginatedFile.getPage(head));
            availablePageNo = availablePage.getPageNo();
            head = availablePage.nextPage;
        }

        return availablePageNo;
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
        // 添加到链表尾部
        add(nextPageNo);
        return nextPageNo;
    }

    @Override
    public void drop(int pageNo) {
        add(pageNo);
    }

    /**
     * 回收一个页面
     * @param pageNo 回收的页面ID
     */
    private synchronized void add(int pageNo) {
        // 如果末尾节点为非法节点，那么首节点也一定为非法
        if (tail == INVALID_PAGE_NO && tailPage == null) {
            tail = pageNo; head = tail;
            tailPage = new AvailablePage(pageNo);
        }
        // 处理尾节点非首节点的情况，涉及一次I/O操作
        else {
            tail = pageNo;
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                // 处理结结尾节点
                tailPage.nextPage = pageNo;
                raf.seek((long) tailPage.getPageNo() * PAGE_SIZE);
                raf.write(tailPage.getPageData(), 0, PAGE_SIZE);

                // 处理当前分配器的状态
                tail = pageNo; tailPage = new AvailablePage(pageNo);
                if (head == INVALID_PAGE_NO) head = tail;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 持久化分配器状态
     */
    @Override
    public synchronized void close() {
        FileAllocPage fileAllocPage = new FileAllocPage(FILE_ALLOC_PAGE);
        fileAllocPage.headAvailablePageNo = head;
        fileAllocPage.tailAvailablePageNo = tail;

        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            raf.seek(fileAllocPage.getPageNo());
            raf.write(fileAllocPage.getPageData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
