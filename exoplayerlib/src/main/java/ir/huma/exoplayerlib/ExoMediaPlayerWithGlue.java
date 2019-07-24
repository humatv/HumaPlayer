/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package ir.huma.exoplayerlib;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v17.leanback.app.VideoSupportFragmentGlueHost;
import android.support.v17.leanback.media.PlaybackBannerControlGlue;
import android.support.v17.leanback.media.PlaybackGlue;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ir.atitec.everythingmanager.manager.FontManager;
import ir.atitec.everythingmanager.utility.Util;

//import ir.atitec.everythingmanager.manager.FontManager;

/**
 * PlayerGlue for video playback
 *
 * @param <>
 */
public class ExoMediaPlayerWithGlue extends PlaybackBannerControlGlue<ExoPlayerAdapter> {

    private PlaybackControlsRow.SkipNextAction skipNext;
    private PlaybackControlsRow.SkipPreviousAction previousAction;
    private PlaybackControlsRow.MultiAction highQualityAction;

    private PlaybackControlsRow.FastForwardAction forwardAction;
    private PlaybackControlsRow.RewindAction rewindAction;

    private List<VideoData> videoDatas = new ArrayList<>();
    private PlaybackControlsRow.PictureInPictureAction pictureInPictureAction;
    private int index = 0;
    private int qalityIndex = 0;
    ArrayObjectAdapter secondaryAdapter;
    ArrayObjectAdapter primaryAdapter;
    Boolean isPlay = true;
    private OnQualityChange qualityChange;
    private String defaultQuality;
    private OnTrackChange onTrackChange;
    private boolean repeating = false;

    public ExoMediaPlayerWithGlue(ExoPlayerAdapter impl, VideoSupportFragmentGlueHost host, String defaultQuality, VideoData... videoData) {
        super(impl.getContext(), new int[]{10}, impl);
        this.defaultQuality = defaultQuality;
        if (videoData != null)
            this.videoDatas.addAll(Arrays.asList(videoData));
//        if (videoData == null || videoData.length == 0) {
//            return;
//        }

        highQualityAction = new PlaybackControlsRow.MultiAction(5342) {
        };

        previousAction = new PlaybackControlsRow.SkipPreviousAction(getContext());
        skipNext = new PlaybackControlsRow.SkipNextAction(getContext());
        forwardAction = new PlaybackControlsRow.FastForwardAction(getContext());
        forwardAction.setDrawables(new Drawable[]{new IconicsDrawable(getContext()).icon(GoogleMaterial.Icon.gmd_forward_10).color(Color.WHITE).sizeDp(48)});
        rewindAction = new PlaybackControlsRow.RewindAction(getContext());
        rewindAction.setDrawables(new Drawable[]{new IconicsDrawable(getContext()).icon(GoogleMaterial.Icon.gmd_replay_10).color(Color.WHITE).sizeDp(48)});

//        setSeekProvider(new PlaybackSeekDataProvider());
        if (host != null)
            setHost(host);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int margin = dpToPx(getContext(),8);
//                TextView view = ((Activity)getContext()).getWindow().getDecorView().findViewById(R.id.current_time);
//                view.setPadding(margin,0,0,0);
//                TextView view2 = ((Activity)getContext()).getWindow().getDecorView().findViewById(R.id.total_time);
//                view2.setPadding(0,0,margin,0);
//            }
//        },2000);


        ((PlaybackControlsRowPresenter) getPlaybackRowPresenter()).setProgressColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        ((PlaybackControlsRowPresenter) getPlaybackRowPresenter()).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.defaultBrandColor));

    }

    Typeface typeface;

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public ExoMediaPlayerWithGlue build() {
        if (getPrimaryActionsAdapter() != null) {
            if (videoDatas.size() > 1) {
                getPrimaryActionsAdapter().add(0, previousAction);
                getPrimaryActionsAdapter().add(getPrimaryActionsAdapter().size(), skipNext);
            }

            getSecondaryActionsAdapter().add(highQualityAction);
            setData();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (typeface != null)
                    FontManager.instance(typeface).setTypeface(((Activity) getContext()).findViewById(R.id.lb_details_description_title).getRootView());
                View layout = ((Activity) getContext()).findViewById(R.id.lb_details_description_title);
                View layout2 = ((Activity) getContext()).findViewById(R.id.lb_details_description_subtitle);
                LinearLayout ll = (LinearLayout) layout.getParent();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ll.getLayoutParams();
                params.width = Util.getWindowWidth((Activity) getContext()) - 500;
//                params.gravity = Gravity.RIGHT;
                ll.setLayoutParams(params);
                LinearLayout.LayoutParams l = (LinearLayout.LayoutParams) layout.getLayoutParams();
                l.gravity = Gravity.RIGHT;
                layout.setLayoutParams(l);

                l = (LinearLayout.LayoutParams) layout2.getLayoutParams();
                l.gravity = Gravity.RIGHT;
                layout2.setLayoutParams(l);

            }
        }, 400);

        return this;
    }

    public OnQualityChange getQualityChange() {
        return qualityChange;
    }

    public void setQualityChange(OnQualityChange qualityChange) {
        this.qualityChange = qualityChange;
    }

    @Override
    protected void onCreateSecondaryActions(ArrayObjectAdapter adapter) {
        super.onCreateSecondaryActions(adapter);
    }

    @Override
    protected void onCreatePrimaryActions(ArrayObjectAdapter adapter) {
        super.onCreatePrimaryActions(adapter);
        adapter.add(0, rewindAction);
        adapter.add(adapter.size(), forwardAction);
    }


    @Override
    public void onActionClicked(Action action) {
        if (shouldDispatchAction(action)) {
            dispatchAction(action);
            return;
        }
        super.onActionClicked(action);
    }

    private boolean shouldDispatchAction(Action action) {
        return action == skipNext
                || action == previousAction
                || action == highQualityAction
                || action == forwardAction
                || action == rewindAction
                ;

    }

    private void dispatchAction(Action action) {
        if (action == skipNext) {
            next();
        } else if (action == previousAction) {
            previous();
        } else if (action == highQualityAction) {
            changeQuality();
        } else if (action == forwardAction) {
            getPlayerAdapter().fastForward();
        } else if (action == rewindAction) {
            getPlayerAdapter().rewind();
        }
    }

    private void notifyActionChanged(PlaybackControlsRow.MultiAction action) {
        int index = -1;
        if (getPrimaryActionsAdapter() != null) {
            index = getPrimaryActionsAdapter().indexOf(action);
        }
        if (index >= 0) {
            getPrimaryActionsAdapter().notifyArrayItemRangeChanged(index, 1);
        } else {
            if (getSecondaryActionsAdapter() != null) {
                index = getSecondaryActionsAdapter().indexOf(action);
                if (index >= 0) {
                    getSecondaryActionsAdapter().notifyArrayItemRangeChanged(index, 1);
                }
            }
        }
    }

    private ArrayObjectAdapter getPrimaryActionsAdapter() {
        if (getControlsRow() == null) {
            return null;
        }
        return (ArrayObjectAdapter) getControlsRow().getPrimaryActionsAdapter();
    }

    private ArrayObjectAdapter getSecondaryActionsAdapter() {
        if (getControlsRow() == null) {
            return null;
        }
        return (ArrayObjectAdapter) getControlsRow().getSecondaryActionsAdapter();
    }

    @Override
    protected void onPlayCompleted() {
        super.onPlayCompleted();
        next();
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setIndexAndPlay(int index) {
        this.index = index;
        try {
            setData();
        } catch (Exception e) {

        }
        if (onTrackChange != null) {
            onTrackChange.onChange(index - 1, index);
        }

    }

    @Override
    public void next() {
        if (repeating && index + 1 >= videoDatas.size()) {
            index = -1;
        }
        if (index + 1 < videoDatas.size()) {
            index++;
            try {
                setData();
            } catch (Exception e) {

            }
            if (onTrackChange != null) {
                onTrackChange.onChange(index - 1, index);
            }
        }
    }

    public void setData() {
        getPlayerAdapter().pause();
        qalityIndex = videoDatas.get(index).getQualityList().size() > 1 ? 1 : 0;
        for (int i = 0; i < videoDatas.get(index).getQualityList().size(); i++) {
            if (videoDatas.get(index).getQualityList().get(i).equalsIgnoreCase(defaultQuality)) {
                qalityIndex = i;
                break;
            }
        }

        ((ExoPlayerAdapter) getPlayerAdapter()).setDataSource(videoDatas.get(index).getUrlList().get(qalityIndex), videoDatas.get(index).isLive());
        playWhenReady(this);
        setTitle(videoDatas.get(index).getTitle());
        setSubtitle(videoDatas.get(index).getSubTitle());

//        getSecondaryActionsAdapter().remove(highQualityAction);
        if (videoDatas.get(index).getQualityList().size() > 1) {

            highQualityAction.setDrawables(videoDatas.get(index).getQualitiesDrawable(getContext()));
            highQualityAction.setIndex(qalityIndex);
            getSecondaryActionsAdapter().notifyItemRangeChanged(0, 1);
//            notifyActionChanged(highQualityAction);
//            getSecondaryActionsAdapter().add(highQualityAction);
        }
    }

    @Override
    public void previous() {
        if (index > 0) {
            index--;
            try {
                setData();
            } catch (Exception e) {

            }
            if (onTrackChange != null) {
                onTrackChange.onChange(index + 1, index);
            }
        }
    }

    private void changeQuality() {
        if (videoDatas.get(index).getQualityList().size() == 1) {
            return;
        }
        qalityIndex++;
        qalityIndex = qalityIndex % videoDatas.get(index).getQualityList().size();
        final int qu = qalityIndex;
        highQualityAction.setIndex(qalityIndex);
        notifyActionChanged(highQualityAction);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (qu != qalityIndex) {
                    return;
                }
                if (qualityChange != null) {
                    qualityChange.onQuality(qalityIndex, videoDatas.get(index).getQualityList().get(qalityIndex));
                }
                getPlayerAdapter().pause();
                final long l = getPlayerAdapter().getCurrentPosition();
                ((ExoPlayerAdapter) getPlayerAdapter()).setDataSource(videoDatas.get(index).getUrlList().get(qalityIndex), videoDatas.get(index).isLive());
                if (isPrepared()) {
                    seekTo(l);
                    play();
                } else {
                    addPlayerCallback(new PlaybackGlue.PlayerCallback() {
                        @Override
                        public void onPreparedStateChanged(PlaybackGlue glue) {
                            if (glue.isPrepared()) {
                                glue.removePlayerCallback(this);
                                seekTo(l);
                                glue.play();
                            }
                        }
                    });
                }
            }
        }, 1000);

    }

    @Override
    public void play() {
        super.play();
        synchronized (isPlay) {
            isPlay = true;
        }
    }

    @Override
    public void pause() {
        super.pause();
        synchronized (isPlay) {
            isPlay = false;
        }
    }

    private void playWhenReady(PlaybackGlue glue) {
        synchronized (isPlay) {
            if (glue.isPrepared() && isPlay) {
                glue.play();
            } else {
                glue.addPlayerCallback(new PlaybackGlue.PlayerCallback() {
                    @Override
                    public void onPreparedStateChanged(PlaybackGlue glue) {
                        if (glue.isPrepared()) {
                            glue.removePlayerCallback(this);
                            synchronized (isPlay) {
                                if (isPlay)
                                    glue.play();
                            }
                        }
                    }
                });
            }
        }
    }

    public void addVideo(VideoData videoData) {
        videoDatas.add(videoData);
    }

    public void setOnTrackChange(OnTrackChange onTrackChange) {
        this.onTrackChange = onTrackChange;
    }

    public List<VideoData> getVideoDatas() {
        return videoDatas;
    }

    public interface OnQualityChange {
        void onQuality(int index, String quality);
    }

    public interface OnTrackChange {
        void onChange(int old, int current);
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }
}