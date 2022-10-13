package zzj.klenkiven.collection.sort;

import java.util.Arrays;

public class MergeSort {
    public static void main(String[] args) {
        int[] arr = new int[]{1,2,43,12,75,23,6,1,2,8,0,32,12,9,76,321,3,5};
        int[] arr2 = new int[]{1,2,43,12,75,23,6,1,2,8,0,32,12,9,76,321,3,5};
        Arrays.sort(arr2);
        mergeSort(arr);
        System.out.println("QuickSort: " + Arrays.toString(arr2));
        System.out.println("MergeSort: " + Arrays.toString(arr));
    }

    private static void mergeSort(int[] arr) {
        int[] aux = new int[arr.length];
        System.arraycopy(arr, 0, aux, 0, arr.length);
        mergeSort(aux, arr, 0, arr.length-1);
        System.out.println("MERGE SRC: " + Arrays.toString(arr));
        System.out.println("MERGE DEST: " + Arrays.toString(aux));
    }

    /**
     * 归并排序 -- 主体
     * @param dest 目标数组
     * @param src 源数组
     * @param left 左指针
     * @param right 右指针
     */
    private static void mergeSort(int[] src, int[] dest, int left, int right) {
        int length = right - left + 1;
        if (length < 7) {
            insertionSort(dest, left, right);
            return;
        }
        if (left >= right) return;

        int mid = (right - left) / 2 + left;

        mergeSort(dest, src, left, mid);
        mergeSort(dest, src, mid+1, right);
        merge(src, dest, left, right, mid);
    }

    /**
     * 合并有序的两部分
     */
    private static void merge(int[] src, int[] dest, int left, int right, int mid) {
        // System.out.println("Merge SRC: left=" + left + ", right=" + right + ", sortedPart=" + Arrays.toString(src));
        int index = left;
        int i = left; int j = mid + 1;
        while (i <= mid && j <= right) {
            if (src[i] <= src[j])
                dest[index++] = src[i++];
            else
                dest[index++] = src[j++];
        }

        if (i > mid) while (j <= right) dest[index++] = src[j++];
        else while (i <= mid) dest[index++] = src[i++];

        // ystem.out.println("Merge: left=" + left + ", right=" + right + ", sortedPart=" + Arrays.toString(dest));
    }

    private static void insertionSort(int[] dest, int left, int right) {
        for (int i = left; i <= right; i++) {
            for (int j = i; j > left && dest[j] < dest[j - 1]; j--) {
                swap(dest, j, j - 1);
            }
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

}
