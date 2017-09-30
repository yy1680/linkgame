package com.jiaxin.mylink.Model;

import java.util.List;

/**
 * Created by jiaxin on 2016/11/29.
 * 连连看控制接口，负责游戏逻辑//及视图的交互
 */

public interface LinkGame {
    void createMap();

    int[][] getMap();

    boolean isValid(int x1,int y1);

    List judge(int x1, int y1, int x2, int y2);

    void removePair(int x1, int y1,int x2,int y2);

    boolean isFinished();

    void reArrange();

    int getRemain();
}
