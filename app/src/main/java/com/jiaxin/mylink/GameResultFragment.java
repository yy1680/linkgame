package com.jiaxin.mylink;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by jiaxin on 2017/1/6.
 */

public class GameResultFragment extends DialogFragment implements View.OnClickListener{
    Button btn_restart;
    Button btn_nextround;
    Button btn_exit;

    public GameResultFragment(){
        setStyle(DialogFragment.STYLE_NO_TITLE,0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_gameresult,null);
        btn_restart = (Button) root.findViewById(R.id.btn_restart);
        btn_nextround = (Button) root.findViewById(R.id.btn_nextround);
        btn_exit = (Button) root.findViewById(R.id.btn_exit);

        return root;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){

        }
    }
}
