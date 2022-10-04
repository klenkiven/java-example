package xyz.klenkiven.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DefaultPage extends AbstractPage {

    public DefaultPage(int pageNo, byte[] data) {
        super(pageNo, data);
    }

    @Override
    protected void constructData(DataInputStream dis) throws IOException {

    }

    @Override
    protected void writePageContent(DataOutputStream dos) throws IOException {

    }
}
