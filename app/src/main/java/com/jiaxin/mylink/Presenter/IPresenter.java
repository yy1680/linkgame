package com.jiaxin.mylink.Presenter;

import java.util.List;

/**
 * Created by jiaxin on 2017/9/13.
 */

public interface IPresenter {
    void start();

    int[][] getMap();

    void onChosen(int[] xy);

    void firstChoice(int[] xy);

    void secondChoice(int[] xy);

    void checkStatus();

    void onGameOver();

    void removePair(int row1,int col1,int row2,int col2,List result);

}
