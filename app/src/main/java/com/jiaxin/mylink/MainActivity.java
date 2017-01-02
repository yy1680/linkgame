package com.jiaxin.mylink;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    //游戏背景板，继承GridView
    LinkPanel panel;
    //背景板每一格代表一个ImageView
    ImageView[] mImageViews;
    //GridView适配器
    MyAdapter myGridViewAdapter;

    //游戏中使用的图片资源
    Bitmap[] unSelectedPics;
    Bitmap[] mSelectedPics;

    //游戏主要逻辑
    LinkGame game;

    //计时器进度条
    RoundCornerProgressBar pb_time;
    TextView tv_time;
    Timer timer;
    int time;

    //游戏结果
    ImageView gameResult;

    //保存格子的宽和高
    int width = 0;
    int height = 0;
    LinkService myController;
    //Todo 重构
    Handler handler = new Handler() {
        WeakReference<Activity> activity;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //读取到画板实际大小后，根据大小初始化格子
                case 1:
                    initImageView();
                    panel.setNumColumns(game.col);
                    myGridViewAdapter = new MyAdapter(MainActivity.this);
                    panel.setHorizontalSpacing(10);
                    panel.setVerticalSpacing(10);
                    panel.setAdapter(myGridViewAdapter);
                    myGridViewAdapter.notifyDataSetChanged();
                    //开始游戏，初始化计时器
                    initTimer();
                    break;
                //更新计时器状态
                default:
                    int time = msg.arg1;
                    pb_time.setProgress(time);
                    tv_time.setText(getTimeToShow(time));
                    if (time == 0) {
                        myController.onGameOver();
                    }
                    break;
            }
        }
    };
    Runnable delayLoadImage = new Runnable() {
        @Override
        public void run() {
            while (width == 0) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                width = panel.getWidth();
            }
            Message msg = handler.obtainMessage();
            msg.what = 1;
            msg.sendToTarget();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化连连看游戏
        initLinkGame();
        //读取上次中断的游戏状态
        resumeGame(savedInstanceState);
        //延迟加载连连看显示板
        new Thread(delayLoadImage).start();
    }

    private void resumeGame(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            time = savedInstanceState.getInt("time");
            width = savedInstanceState.getInt("width");
            height = savedInstanceState.getInt("height");
            //// TODO: 2016/12/26 游戏状态map
        }
    }

    private void initLinkGame() {
        //初始化连连看游戏
        game = new LinkGame();
        myController = new LinkServiceImpl();

        //读取连连格子图片
        int lenth = Contant.PICTURES.length;
        unSelectedPics = new Bitmap[lenth];
        for (int i = 0; i < lenth; i++) {
            unSelectedPics[i] = BitmapFactory.decodeResource(getResources(),
                    Contant.PICTURES[i]);
        }
        mSelectedPics = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.blue_selected),
                BitmapFactory.decodeResource(getResources(), R.drawable.red_selected),
                BitmapFactory.decodeResource(getResources(), R.drawable.green_selected),
                BitmapFactory.decodeResource(getResources(), R.drawable.yellow_selected)
        };

        //初始化连连显示板
        panel = (LinkPanel) findViewById(R.id.panel);
        panel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myController.doChose(position);
            }
        });

    }

    private void initImageView() {
        //获得格子数目
        int count = game.row * game.col;
        mImageViews = new ImageView[count];
        //计算每一个格子的宽和高，初始化每一个ImageView
        int padding = panel.getPaddingLeft();
        width = (panel.getWidth() - 2 * padding) / 10 - 10;
        height = (panel.getHeight() - 2 * padding) / 12 - 10;
        for (int i = 0; i < count; i++) {
            mImageViews[i] = new ImageView(this);
            mImageViews[i].setLayoutParams(new AbsListView.LayoutParams(width, height));
            mImageViews[i].setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    //初始化计时器，包括进度条和倒计时文本
    public void initTimer() {
        pb_time = (RoundCornerProgressBar) findViewById(R.id.pb_time);
        tv_time = (TextView) findViewById(R.id.tv_time);
        time = Contant.LIMITTIME;
        tv_time.setText(getTimeToShow(time));
        pb_time.setMax(time);
        pb_time.setVisibility(View.VISIBLE);
        timer = new Timer();
        //每一秒更新计时器状态
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                msg.arg1 = time;
                time--;
                handler.sendMessage(msg);
            }
        }, 0, 1000);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("width", width);
        outState.putInt("height", height);
        outState.putInt("time", time);
        // TODO: 2016/12/26 游戏状态map
        super.onSaveInstanceState(outState);
    }

    public void gameResultZoomIn() {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0f, 1f);
        ObjectAnimator in = ObjectAnimator.ofPropertyValuesHolder(gameResult, scaleX, scaleY);
        in.setDuration(2000);
        in.start();
    }

    public void gameResultZoomOut() {
        PropertyValuesHolder rescaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 0f);
        PropertyValuesHolder rescaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 0f);
        ObjectAnimator out = ObjectAnimator.ofPropertyValuesHolder(gameResult, rescaleX, rescaleY);
        out.setDuration(2000);
        out.start();
    }

    /*
    * 把时间转换成--：--显示,单位秒
    */
    private String getTimeToShow(int time) {
        int min = time / 60;
        int sec = time % 60;
        String mzero = min < 10 ? "0" : "";
        String szero = sec < 10 ? "0" : "";
        return mzero + min + ":" + szero + sec;
    }

    private void reStart() {
        game = new LinkGame();
        time = Contant.LIMITTIME;
        pb_time.setVisibility(View.VISIBLE);
        tv_time.setVisibility(View.VISIBLE);
        myGridViewAdapter.notifyDataSetChanged();
    }

    class MyAdapter extends BaseAdapter {
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
                view.setImageBitmap(unSelectedPics[num]);
            } else {
                view.setVisibility(View.GONE);
            }
            return view;
        }
    }

    class LinkServiceImpl implements LinkService {

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.2f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.2f);
        PropertyValuesHolder rescaleX = PropertyValuesHolder.ofFloat("scaleX", 1.2f, 1f);
        PropertyValuesHolder rescaleY = PropertyValuesHolder.ofFloat("scaleY", 1.2f, 1f);
        ObjectAnimator animator;
        int chosenX = -1;
        int chosenY = -1;
        int chosenPos = -1;

        @Override
        public void changeImageState(int curState, int pos) {
            int num = game.linkMap.get(pos);
            if (Contant.SELECTED == curState) {
                animator = ObjectAnimator.ofPropertyValuesHolder(mImageViews[pos],
                        rescaleX, rescaleY);
                animator.setDuration(30);
                animator.start();

            } else if (Contant.UNSELECTED == curState) {
                animator = ObjectAnimator.ofPropertyValuesHolder(mImageViews[pos],
                        scaleX, scaleY);
                animator.setDuration(30);
                animator.start();
            }

        }

        @Override
        public void doChose(int position) {
            if (chosenPos > -1) {
                int[] xy = game.linkMap.positionToXY(position);
                int curX = xy[0];
                int curY = xy[1];
                if (position != chosenPos &&
                        game.linkMap.get(position) == game.linkMap.get(chosenPos)) {
                    List<int[]> points = game.judge(chosenX, chosenY, curX, curY);
                    if (points != null) {
                        drawLinesByPoints(points);
                        game.remove(chosenX, chosenY, curX, curY);
                        remove(chosenPos, position);
                        panel.postInvalidate();
                        if (game.isFinished()) {
                            onGameOver();
                        }
                    } else {
                        unChose();
                    }
                } else {
                    unChose();
                }
            } else {
                int[] xy = game.linkMap.positionToXY(position);
                chosenX = xy[0];
                chosenY = xy[1];
                chosenPos = position;

                changeImageState(Contant.UNSELECTED, position);
            }
        }

        void unChose() {
            changeImageState(Contant.SELECTED, chosenPos);
            chosenX = -1;
            chosenY = -1;
            chosenPos = -1;
        }

        @Override
        public void drawLinesByPoints(List<int[]> points) {
            panel.setPoints(points);
            panel.setState(LinkPanel.POINTS_TO_DRAW);
            panel.invalidate();
        }

        @Override
        public void remove(int pos1, int pos2) {
            mImageViews[pos1].setVisibility(View.GONE);
            mImageViews[pos2].setVisibility(View.GONE);
            chosenX = -1;
            chosenY = -1;
            chosenPos = -1;
        }

        @Override
        public void onGameOver() {
            gameResult = (ImageView) findViewById(R.id.iv_gameresult);
            //显示结果图片
            if (game.isFinished()) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) gameResult.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                gameResult.setImageBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.win));
            } else {
                gameResult.setImageBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.fail));
            }
            //播放图片动画
            gameResultZoomIn();
            gameResult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reStart();
                }
            });
            //取消计时器
            timer.cancel();
            //让进度条计时器消失
            pb_time.setVisibility(View.GONE);
            tv_time.setVisibility(View.GONE);
        }
    }
}
