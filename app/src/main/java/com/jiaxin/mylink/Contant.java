package com.jiaxin.mylink;

/**
 * Created by jiaxin on 2016/11/29.
 */

public class Contant {
    /*
    * 每个格子按下的状态
    * */
    public static final int SELECTED = 1;
    public static final int UNSELECTED = 2;
    /*
    * 限制游戏时间，单位秒
    * */
    public static final int LIMITTIME = 600;
    /*
    * 配置连连看行、列数,实际显示为行列数各自减去2
    * */
    public static final int ROW = 12;
    public static final int COL = 10;
    /*
    * 设置同种图片的数目除以2，即有多少配对，越小难度越大
    * */
    public static final int DEFAULT_PAIRS = 4;
    /*
    * 游戏小格子图片
    * */
    public static final int[] PICTURES = new int[]{
            R.drawable.p1,
            R.drawable.p2,
            R.drawable.p3,
            R.drawable.p4,
            R.drawable.p5,
            R.drawable.p6,
            R.drawable.p7,
            R.drawable.p8,
            R.drawable.p9,
            R.drawable.p10,
//            R.drawable.p11,
//            R.drawable.p12,
//            R.drawable.p13,
    };
}
