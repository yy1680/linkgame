package com.jiaxin.mylink.Activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.jiaxin.mylink.Contant;
import com.jiaxin.mylink.Model.LinkGameImpl;
import com.jiaxin.mylink.Model.LinkGame;
import com.jiaxin.mylink.MusicPlayer;
import com.jiaxin.mylink.Presenter.IPresenter;
import com.jiaxin.mylink.R;
import com.jiaxin.mylink.View.IView;
import com.jiaxin.mylink.View.LinkPanel;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity implements IPresenter {
    public static final String TAG = "MainAct";
    //Model
    LinkGame game;
    //View
    IView view;
    @BindView(R.id.panel)
    LinkPanel panel;

    //计时器进度条
    @BindView(R.id.pb_time)
    RoundCornerProgressBar pb_time;
    @BindView(R.id.tv_time)
    TextView tv_time;
    Timer timer;
    int time;
    Handler handler = new LinkHandler(this);

    //游戏结果
    ImageView gameResult;

    //声音相关
    MusicPlayer mMusicPlayer;

    //重排画面及提示按钮
    @BindView(R.id.iv_refresh)
    ImageView refresh;
    @BindView(R.id.iv_tips)
    ImageView tips;

    //游戏状态
    public static final int PLAYING = 0;
    public static final int RESTORE = 1;
    private static int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //计时器
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
        mMusicPlayer.playBackGroundMusic();
    }

    @Override
    protected void onStop() {
        timer.cancel();
        timer.purge();
        mMusicPlayer.stopBackGroundMusic();
        super.onStop();
    }

    //初始化计时器，包括进度条和倒计时文本
    public void initialize() {
        //进度条
        tv_time.setText(getTimeToShow(time));
        tv_time.setVisibility(View.VISIBLE);
        time = Contant.LIMITTIME;
        pb_time.setMax(time);
        pb_time.setVisibility(View.VISIBLE);

        //功能按钮
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reArrange();
            }
        });
        tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tips();
            }
        });

        //初始化连连看游戏
        game = new LinkGameImpl(this);
        game.createMap();

        //初始化连连显示板
        view = panel;
        view.setPresenter(this);

        //声音
        mMusicPlayer = new MusicPlayer(this);
        state = PLAYING;
    }

    //把时间转换成--：--显示,单位秒
    private String getTimeToShow(int time) {
        int min = time / 60;
        int sec = time % 60;
        String mzero = min < 10 ? "0" : "";
        String szero = sec < 10 ? "0" : "";
        return mzero + min + ":" + szero + sec;
    }

    private void reStart() {
        game = new LinkGameImpl(this);
        time = Contant.LIMITTIME;
        tips.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.VISIBLE);
        initialize();
    }

    private void updateTime(int time) {
        pb_time.setProgress(time);
        tv_time.setText(getTimeToShow(time));
        if (time == 0) {
            onGameOver();
        }
    }

    //重排功能
    private void reArrange() {
        game.reArrange();
        view.drawMap();
    }

    //提示功能
    private void tips() {
        int[][] map = game.getMap();
        int row = map.length;
        int col = map[0].length;
        for (int x1 = 1; x1 < row - 1; x1++) {
            for (int y1 = 1; y1 < col - 1; y1++) {
                for (int x2 = 1; x2 < row - 1; x2++) {
                    for (int y2 = 1; y2 < col - 1; y2++) {
                        if (x1 == x2 && y1 == y2) {
                            continue;
                        }
                        List<int[]> result = game.judge(x1, y1, x2, y2);
                        if (result != null) {
                            removePair(x1, y1, x2, y2, result);
                            return;
                        }
                    }
                }
            }
        }
        reArrange();
    }

    @Override
    public void start() {
        initialize();
        view.drawMap();
    }

    @Override
    public int[][] getMap() {
        return game.getMap();
    }

    private int CHOSEN_STATE = 0;
    public static final int CHOSEN_FIRST = 1;
    public static final int CHOSEN_NONE = 0;
    @Override
    public void onChosen(int[] xy) {
        //先看点击的位置是否选中一个格子
        if (!game.isValid(xy[0], xy[1])) {
            return;
        }
        Log.i(TAG+"-onChosen","state:"+CHOSEN_STATE);
        switch (CHOSEN_STATE) {
            case CHOSEN_NONE:
                firstChoice(xy);
                CHOSEN_STATE = CHOSEN_FIRST;
                break;
            case CHOSEN_FIRST:
                secondChoice(xy);
                break;
        }
    }
    //X代表第几行
    //y代表第几列
    int row1 = -1;
    int col1 = -1;
    @Override
    public void firstChoice(int[] xy1) {
        col1 = xy1[0];
        row1 = xy1[1];
        Log.v(TAG+"-firstChoice","位置："+row1+"行"+col1+"列被选中  state:"+CHOSEN_STATE);
        view.onChosen(row1, col1);
    }
    @Override
    public void secondChoice(int[] xy2) {
        int row2 = xy2[1];
        int col2 = xy2[0];
        Log.v(TAG+"-secondChoice","位置："+row2+"行"+col2+"列被选中  state:"+CHOSEN_STATE);
        List result = game.judge(row1, col1, row2, col2);
        if (result != null && !result.isEmpty()) {
            removePair(row1, col1, row2, col2, result);
            row1 = -1;
            col1 = -1;
            CHOSEN_STATE = CHOSEN_NONE;
        }else{
            Log.v(TAG+"-secondChoice","连不上");
            firstChoice(xy2);
            CHOSEN_STATE = CHOSEN_FIRST;
        }
        view.drawMap();
    }

    @Override
    public void removePair(int row1, int col1, int row2, int col2, List result) {
        Log.v(TAG,"位置："+row1+"行"+col1+"列,"+row2+"行"+col2+"列被消去");
        view.removePair(result,game.getMap()[row1][col1]);
        game.removePair(row1, col1, row2, col2);
        checkStatus();
    }

    @Override
    public void checkStatus() {
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
        window.getDecorView().setPadding(0, 0, 0, 0);
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

    private static class LinkHandler extends Handler {
        WeakReference<MainActivity> activity;

        public LinkHandler(MainActivity activity) {
            this.activity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int time = msg.arg1;
            activity.get().updateTime(time);
        }
    }

    private Dialog mGameResultDialog;
    private void createDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View root = inflater.inflate(R.layout.dialog_gameresult, null);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(metrics.widthPixels, metrics.heightPixels);

        ImageView result = (ImageView) root.findViewById(R.id.iv_gameresult);
        result.setImageResource(game.isFinished() ?
                R.drawable.win : R.drawable.fail);
        Button btn_restart = (Button) root.findViewById(R.id.btn_restart);
        btn_restart.setOnClickListener(dialogListener);
        Button btn_nextround = (Button) root.findViewById(R.id.btn_nextround);
        btn_nextround.setOnClickListener(dialogListener);
        Button btn_exit = (Button) root.findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(dialogListener);
        mGameResultDialog = new Dialog(this, R.style.MyDialog);
        mGameResultDialog.setContentView(root, params);
    }

    private View.OnClickListener dialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mGameResultDialog.dismiss();
            switch (v.getId()) {
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