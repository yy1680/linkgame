package com.jiaxin.mylink;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity {

    //游戏背景板，继承GridView
    LinkPanel panel;

    //GridView适配器
    LinkPanel.MyAdapter myGridViewAdapter;

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

    LinkService myController;
    //Todo 重构
    Handler handler = new LinkHandler(this);

    //声音相关
    MusicPlayer mMusicPlayer;

    //重排画面及提示按钮
    ImageView refresh;
    ImageView tips;

//    Runnable delayLoadImage = new Runnable() {
//        @Override
//        public void run() {
//            while (width == 0) {
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                width = panel.getWidth();
//            }
//            Message msg = handler.obtainMessage();
//            msg.what = 1;
//            msg.sendToTarget();
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化连连看游戏
        initLinkGame();
        //读取上次中断的游戏状态
        resumeGame(savedInstanceState);
        //延迟加载连连看显示板
        //new Thread(delayLoadImage).start();
    }

    private void resumeGame(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            time = savedInstanceState.getInt("time");
//            width = savedInstanceState.getInt("width");
//            height = savedInstanceState.getInt("height");
            //// TODO: 2016/12/26 游戏状态map

        }
    }

    private void initLinkGame() {
        //初始化连连看游戏
        game = new LinkGame();
        myController = new LinkServiceImpl();

        //初始化连连显示板
        panel = (LinkPanel) findViewById(R.id.panel);
        panel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myController.doChose(position);
            }
        });

        //声音
        mMusicPlayer = new MusicPlayer(this);

        //功能按钮
        refresh = (ImageView) findViewById(R.id.iv_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rearrange();
            }
        });
        tips = (ImageView) findViewById(R.id.iv_tips);
        tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tips();
            }
        });
    }

    //初始化计时器，包括进度条和倒计时文本
    public void initTimer() {
        pb_time = (RoundCornerProgressBar) findViewById(R.id.pb_time);
        tv_time = (TextView) findViewById(R.id.tv_time);
        time = Contant.LIMITTIME;
        tv_time.setText(getTimeToShow(time));
        tv_time.setVisibility(View.VISIBLE);
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
//        outState.putInt("width", width);
//        outState.putInt("height", height);
        outState.putInt("time", time);
        // TODO: 游戏状态map
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //显示功能按钮图片
        refresh.setVisibility(View.VISIBLE);
        tips.setVisibility(View.VISIBLE);

        panel.setNumColumns(game.col);
        panel.setLinkGame(game);
        panel.initImageView();
        myGridViewAdapter = panel.new MyAdapter(MainActivity.this);
        panel.setHorizontalSpacing(10);
        panel.setVerticalSpacing(10);
        panel.setAdapter(myGridViewAdapter);
        myGridViewAdapter.notifyDataSetChanged();
        //开始游戏，初始化计时器
        initTimer();
        //放音乐
        mMusicPlayer.playBackGroundMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        mMusicPlayer.stopBackGroundMusic();
        super.onStop();
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
        tips.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.VISIBLE);
        initTimer();
        panel.setLinkGame(game);
        panel.initImageView();
        myGridViewAdapter.notifyDataSetChanged();
    }

    private void onPanelWidthGot() {

    }

    private void updateTime(int time) {
        pb_time.setProgress(time);
        tv_time.setText(getTimeToShow(time));
        if (time == 0) {
            myController.onGameOver();
        }
    }

    //重排当前格子
    private void rearrange() {
        game.linkMap.swap(game.map);
        for (ImageView view : panel.getImageViews()) {
            view.setVisibility(View.VISIBLE);
        }
        clearImageState();
        ((BaseAdapter) panel.getAdapter()).notifyDataSetChanged();
    }

    private void clearImageState() {
        int pos = ((LinkServiceImpl) myController).chosenPos;
        if (pos != -1) {
            myController.changeImageState(Contant.SELECTED, pos);
        }
    }

    //提示
    private void tips() {
        int row = game.row;
        int col = game.col;
        for(int x1 = 1;x1 < row - 1;x1++){
            for (int y1 = 1;y1 < col - 1;y1++){
                for(int x2 = 1;x2 < row - 1;x2++){
                    for(int y2 = 1;y2 < col - 1;y2++){
                        if(x1 == x2 && y1 == y2) {
                            continue;
                        }
                        List<int[]> result = game.judge(x1,y1,x2,y2);
                        if(result != null){
                            myController.dismissTwoUnit(result,
                                    Util.PointsToPosition(x1,y1),
                                    Util.PointsToPosition(x2,y2));
                            return;
                        }
                    }
                }
            }
        }
        rearrange();
    }

    private static class LinkHandler extends Handler {
        WeakReference<MainActivity> activity;

        public LinkHandler(MainActivity activity) {
            this.activity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //读取到画板实际大小后，根据大小初始化格子
                case 1:
                    activity.get().onPanelWidthGot();
                    break;
                //更新计时器状态
                default:
                    int time = msg.arg1;
                    activity.get().updateTime(time);
                    break;
            }
        }
    }


    class LinkServiceImpl implements LinkService {

        public int chosenPos = -1;
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.2f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.2f);
        PropertyValuesHolder rescaleX = PropertyValuesHolder.ofFloat("scaleX", 1.2f, 1f);
        PropertyValuesHolder rescaleY = PropertyValuesHolder.ofFloat("scaleY", 1.2f, 1f);
        ObjectAnimator animator;
        int chosenX = -1;
        int chosenY = -1;

        @Override
        public void changeImageState(int curState, int pos) {
            if (Contant.SELECTED == curState) {
                animator = ObjectAnimator.ofPropertyValuesHolder(
                        panel.getImageView(pos),rescaleX, rescaleY);
                animator.setDuration(0);
                animator.start();

            } else if (Contant.UNSELECTED == curState) {
                animator = ObjectAnimator.ofPropertyValuesHolder(
                        panel.getImageView(pos),scaleX, scaleY);
                animator.setDuration(0);
                animator.start();
            }
        }

        @Override
        public void doChose(int position) {
            if (chosenPos > -1) {
                changeImageState(Contant.SELECTED, chosenPos);
                int[] xy = game.linkMap.positionToXY(position);
                int curX = xy[0];
                int curY = xy[1];
                if (position != chosenPos) {
                    List<int[]> points = game.judge(chosenX, chosenY, curX, curY);
                    if (points != null) {
                        dismissTwoUnit(points,chosenPos, position);
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
        public void dismissTwoUnit(List<int[]> points,int pos1, int pos2) {
            //画连接线
            drawLinesByPoints(points);
            //放音效
            mMusicPlayer.playSoundEffect();
            //从游戏模型中删除两格
            int[] XY1 = Util.PositionToPoints(pos1);
            int[] XY2 = Util.PositionToPoints(pos2);
            game.remove(XY1[0], XY1[1], XY2[0], XY2[1]);
            //在视图层中删除两格
            panel.getImageView(pos1).setVisibility(View.GONE);
            panel.getImageView(pos2).setVisibility(View.GONE);
            chosenX = -1;
            chosenY = -1;
            chosenPos = -1;
            panel.postInvalidate();
            if (game.isFinished()) {
                onGameOver();
            }
        }

        @Override
        public void onGameOver() {
            createDialog();
            mGameResultDialog.show();

            //背景透明代码
            Window window = mGameResultDialog.getWindow();
            window.getDecorView().setPadding(0,0,0,0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.alpha = 0.8f;
            window.setAttributes(lp);

            //取消计时器
            timer.cancel();
            //让功能按钮、进度条、计时器消失
            tips.setVisibility(View.GONE);
            refresh.setVisibility(View.GONE);
            pb_time.setVisibility(View.GONE);
            tv_time.setVisibility(View.GONE);

        }
    }
    private Dialog mGameResultDialog;

    private void createDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View root = inflater.inflate(R.layout.dialog_gameresult,null);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(metrics.widthPixels,metrics.heightPixels);

        ImageView result = (ImageView) root.findViewById(R.id.iv_gameresult);
        result.setImageResource(game.isFinished()?
                R.drawable.win:R.drawable.fail);
        Button btn_restart = (Button) root.findViewById(R.id.btn_restart);
        btn_restart.setOnClickListener(dialogListener);
        Button btn_nextround = (Button) root.findViewById(R.id.btn_nextround);
        btn_nextround.setOnClickListener(dialogListener);
        Button btn_exit = (Button) root.findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(dialogListener);
        mGameResultDialog = new Dialog(this,R.style.MyDialog);
        mGameResultDialog.setContentView(root,params);

    }
    private View.OnClickListener dialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mGameResultDialog.dismiss();
            switch (v.getId()){
                case R.id.btn_restart:
                    reStart();
                    break;
                case R.id.btn_nextround:
                    reStart();
                    break;
                case R.id.btn_exit:
                    finish();
                    break;
            }
        }
    };

}
