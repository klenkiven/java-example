package xyz.klenkiven.io;

import java.io.*;

/**
 * 页面的抽象实现
 */
public abstract class AbstractPage implements Page {
    /** 页面序号 */
    private final int pageNo;
    /** 判断是否为垃圾 */
    private byte isTrash = 1;
    /** 页面原始数据 */
    private byte[] data;
    /** 页面容量 */
    protected int capacity = PAGE_SIZE;

    public AbstractPage(int pageNo, byte[] data) {
        // 初始化数据 和 ID
        this.pageNo = pageNo;
        if (data == null || data.length == 0) return;
        this.data = data.clone();

        /* 处理数据内容 构建页面 */
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        try {
            // 处理页头的数据内容
            constructPageHeaderData(dis);
            // 处理页面数据
            constructData(dis);
            // 判断剩余容量
            capacity = dis.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理页面头部数据
     * @param dis 数据输入流
     */
    private void constructPageHeaderData(DataInputStream dis) throws IOException {
        this.isTrash = dis.readByte();
    }

    /**
     * 获取页面的数据
     * @param dis 页面数据输入流
     * @throws IOException IO异常
     */
    protected abstract void constructData(DataInputStream dis) throws IOException;

    @Override
    public boolean isTrash() {
        return isTrash == 1;
    }

    @Override
    public int getPageNo() {
        return pageNo;
    }

    @Override
    public byte[] getOriginData() {
        return data;
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