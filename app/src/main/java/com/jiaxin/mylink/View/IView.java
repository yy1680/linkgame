package com.jiaxin.mylink.View;

import com.jiaxin.mylink.Presenter.IPresenter;

import java.util.List;

/**
 * Created by jiaxin on 2017/8/30.
 */

public interface IView {
    void removePair(List points,int whichPic);

    void drawMap();

    void setPresenter(IPresenter presenter);

    void onChosen(int row,int col);
}
