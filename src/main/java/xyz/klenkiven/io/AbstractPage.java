package xyz.klenkiven.io;

import java.io.*;

/**
 * 页面的抽象实现
 */
public abstract class AbstractPage implements Page {
    private final int pageNo;
    protected byte[] oldData;

    public AbstractPage() { this(null); }

    public AbstractPage(Page page) {
        if (page != null) {
            this.pageNo = page.getPageNo();
            this.oldData = page.getOriginData().clone();

            // 构建页面数据
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(oldData));
            try {
                constructData(dis);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.pageNo = INVALID_PAGE_NO;
            this.oldData = new byte[0];
        }
    }

    public AbstractPage(int pageNo, byte[] data) {
        this.pageNo = pageNo;
        this.oldData = data.clone();
    }

    /**
     * 获取页面的数据
     * @param dis 页面数据输入流
     * @throws IOException IO异常
     */
    protected abstract void constructData(DataInputStream dis) throws IOException;

    @Override
    public int getPageNo() {
        return pageNo;
    }

    @Override
    public byte[] getOriginData() {
        return oldData;
    }

    @Override
    public byte[] getPageData() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(PAGE_SIZE);
        DataOutputStream dos = new DataOutputStream(baos);

        // 写入页面相关数据
        try {
            writePageContent(dos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 页面对齐
        int paddingLen = PAGE_SIZE - baos.size();
        byte[] zeroes = new byte[paddingLen];
        try {
            dos.write(zeroes, 0, paddingLen);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    /**
     * 获取当前页面数据
     * @param dos 页面数据流
     */
    protected abstract void writePageContent(DataOutputStream dos) throws IOException;
}