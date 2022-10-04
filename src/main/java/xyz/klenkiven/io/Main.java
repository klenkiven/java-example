package xyz.klenkiven.io;

public class Main {
    public static void main(String[] args) {
        PaginatedFile paginatedFile = new DefaultPaginatedFile("test.dat");
        Page page = paginatedFile.getPage(0);
    }
}
