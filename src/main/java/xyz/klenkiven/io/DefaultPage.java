package xyz.klenkiven.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static xyz.klenkiven.io.Page.PAGE_SIZE;

public class DefaultPage extends AbstractPage {

    public DefaultPage(int pageNo, byte[] data) {
        super(pageNo, data);
    }

    @Override
    protected void constructData(DataInputStream dis) {

    }

    @Override
    protected void writePageContent(DataOutputStream dos) throws IOException {
        dos.write(getOriginData(), 0, PAGE_SIZE);
    }
}
