package zzj.klenkiven.io.prototype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DefaultPage extends AbstractPage {

    public DefaultPage(int pageNo, byte[] data) {
        super(pageNo, data);
    }

    @Override
    protected void constructData(DataInputStream dis) {

    }

    @Override
    protected void writePageContent(DataOutputStream dos) throws IOException {
        dos.write(getOriginData(), PAGE_HEADER_SIZE, available);
    }
}
