package com.jiaxin.mylink;

import java.util.Random;

/**
 * Created by jiaxin on 2016/10/17.
 * 连连看游戏基本数据模型，是一个二维整形数组，正数表示图形编号，-1表示被消去的格子，-2表示空格子
 */

public class LinkMap {
    int[][] map;
    int totalNum;
    int row;
    int col;
    int pairs;

    public int[][] init(int r, int c, int p) {
        row = r;
        col = c;
        map = new int[row][col];
        totalNum = row * col;
        pairs = p;
        int tem = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (i == 0 || i == row - 1 || j == 0 || j == col - 1) {
                    map[i][j] = -2;
                    continue;
                }
                map[i][j] = tem++ / (2 * p);
            }
        }
        map = swap(map);
        return map;
    }

    public int[][] swap(int[][] src) {
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 2 * totalNum; i++) {
            int firstUnit = i >= totalNum ? i - totalNum : i;
            int secUnit = random.nextInt(totalNum);
            if (firstUnit != secUnit) {
                int[] xy1 = positionToXY(firstUnit);
                int[] xy2 = positionToXY(secUnit);
                int x1 = xy1[0];
                int y1 = xy1[1];
                int x2 = xy2[0];
                int y2 = xy2[1];
                if (src[x1][y1] == -2 || src[x2][y2] == -2) {
                    continue;
                }
                int tem = src[x1][y1];
                src[x1][y1] = src[x2][y2];
                src[x2][y2] = tem;
            }
        }
        return src;
    }

    public int get(int pos) {
        int xy[] = positionToXY(pos);
        int x = xy[0];
        int y = xy[1];
        return map[x][y];
    }


    public int[] positionToXY(int pos) {
        int[] xy = new int[2];
        xy[0] = pos / col;
        xy[1] = pos % col;
        return xy;
    }

}
