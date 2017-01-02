package com.jiaxin.mylink;

import java.util.List;

/**
 * Created by jiaxin on 2016/11/29.
 * 连连看控制接口，负责游戏逻辑及视图的交互
 */

public interface LinkService {
    public abstract void changeImageState(int currentState, int pos);

    public abstract void doChose(int pos);

    public abstract void drawLinesByPoints(List<int[]> points);

    public abstract void remove(int pos1, int pos2);

    public abstract void onGameOver();
}
