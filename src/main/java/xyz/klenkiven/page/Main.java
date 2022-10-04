package xyz.klenkiven.page;

import java.util.Random;

public class Main {
    private static Random rand = new Random();

    public static void main(String[] args) {
        PaginatedFile paginatedFile = new PaginatedFile("test-01.dat");
        for (int i = 0; i < 102400; i++) {
            paginatedFile.writePage(new Page(i, genRandomData(4096)));
        }
    }

    public static byte[] genRandomData(int pageSize) {
        byte[] data = new byte[pageSize];
        rand.nextBytes(data);
        return data;
    }
}
