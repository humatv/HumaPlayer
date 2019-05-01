package ir.huma.exoplayerlib;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class VideoData {
    private List<Uri> urlList = new ArrayList<>();
    private List<String> qualityList = new ArrayList<>();
    private String title;
    private String subTitle;
    private boolean isLive;

    public VideoData() {
    }

    public VideoData addData(String url, String quality) {
        urlList.add(Uri.parse(url));
        qualityList.add(quality);
        return this;
    }

    public List<Uri> getUrlList() {
        return urlList;
    }

    public List<String> getQualityList() {
        return qualityList;
    }

    public Drawable[] getQualitiesDrawable(Context context) {
        Drawable[] drawables = new Drawable[qualityList.size()];
        if (qualityList.size() > 1) {
            for (int i = 0; i < qualityList.size(); i++) {
                drawables[i] = new TextDrawable(context.getResources(), qualityList.get(i));
            }
        } else {
            drawables[0] = new TextDrawable(context.getResources(), "");
        }
        return drawables;
    }

    public String getTitle() {
        return title;
    }

    public VideoData setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public VideoData setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public boolean isLive() {
        return isLive;
    }

    public VideoData setLive(boolean live) {
        isLive = live;
        return this;
    }
}
