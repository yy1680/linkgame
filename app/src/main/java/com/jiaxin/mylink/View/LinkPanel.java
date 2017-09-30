package com.jiaxin.mylink.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.jiaxin.mylink.Contant;
import com.jiaxin.mylink.Presenter.IPresenter;
import com.jiaxin.mylink.Util;

import java.util.List;

/**
 * 连连看主体视图，继承自gridview，主要负责绘制连接线
 */

public class LinkPanel extends View implements IView{

    public static final int DRAW_LINES_AND_TARGET = 0;
    public static final int DRAW_MAP = 1;
    public static final int ONCHOSEN = 2;
    public static final int DEFAULT = 1;

    private int STATE = DRAW_MAP;

    private int height;
    private int width;

    private Bitmap[] bitmaps;
    private Paint paint = new Paint();

    private List<int[]> points;
    private IPresenter presenter;

    public LinkPanel(Context context) {
        super(context);
        loadConfig();
    }

    public LinkPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadConfig();
    }

    private void loadConfig() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        //读取屏幕大小，转换成图标大小
        int col = Integer.parseInt(preferences.getString("pref_col","0")) + 2;
        int row = Integer.parseInt(preferences.getString("pref_row","0")) + 2;
        int size = Math.min(preferences.getInt("height",0)/row,preferences.getInt("width",0)/col);
        height = size;
        width = size;
        //加载图标资源
        bitmaps = new Bitmap[Contant.PICTURES.length];
        for(int i = 0;i < bitmaps.length;i++){
            bitmaps[i] = BitmapFactory.decodeResource(getResources(), Contant.PICTURES[i]);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMap(canvas);
        switch (STATE) {
            case DRAW_LINES_AND_TARGET:
                drawLinesAndTarget(canvas);
                STATE = DEFAULT;
                invalidate();
                break;
            case ONCHOSEN:
                drawBitmap(canvas,which,chosenLeft,chosenTop,chosenRight,chosenBottom);
                drawBoundary(canvas,chosenLeft,chosenTop,chosenRight,chosenBottom);
                STATE = DEFAULT;
                break;
            case DRAW_MAP:
                break;
        }
    }

    private void drawBoundary(Canvas canvas, int chosenLeft, int chosenTop, int chosenRight, int chosenBottom) {
        RectF rectF = new RectF(chosenLeft,chosenTop,chosenRight,chosenBottom);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10f);
        canvas.drawRect(rectF,paint);
        paint.reset();
    }

    private void drawBitmap(Canvas canvas,int which,int left,int top,int right,int bottom){
        RectF dest = new RectF();
        dest.left = left;
        dest.top = top;
        dest.right = right;
        dest.bottom = bottom;
        canvas.drawBitmap(bitmaps[which],null,dest,paint);
    }

    private void drawLinesAndTarget(Canvas canvas) {
        int hafewidth = width / 2;
        int hafeheight = height / 2;
        paint.setStrokeWidth(10f);
        paint.setColor(Color.RED);
        for (int i = 0; i < points.size() - 1; i++) {
            int y1 = points.get(i)[0];
            int x1 = points.get(i)[1];
            int y2 = points.get(i + 1)[0];
            int x2 = points.get(i + 1)[1];
            canvas.drawLine(x1 * width + hafewidth, y1 * height + hafeheight,
                    x2 * width + hafewidth, y2 * height + hafeheight, paint);
        }
        int[] lastPoint = points.get(points.size()-1);
        calculateSizeAndTarget(lastPoint[0],lastPoint[1]);
        drawBitmap(canvas,targetPic,chosenLeft,chosenTop,chosenRight,chosenBottom);
        drawBoundary(canvas,chosenLeft,chosenTop,chosenRight,chosenBottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    int targetPic;
    @Override
    public void removePair(List points,int whichPic) {
        this.points = points;
        this.STATE = DRAW_LINES_AND_TARGET;
        this.targetPic = whichPic;
        invalidate();
    }

    @Override
    public void drawMap(){
        invalidate();
    }

    private void drawMap(Canvas canvas) {
        int[][] map = presenter.getMap();
        if(map == null){
            return;
        }
        for (int y = 0;y < map.length;y++){
            for(int x = 0; x < map[0].length;x++){
                if(map[y][x] < 0){
                    continue;
                }
                drawBitmap(canvas,map[y][x],x*width,y*height,(x+1)*width,(y+1)*height);
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action == MotionEvent.ACTION_UP){
            presenter.onChosen(Util.XYToIndex(width,height,event.getX(),event.getY()));
        }
        return true;
    }

    @Override
    public void setPresenter(IPresenter presenter) {
        this.presenter = presenter;
    }

    private int chosenLeft;
    private int chosenTop;
    private int chosenRight;
    private int chosenBottom;
    private int which;
    private int spacing = 10;

    @Override
    public void onChosen(int row, int col) {
        calculateSizeAndTarget(row,col);
        STATE = ONCHOSEN;
        invalidate();
    }

    private void calculateSizeAndTarget(int row,int col){
        chosenLeft = col * width - spacing;
        chosenTop = row * height - spacing;
        chosenRight = (col + 1) * width + spacing;
        chosenBottom = (row + 1) * height + spacing;
        which = presenter.getMap()[row][col];
    }
}
