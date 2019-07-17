package ir.huma.humaleanbackvideolib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v17.leanback.app.VideoSupportFragment;
import android.support.v17.leanback.app.VideoSupportFragmentGlueHost;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.lang.reflect.Field;

import ir.huma.exoplayerlib.ExoMediaPlayerWithGlue;
import ir.huma.exoplayerlib.ExoPlayerAdapter;
import ir.huma.exoplayerlib.VideoData;

public class TestVideoPlayerFragment extends VideoSupportFragment {

    //    public static final String URL = "https://tci1.asset.aparat.com/aparat-video/caf8edf07fa7a9637dc6537a725230f112926411-240p__24703.mp4";
    public static final String URL = "http://cdnlive.irib.ir/live-channels/smil:tv2/playlist.m3u8?s=-o7pCet47k7PjBdgH-cOGw";
    public static final String URL2 = "https://hw1.cdn.asset.aparat.com/aparat-video/0432a1acadb535c4b191e4c4eec1d85e12873438-480p__39876.mp4";
    VideoSupportFragmentGlueHost mHost;

    private ExoMediaPlayerWithGlue mMediaPlayerGlue;

    Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        mHost = new VideoSupportFragmentGlueHost(this);
        mMediaPlayerGlue = new ExoMediaPlayerWithGlue(new ExoPlayerAdapter(context), mHost, "360p",
                new VideoData().setTitle("خرکده").setSubTitle("آلودگی هوا").addData("https://hw6.cdn.asset.aparat.com/aparat-video/9fc756da8d1d652c931319f7479a8a5312948926-480p__42291.mp4", "480p").addData("https://hw6.cdn.asset.aparat.com/aparat-video/9fc756da8d1d652c931319f7479a8a5312948926-360p__42291.mp4", "360p").addData("https://hw6.cdn.asset.aparat.com/aparat-video/9fc756da8d1d652c931319f7479a8a5312948926-360p__42291.mp4", "240p"),
                new VideoData().setTitle("hello").setSubTitle("hello sub").setLive(true).addData(URL, "test"),
                new VideoData().setTitle("الکی").setSubTitle("توضیحات زیرین الکی").addData(URL2, "720p"));
        mMediaPlayerGlue.setRepeating(true);
        mMediaPlayerGlue.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/BYekan.ttf"));

        mMediaPlayerGlue.build();
    }

}
