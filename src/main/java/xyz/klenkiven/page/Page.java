package xyz.klenkiven.page;

public class Page {
    private int pageNo;
    private byte[] data;

    public Page(int pageNo, byte[] data) {
        this.pageNo = pageNo;
        this.data = data.clone();
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
