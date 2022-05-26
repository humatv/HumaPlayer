package ir.huma.myExoplayerlib;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.chibde.visualizer.LineBarVisualizer;

public class MyLineBarVisualizer extends LineBarVisualizer {
    public MyLineBarVisualizer(Context context) {
        super(context);
    }

    public MyLineBarVisualizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLineBarVisualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int audioSessionId;

    @Override
    public void setPlayer(int audioSessionId) {
        super.setPlayer(audioSessionId);
        this.audioSessionId = audioSessionId;
    }

    @Override
    public void release() {
        if (visualizer != null) {
            visualizer.release();
            bytes = null;
            invalidate();
            visualizer = null;
        }
    }
}
