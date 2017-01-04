package com.jiaxin.mylink;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiaxin on 2016/10/17.
 * 连连游戏逻辑，包括消去逻辑，胜利条件检查
 * x代表第几行，y代表第几列
 */

public class LinkGame {
    public final int pairs = Contant.DEFAULT_PAIRS;
    public final int row = Contant.ROW;
    public final int col = Contant.COL;
    LinkMap linkMap;
    int[][] map;
    int left;

    public LinkGame(){
        linkMap = new LinkMap();
        linkMap.init(row,col, pairs);
        left = (row - 2) * (col - 2);
        map = linkMap.map;
    }

    public boolean isFinished(){
        return left == 0;
    }

    //判断格子是否为空
    public boolean isBlocked(int x, int y){
        return map[x][y] > -1;
    }

    //水平检测用来判断两个点的纵坐标是否相等，同时判断两点间有没有障碍物。
    public List<int[]> horison(int x1, int y1, int x2, int y2){
        List<int[]> result = new ArrayList<>();
        if(x1 != x2){
            return null;
        }
        if(y1 == y2){
            return null;
        }
        for (int i = Math.min(y1,y2) + 1; i < Math.max(y1,y2); i++) {
            if(isBlocked(x1,i)){
                return null;
            }
        }
        result.add(new int[]{x1,y1});
        result.add(new int[]{x2,y2});
        return result;
    }

    //垂直检测用来判断两个点的横坐标是否相等，同时判断两点间有没有障碍物。
    public List<int[]> vertical(int x1, int y1, int x2, int y2){
        List<int[]> result = new ArrayList<>();
        if(y1 != y2){
            return null;
        }
        if(x1 == x2){
            return null;
        }
        for (int i = Math.min(x1,x2) + 1; i < Math.max(x1,x2); i++) {
            if(isBlocked(i,y1)){
                return null;
            }
        }
        result.add(new int[]{x1,y1});
        result.add(new int[]{x2,y2});;
        return result;
    }

    //一个拐角检测可分解为水平检测和垂直检测，当两个同时满足时，便两点可通过一个拐角相连。即：
    //一个拐角检测 = 水平检测 && 垂直检测
    public List<int[]> turn_once(int x1, int y1, int x2, int y2){
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        List<int[]> result = new ArrayList<>();
        if(x1 < x2 && y1 < y2 || x1 > x2 && y1 > y2){
            x3 = Math.min(x1,x2);
            y3 = Math.max(y1,y2);
            x4 = Math.max(x1,x2);
            y4 = Math.min(y1,y2);
            List<int[]> ver_1_4 = vertical(x1,y1,x4,y4);
            List<int[]> hor_4_2 = horison(x4,y4,x2,y2);
            List<int[]> hor_1_3 = horison(x1,y1,x3,y3);
            List<int[]> ver_3_2 = vertical(x3,y3,x2,y2);
            //左上右下:p13
            //-------:42
            if(x1 < x2){
                if(ver_1_4 != null && hor_4_2 != null){
                    if(!isBlocked(x4,y4)){
                        result.addAll(ver_1_4);
                        result.add(hor_4_2.get(1));
                        return result;
                    }
                }
                if(hor_1_3 != null && ver_3_2 != null){
                    if(!isBlocked(x3,y3)){
                        result.addAll(hor_1_3);
                        result.add(ver_3_2.get(1));
                        return result;
                    }
                }
            }
            //右下左上:23
            //-------:41
            else if(x1 > x2){
                List<int[]> hor_1_4 = horison(x1,y1,x4,y4);
                List<int[]> ver_4_2 = vertical(x4,y4,x2,y2);
                List<int[]> ver_1_3 = vertical(x1,y1,x3,y3);
                List<int[]> hor_3_2 = horison(x3,y3,x2,y2);
                if(hor_1_4 != null && ver_4_2 != null){
                    if(!isBlocked(x4,y4)){
                        result.addAll(hor_1_4);
                        result.add(ver_4_2.get(1));
                        return result;
                    }
                }
                if(ver_1_3 != null && hor_3_2 != null){
                    if(!isBlocked(x3,y3)){
                        result.addAll(ver_1_3);
                        result.add(hor_3_2.get(1));
                        return result;
                    }
                }
            }
        }

        else if(x1 > x2 && y1 < y2 || x1 < x2 && y1 > y2){
            x3 = Math.min(x1,x2);
            y3 = Math.min(y1,y2);
            x4 = Math.max(x1,x2);
            y4 = Math.max(y1,y2);
            //右上左下:31
            //-------:24
            if(x1 < x2){
                List<int[]> hor_1_3 = horison(x1,y1,x3,y3);
                List<int[]> ver_3_2 = vertical(x3,y3,x2,y2);
                List<int[]> ver_1_4 = vertical(x1,y1,x4,y4);
                List<int[]> hor_4_2 = horison(x4,y4,x2,y2);
                if(hor_1_3 != null && ver_3_2 != null){
                    if(!isBlocked(x3,y3)){
                        result.addAll(hor_1_3);
                        result.add(ver_3_2.get(1));
                        return result;
                    }
                }
                if(ver_1_4 != null && hor_4_2 != null){
                    if(!isBlocked(x4,y4)){
                        result.addAll(ver_1_4);
                        result.add(hor_4_2.get(1));
                        return result;
                    }
                }
            }
            //左下右上:32
            //-------:14
            else if(x1 > x2){
                List<int[]> hor_1_4 = horison(x1,y1,x4,y4);
                List<int[]> ver_4_2 = vertical(x4,y4,x2,y2);
                List<int[]> ver_1_3 = vertical(x1,y1,x3,y3);
                List<int[]> hor_3_2 = horison(x3,y3,x2,y2);
                if(hor_1_4 != null && ver_4_2 != null){
                    if(!isBlocked(x4,y4)){
                        result.addAll(hor_1_4);
                        result.add(ver_4_2.get(1));
                        return result;
                    }
                }
                if(ver_1_3 != null && hor_3_2 != null){
                    if(!isBlocked(x3,y3)){
                        result.addAll(ver_1_3);
                        result.add(hor_3_2.get(1));
                        return result;
                    }
                }
            }

        }

        return null;
    }

    //两个拐角检测可分解为一个拐角检测和水平检测或垂直检测。即：
    //两个拐角检测 = 一个拐角检测 && (水平检测 || 垂直检测)
    public List<int[]> turn_twice(int x1, int y1, int x2, int y2){
        List<int[]> result = new ArrayList<>();
        for(int x = 0;x < row;x++){
            for (int y = 0;y < col;y++){
                if (x != x1 && x != x2 && y != y1 && y != y2){
                    continue;
                }
                if ((x == x1 && y == y1) || (x == x2 && y == y2)){
                    continue;
                }
                if (isBlocked(x, y)){
                    continue;
                }
                List<int[]> Point1toXY = turn_once(x1,y1,x,y);
                List<int[]> XYtoPoint2 = horison(x,y,x2,y2);
                XYtoPoint2 = XYtoPoint2 == null ? null : vertical(x,y,x2,y2);
                if(Point1toXY != null && XYtoPoint2 != null){
                    result.addAll(Point1toXY);
                    result.add(XYtoPoint2.get(1));
                    return result;
                }
                Point1toXY = horison(x1,y1,x,y) == null ?
                        vertical(x1,y1,x,y) : horison(x1,y1,x,y);
                XYtoPoint2 = turn_once(x,y,x2,y2);
                if(Point1toXY != null && XYtoPoint2 != null){
                    result.add(Point1toXY.get(0));
                    result.addAll(XYtoPoint2);
                    return result;
                }
            }
        }
        return null;
    }

    public void remove(int x1, int y1,int x2,int y2){
        linkMap.map[x1][y1] = -1;
        linkMap.map[x2][y2] = -1;
        left -= 2;
    }

    public List<int[]> judge(int x1, int y1, int x2, int y2){
        List<int[]> result;
        result = vertical(x1,y1,x2,y2);
        if (result != null){
            return result;
        }
        result = horison(x1,y1,x2,y2);
        if (result != null){
            return result;
        }
        result = turn_once(x1,y1,x2,y2);
        if (result != null){
            return result;
        }
        result = turn_twice(x1,y1,x2,y2);
        if (result != null){
            return result;
        }
        return null;
    }

}
