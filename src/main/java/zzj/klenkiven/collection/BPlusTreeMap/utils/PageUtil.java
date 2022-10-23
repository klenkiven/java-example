package zzj.klenkiven.collection.BPlusTreeMap.utils;

public class PageUtil {

    private static int num = 0;
    public static int getNewPageId() {
        return num++;
    }

}
