package com.jiaxin.mylink;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by jiaxin on 2017/2/15.
 */

public class LinkServiceImpl implements LinkService{

    public int chosenPos = -1;
    PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.2f);
    PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.2f);
    PropertyValuesHolder rescaleX = PropertyValuesHolder.ofFloat("scaleX", 1.2f, 1f);
    PropertyValuesHolder rescaleY = PropertyValuesHolder.ofFloat("scaleY", 1.2f, 1f);
    ObjectAnimator animator;
    int chosenX = -1;
    int chosenY = -1;
    private LinkGame game;
    private ImageView[] mImageViews;
    private LinkPanel mPanel;
    private ImageView mGameResult;
    private Context mContext;

    public LinkServiceImpl(Context context, LinkGame game, ImageView[] imageViews, LinkPanel mPanel, ImageView mGameResult){
        this.game = game;
        this.mImageViews = imageViews;
        this.mPanel = mPanel;
        this.mGameResult = mGameResult;
        this.mContext = context;
    }

    @Override
    public void changeImageState(int curState, int pos) {
        if (Contant.SELECTED == curState) {
            animator = ObjectAnimator.ofPropertyValuesHolder(mImageViews[pos],
                    rescaleX, rescaleY);
            animator.setDuration(0);
            animator.start();

        } else if (Contant.UNSELECTED == curState) {
            animator = ObjectAnimator.ofPropertyValuesHolder(mImageViews[pos],
                    scaleX, scaleY);
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
        mPanel.setPoints(points);
        mPanel.setState(LinkPanel.POINTS_TO_DRAW);
        mPanel.invalidate();
    }

    @Override
    public void dismissTwoUnit(List<int[]> points,int pos1, int pos2) {
        //画连接线
        drawLinesByPoints(points);
        //放音效
        //mMusicPlayer.playSoundEffect();
        //从游戏模型中删除两格
        int[] XY1 = Util.PositionToPoints(pos1);
        int[] XY2 = Util.PositionToPoints(pos2);
        game.remove(XY1[0], XY1[1], XY2[0], XY2[1]);
        //在视图层中删除两格
        mImageViews[pos1].setVisibility(View.GONE);
        mImageViews[pos2].setVisibility(View.GONE);
        chosenX = -1;
        chosenY = -1;
        chosenPos = -1;
        mPanel.postInvalidate();
        if (game.isFinished()) {
            onGameOver();
        }
    }

    @Override
    public void onGameOver() {
        //显示结果图片
        if (game.isFinished()) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mGameResult.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mGameResult.setImageBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.drawable.win));
        } else {
            mGameResult.setImageBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fail));
        }
        //播放图片动画
        //gameResultZoomIn();
//            gameResult.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    reStart();
//                }
//            });
/*        createDialog();
        mGameResultDialog.show();
        //取消计时器
        timer.cancel();
        //让进度条计时器消失
        pb_time.setVisibility(View.GONE);
        tv_time.setVisibility(View.GONE);
        //关掉音乐
        mMusicPlayer.stopBackGroundMusic();*/
    }
}
