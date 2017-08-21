package com.jiaxin.mylink;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

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
    //背景板每一格代表一个ImageView
    private ImageView[] mImageViews;
    private LinkGame game;

    public void setLinkGame(LinkGame game){
        this.game = game;
    }

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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    public void initImageView() {
        //获得格子数目
        int count = row * col;
        mImageViews = new ImageView[count];
        //计算每一个格子的宽和高，初始化每一个ImageView
        int padding = getPaddingLeft();
        int width = (1080 - 2 * padding) / 10 - 10;
        int height = (1500 - 2 * padding) / 12 - 10;
        for (int i = 0; i < count; i++) {
            mImageViews[i] = new ImageView(getContext());
            mImageViews[i].setLayoutParams(new AbsListView.LayoutParams(width, height));
            mImageViews[i].setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }
    public ImageView getImageView(int num){
        return this.mImageViews[num];
    }
    public ImageView[] getImageViews(){
        return mImageViews;
    }

    public class MyAdapter extends BaseAdapter {
        Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return mImageViews == null ? 0 : mImageViews.length;
        }

        @Override
        public Object getItem(int position) {
            return mImageViews[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView view = mImageViews[position];
            int num = game.linkMap.get(position);
            if (num > -1) {
                view.setImageBitmap(Util.getImage(getResources(),num));
            } else {
                view.setVisibility(View.GONE);
            }
            return view;
        }
    }
}
