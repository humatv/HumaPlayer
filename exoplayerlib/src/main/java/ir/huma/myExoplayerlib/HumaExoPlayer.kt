package ir.huma.myExoplayerlib

import android.content.Context
import android.media.session.PlaybackState
import android.util.Log
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util

class HumaExoPlayer(context: Context) : SimpleExoPlayer(SimpleExoPlayer.Builder(context)) {

    private var dataSourceFactoryHttp: DataSource.Factory =
        DefaultHttpDataSourceFactory(Util.getUserAgent(context, context.packageName))
    private var dataSourceFactoryFile: DataSource.Factory = DefaultDataSourceFactory(context)
    private var qualityChanging = false
    var tag: Any? = null;
    var updateViewListener: UpdateViewListener? = null
    var mediaInfoes = ArrayList<MediaInfo>()
        private set
    var defaultQuality: String? = null
        get() {
            if (field == null && mediaInfoes.size > 0) {
                field = mediaInfoes[0].currentQuality
            }
            return field
        }
    private var eventListener = object : Player.EventListener {
        override fun onPlaybackStateChanged(state: Int) {
            if (state == PlaybackState.STATE_PLAYING) {
                val item = getCurrentMedia()

                updateViewListener?.update(this@HumaExoPlayer, item!!)
                defaultQuality = item!!.currentQuality
                qualityChanging = false

            }
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
            val item = getCurrentMedia()

            if (item!!.hasVideo == null) {
                if (trackGroups.length == 1 && trackGroups[0].getFormat(0).sampleMimeType?.indexOf("audio") != -1
                ) {
                    item!!.hasVideo = false
                } else if (trackGroups.length > 1) {
                    item!!.hasVideo = true
                }
            }
//                super.onTracksChanged(trackGroups, trackSelections)
            if (!qualityChanging) {
                updateViewListener?.update(this@HumaExoPlayer, item)

                Log.d(
                    "exo_player",
                    "setData" + " : onTrackChanged " + item.title + " " + item.currentQuality
                )
                if (item.seek > currentPosition) {
                    seekTo(item.seek)
                }
            }
        }

    }

    init {
        addListener(this.eventListener)
    }


    fun notifyMediaChange(currentIndex: Int = currentWindowIndex) {
        qualityChanging = true
        pause()
        stop()
        clearMediaItems()
        val media = mediaInfoes[currentIndex]
        media.getMediaItem(defaultQuality!!)?.let {
            for (item in mediaInfoes) {
                addMediaSource(buildMediaSource(item))
            }
            val isResumePlaying = currentIndex == currentWindowIndex
            seekToDefaultPosition(currentIndex)
            prepare()
            play()
//            if (isResumePlaying && currentPosition > media.seek)
//                seekTo(currentPosition)
//            else
//                seekTo(media.seek)
        }
    }

    fun getCurrentMedia(): MediaInfo? {
        return currentWindowIndex.let { mediaInfoes.getOrNull(it) }
    }

    fun addMedia(index: Int, mediaInfo: MediaInfo) {
        var newIndex = 0
        if (mediaInfoes.size == 0) {
            newIndex = 0
        } else {
            if (mediaInfoes.get(0).index > index) {
                newIndex = 0
            }
            else if (mediaInfoes.size > index) {
                newIndex = index
            }
            else {
                newIndex = mediaInfoes.size
            }
        }
        mediaInfo.index = index
        mediaInfoes.add(newIndex, mediaInfo)
        addMediaSource(newIndex, buildMediaSource(mediaInfo))
    }

    fun addMedia(mediaInfo: MediaInfo) {
        mediaInfoes.add(mediaInfo)
        addMediaSource(buildMediaSource(mediaInfo))
    }

    fun updateMedia(index: Int, media: MediaInfo) {
        mediaInfoes[index] = media
        buildMediaSource(media).let {
            removeMediaItem(index)
            addMediaSource(index, it)
        }
    }


    fun addMedias(index: Int, mediaInfoes: List<MediaInfo>) {
        this.mediaInfoes.addAll(index, mediaInfoes)
        for (item in mediaInfoes) {
            addMediaSource(index, buildMediaSource(item))
        }
    }

    fun addMedias(mediaInfoes: List<MediaInfo>) {
        this.mediaInfoes.addAll(mediaInfoes)
        for (item in mediaInfoes) {
            addMediaSource(buildMediaSource(item))
        }
    }

    private fun buildMediaSource(mediaInfo: MediaInfo): MediaSource {
        val dataSourceFactory =
            if (!mediaInfo.isMediaLocal(defaultQuality!!)) dataSourceFactoryHttp else dataSourceFactoryFile
        val mediaSource: MediaSource = if (!mediaInfo.isLive) {
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaInfo.getMediaItem(defaultQuality!!)!!)
        } else
            HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaInfo.getMediaItem(defaultQuality!!)!!)
        if (mediaInfo.getCurrentSubtitle() == null) {
            return mediaSource
        }
        val subtitleSource: MediaSource = SingleSampleMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaInfo.getCurrentSubtitle()!!, C.TIME_UNSET)
        val subtitleSource2: MediaSource = SingleSampleMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaInfo.getCurrentSubtitle()!!, C.TIME_UNSET)

        return MergingMediaSource(mediaSource, subtitleSource, subtitleSource2)
    }

    fun start(currentIndex: Int = 0) {
        Log.d("MyExoPlayer", "start")

        seekToDefaultPosition(currentIndex)
        if (getCurrentMedia()!!.seek > currentPosition) {
            seekTo(getCurrentMedia()!!.seek)
        }

        prepare()
        play()
    }

    interface UpdateViewListener {
        fun update(player: SimpleExoPlayer, mediaInfo: MediaInfo)
    }

}