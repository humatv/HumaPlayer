package ir.huma.myExoplayerlib.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.google.android.exoplayer2.ui.PlayerView;

import androidx.annotation.Nullable;

public class MyPlayerView extends PlayerView {
    private OnBackListener onBackListener;

    public MyPlayerView(Context context) {
        super(context);
    }

    public MyPlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            if(onBackListener != null && onBackListener.onBackClick()){
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public OnBackListener getOnBackListener() {
        return onBackListener;
    }

    public void setOnBackListener(OnBackListener onBackListener) {
        this.onBackListener = onBackListener;
    }

    public interface OnBackListener {
        boolean onBackClick();
    }
}
