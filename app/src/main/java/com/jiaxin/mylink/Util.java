package com.jiaxin.mylink;

/**
 * Created by jiaxin on 2017/1/4.
 */

public class Util {
    //X是横坐标，代表多少列
    //y是纵坐标，代表多少行
    public static int[] XYToIndex(int width,int height,float x, float y){
        int[] xy = new int[2];
        xy[0] = (int)(x/width);
        xy[1] = (int)(y/height);
        return xy;
    }

}
