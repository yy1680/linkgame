package com.jiaxin.mylink;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import java.util.List;

/**
 * Created by jiaxin on 2016/12/8.
 * 连连看主体视图，继承自gridview，主要负责绘制连接线
 */

public class LinkPanel extends GridView {

    public static final int POINTS_TO_DRAW = 0;
    public static final int NOTHING_TO_DRAW = 1;
    private final int col = Contant.COL;
    private final int row = Contant.ROW;
    private List<int[]> points;
    private int STATE = NOTHING_TO_DRAW;

    public LinkPanel(Context context) {
        super(context);
    }

    public LinkPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinkPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        switch (STATE) {
            case POINTS_TO_DRAW:
                drawLines(canvas);
                //下次重绘不画直线
                STATE = NOTHING_TO_DRAW;
                break;
            case NOTHING_TO_DRAW:
                break;
        }
    }

    public void setPoints(List<int[]> points) {
        this.points = points;
    }

    public void setState(int state) {
        this.STATE = state;
    }

    private void drawLines(Canvas canvas) {
        View view = getChildAt(0);
        int hafewidth = view.getWidth() / 2;
        int hafeheight = view.getHeight() / 2;
        Paint paint = new Paint();
        paint.setStrokeWidth(10f);
        paint.setColor(Color.RED);
        for (int i = 0; i < points.size() - 1; i++) {
            int x1 = points.get(i)[0];
            int y1 = points.get(i)[1];
            int x2 = points.get(i + 1)[0];
            int y2 = points.get(i + 1)[1];
            View view1 = getChildAt(x1 * col + y1);
            View view2 = getChildAt(x2 * col + y2);
            canvas.drawLine(view1.getX() + hafewidth, view1.getY() + hafeheight,
                    view2.getX() + hafewidth, view2.getY() + hafeheight, paint);
        }

    }
}
