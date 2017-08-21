package com.jiaxin.mylink;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by jiaxin on 2017/1/4.
 */

public class Util {
    public static int[] PositionToPoints(int pos){
        int col = Contant.COL;
        int[] xy = new int[2];
        xy[0] = pos / col;
        xy[1] = pos % col;
        return xy;
    }
    public static int PointsToPosition(int x,int y){
        int col = Contant.COL;
        int pos = x * col + y;
        return pos;
    }
    private static Bitmap[] images;

    public static Bitmap getImage(Resources res,int num){
        if(images == null){
            int len = Contant.PICTURES.length;
            images = new Bitmap[len];
            for (int i = 0; i < len; i++) {
                images[i] = BitmapFactory.decodeResource(res,Contant.PICTURES[i]);
            }
        }
        return images[num];
    }
}
