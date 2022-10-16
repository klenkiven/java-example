package zzj.klenkiven.algorithom;

import java.util.*;

public class Dijkstra {
    private static final int UNREACHABLE = Integer.MAX_VALUE;

    public static void main(String[] args) {
        int[][] matrix = new int[][]{
                {0, 5, UNREACHABLE, UNREACHABLE, UNREACHABLE, UNREACHABLE},
                {UNREACHABLE, 0, 4, UNREACHABLE, UNREACHABLE, UNREACHABLE},
                {8, UNREACHABLE, 0, UNREACHABLE, UNREACHABLE, 9},
                {UNREACHABLE, UNREACHABLE, 5, 0, UNREACHABLE, 6},
                {UNREACHABLE, UNREACHABLE, UNREACHABLE, 5, 0, UNREACHABLE},
                {3, UNREACHABLE, UNREACHABLE, UNREACHABLE, 1, 0}
        };

        int[][] path = shortestPathFrom(matrix, 0);

        System.out.println("[minLength, prevNode]: " + Arrays.deepToString(path));
        System.out.print("Shortest Path from 0 to 5: ");

        int startNode = 0, endNode = 5;
        int[] last = path[endNode];
        System.out.print(endNode);
        for (int i = 0; last[1] != startNode; i++) {
            System.out.print(" <- ");
            System.out.print(last[1]);
            last = path[last[1]];
        }
        System.out.println(" <- " + startNode);
    }

    private static int[][] shortestPathFrom(int[][] matrix, int startNode) {
        int[][] path = new int[matrix.length][2];
        boolean[] visited = new boolean[matrix.length];

        // 第一次初始化
        for (int i = 0; i < matrix.length; i++) {
            path[i][0] = matrix[startNode][i];
            path[i][1] = startNode;
        }
        visited[startNode] = true;
        System.out.println("startNode=" + startNode + ", minPath=" + Arrays.deepToString(path));

        int remainNode = matrix.length - 1;
        int next = startNode;
        while (remainNode > 0) {
            // 寻找路径最短的下一个位置
            int minLength = UNREACHABLE;
            for (int i = 0; i < matrix.length; i++) {
                if (!visited[i] && path[i][0] != UNREACHABLE) {
                    next = i;
                    minLength = Math.min(minLength, path[i][0]);
                }
            }
            visited[next] = true; remainNode--;

            // 更新当前的最短路径信息
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[next][j] != UNREACHABLE) {
                    if (path[j][0] == UNREACHABLE || path[j][0] > path[next][0] + matrix[next][j]) {
                        path[j][0] = path[next][0] + matrix[next][j];
                        path[j][1] = next;
                    }
                }
            }
            System.out.println("next=" + next + ", minPath=" + Arrays.deepToString(path));
        }

        return path;
    }
}
