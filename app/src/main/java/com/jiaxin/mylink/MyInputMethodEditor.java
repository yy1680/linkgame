package com.jiaxin.mylink;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;

/**
 * Created by jiaxin on 2016/12/28.
 */

public class MyInputMethodEditor extends InputMethodService {
    KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            InputConnection connection = getCurrentInputConnection();
            String text = "";
            switch (primaryCode) {
                case 159:
                    text = "15915760015";
                    break;
                case 308:
                    text = "308592372@qq.com";
                    break;
                case 136:
                    text = "13672467193";
                    break;
                case 1:
                    text = "jxdxiaohao1";
                    break;
                case 2:
                    text = "jxdxiaohao2";
                    break;
                case 3:
                    text = "jxdxiaohao3";
                    break;
                case 4:
                    text = "jxdxiaohao4";
                    break;
                case 5:
                    text = "jxdxiaohao5";
                    break;
                case 6:
                    text = "jxdxiaohao6";
                    break;
                case 7:
                    text = "jxdxiaohao7";
                    break;
                case 100:
                    text = "scnu2009hjx";
                    break;
                case 200:
                    text = "jxdxhzymm";
                    break;
                case -5:
                    connection.deleteSurroundingText(1, 0);
                    return;
            }

            connection.commitText(text, 1);
        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };

    @Override
    public View onCreateInputView() {
        LayoutInflater lf = getLayoutInflater();
        View root = lf.inflate(R.layout.keyboard, null);
        KeyboardView keyboardView = (KeyboardView) root.findViewById(R.id.keyboard);
        keyboardView.setOnKeyboardActionListener(listener);
        Keyboard keyboard = new Keyboard(this, R.xml.keys_layout);
        keyboardView.setKeyboard(keyboard);
        return root;
    }

//    View.OnClickListener mListener = new View.OnClickListener(){
//        @Override
//        public void onClick(View v) {
//            Log.v("--------------------","click");
//            InputConnection connection = getCurrentInputConnection();
//            int id = v.getId();
//            switch (id){
//                case R.id.k159:
//                    int inputType = getCurrentInputEditorInfo().inputType;
//                    Toast.makeText(getApplicationContext(),"159",Toast.LENGTH_SHORT)
//                            .show();
//                    if(inputType == InputType.TYPE_CLASS_TEXT){
//
//                    }else if(InputType.TYPE_TEXT_VARIATION_PASSWORD == inputType){
//                        Log.v("aaa","oh year");
//                    }
//                    connection.commitText("15915760015",1);
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
}
