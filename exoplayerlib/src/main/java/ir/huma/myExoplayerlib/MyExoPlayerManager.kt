package ir.huma.myExoplayerlib

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.media.session.PlaybackState
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.chibde.visualizer.LineBarVisualizer
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioListener
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.text.CaptionStyleCompat
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import ir.huma.myExoplayerlib.util.MyAdapter
import ir.huma.myExoplayerlib.util.MyPlayerView
import ir.huma.myExoplayerlib.util.TextDrawable
import java.util.*
import kotlin.collections.ArrayList

public class MyExoPlayerManager : FrameLayout {

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context!!, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    var showControllerTimeout = 4000
    var marginBottom = 120
        set(value) {
            if (view != null) {
                val viewGroup = view.findViewById<ViewGroup>(R.id.exo_controller_bottom_view)
                (viewGroup.layoutParams as RelativeLayout.LayoutParams).bottomMargin = value
            }
        }

    var typeface: Typeface? = null
        set(value) {
            field = value
            titleTextView.setTypeface(value)
            descriptionTextView.setTypeface(value)
            view.findViewById<TextView>(R.id.exo_duration).setTypeface(value)
            view.findViewById<TextView>(R.id.exo_position).setTypeface(value)
        }

    var qualityChanging = false;
    lateinit var player: SimpleExoPlayer
    lateinit var playerView: MyPlayerView
    var hasVideo = false
        get() {
            if (player != null)
                return player.videoFormat != null || (player.videoFormat == null && player.audioFormat == null)
            return field
        }
    private lateinit var view: View
    private lateinit var visualizer: LineBarVisualizer
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var avatarImageView: ImageView
    private lateinit var qualityButton: ImageView
    private lateinit var backImageView: ImageView
    private lateinit var subtitleButon: ImageView

    var listener: Player.EventListener? = null
        set(value) {
            if (value != null && player != null)
                player.addListener(value!!)
        }
    lateinit var dataSourceFactoryHttp: DataSource.Factory
    lateinit var dataSourceFactoryFile: DataSource.Factory
    var currentIndex = 0
    var mediaInfoes = ArrayList<MediaInfo>()
        private set
    private var tempQuality: String? = null
        get() {
            if (field == null && mediaInfoes != null && mediaInfoes.size > 0) {
                field = mediaInfoes.get(0).currentQuality
            }
            return field
        }
    private var subtitleView: SubtitleView? = null
    private fun init() {

        view = LayoutInflater.from(context).inflate(R.layout.my_exo_player_mananger, this, true)
        playerView = view.findViewById(R.id.playerView)
        visualizer = view.findViewById(R.id.visualizer)
        titleTextView = view.findViewById(R.id.titleTextView)
        descriptionTextView = view.findViewById(R.id.descriptionTextView)
        avatarImageView = view.findViewById(R.id.avatarImageView)
        qualityButton = view.findViewById(R.id.exo_quality)
        backImageView = view.findViewById(R.id.backImageView)
        subtitleButon = view.findViewById(R.id.exo_sub)
        val trackSelector = DefaultTrackSelector(context!!)
        trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd())

        player = SimpleExoPlayer.Builder(context!!).setTrackSelector(trackSelector).build()
//        playerView.subtitleView.visibility = View.GONE
        marginBottom = 60
        playerView.player = player
        playerView.controllerShowTimeoutMs = 0
        val visualizerColor = ContextCompat.getColor(context!!, R.color.huma_player_visualizer_Color)
        visualizer.setColor(visualizerColor);
        visualizer.setDensity(120f)
        val field = LineBarVisualizer::class.java.getDeclaredField("middleLine")
        field.isAccessible = true
        val middlePaint = Paint()
        middlePaint.setColor(visualizerColor)
        field.set(visualizer, middlePaint)
        subtitleView = playerView.subtitleView

        dataSourceFactoryHttp = DefaultHttpDataSourceFactory(Util.getUserAgent(context, ""))
        dataSourceFactoryFile = DefaultDataSourceFactory(context)

        player.addListener(object : Player.EventListener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == PlaybackState.STATE_PLAYING) {
                    if (!hasVideo) {
                        descriptionTextView.visibility = View.GONE
                        visualizer.visibility = View.VISIBLE
                        playerView.controllerShowTimeoutMs = 0
                    } else {
                        descriptionTextView.visibility = View.VISIBLE
                        visualizer.visibility = View.GONE
                        playerView.controllerShowTimeoutMs = showControllerTimeout
                    }
                    val item = mediaInfoes.get(player.currentWindowIndex)
                    tempQuality = item.currentQuality
                    Log.d("exo_player", "setData" + " : statePlaying " + item.title + " " + item.currentQuality)
                    setData()
                    qualityChanging = false

                }
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
                currentIndex = player.currentWindowIndex
//                super.onTracksChanged(trackGroups, trackSelections)
                if (!qualityChanging) {

                    descriptionTextView.visibility = View.VISIBLE
                    visualizer.visibility = View.GONE
                    playerView.controllerShowTimeoutMs = 0
                    val item = mediaInfoes.get(player.currentWindowIndex)

                    Log.d("exo_player", "setData" + " : onTrackChangeed " + item.title + " " + item.currentQuality)
                    if(item.seek > player.currentPosition){
                        player.seekTo(item.seek)
                    }
                    setData()
                }
            }

        })
        player.addVideoListener(object : VideoListener {
            override fun onRenderedFirstFrame() {
                Log.d("exo_player", "addVideoListener " + player.audioFormat + player.videoFormat)
            }
        })

        // player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.addTextOutput { cues ->
            Log.e("cues are ", cues.toString())
            if (subtitleView != null) {
                subtitleView!!.onCues(cues)
            }
        }

        playerView.setOnBackListener {
            if (hasVideo && playerView.isControllerVisible()) {
                playerView.hideController()
                true
            } else
                false
        }

        player.addAudioListener(object : AudioListener {
            override fun onAudioSessionId(audioSessionId: Int) {
//                if (!hasVideo) {
                try {
                    visualizer.setPlayer(audioSessionId);
                } catch (e: Exception) {

                }
//                    descriptionTextView.visibility = View.GONE
//                }
//                Log.d("exo_player", "addAudioListener " + player.audioFormat + player.videoFormat)
            }
        })

        qualityButton.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                val item = mediaInfoes.get(player.currentWindowIndex)
                if (tempQuality == null) {
                    tempQuality = item.currentQuality
                }
                tempQuality = item.getNextQuality(tempQuality!!)
                qualityButton.setImageDrawable(TextDrawable(resources, tempQuality))
                Handler().postDelayed(object : Runnable {
                    val quality = tempQuality
                    override fun run() {
                        if (quality == tempQuality) {
                            changeMediaSource()
                        }
                    }
                }, 2000)
            }
        })

        subtitleButon.setOnClickListener(OnClickListener {
            val contentView = LinearLayout(context)
            contentView.setOrientation(LinearLayout.VERTICAL);
            contentView.setBackgroundColor(Color.WHITE)
            contentView.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val adapter = MyAdapter<String>(context, R.layout.layout_subtitle_popup_item, MediaInfo.MyHolder::class.java)

            var temp = false
            if (mediaInfoes.size > 0 && mediaInfoes.get(player.currentWindowIndex).subtitles != null) {
                temp = true
                for (item in mediaInfoes.get(player.currentWindowIndex).subtitles!!) {
                    val loc = Locale(item.language)
                    val name: String = loc.getDisplayLanguage(loc)
                    adapter.add(name)
                }
                adapter.add("غیرفعال")
                adapter.setObjects(
                        if (subtitleView!!.visibility != View.VISIBLE) adapter.count - 1 else mediaInfoes.get(player.currentWindowIndex).currentSubtitle,
                        typeface
                )
            }

            val popup = PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

            for (i in 0..adapter.count - 1) {
                val v = adapter.getView(i, null, contentView)
                v.setOnClickListener(OnClickListener {
                    val x = contentView.indexOfChild(v)
                    if (x == adapter.count - 1) {
                        subtitleView!!.visibility = View.GONE
                        subtitleButon!!.isSelected = true;
                    } else {
                        subtitleView!!.visibility = View.VISIBLE
                        subtitleButon!!.isSelected = false;
                        if (mediaInfoes.get(player.currentWindowIndex).currentSubtitle != x) {
                            mediaInfoes.get(player.currentWindowIndex).currentSubtitle = x
                            changeMediaSource()
                        }
                    }
                    popup.dismiss()
                })
                contentView.addView(v)
            }
            popup.setOnDismissListener {
                playerView.controllerShowTimeoutMs = showControllerTimeout
            }

            if (temp) {
                popup.showAsDropDown(it, 0, -(it.getHeight() + (72 * adapter.count)), Gravity.CENTER_HORIZONTAL)
                playerView.controllerShowTimeoutMs = 0
            }
        })
    }


    fun changeMediaSource() {
        player.pause()
        val pos = player.currentPosition
        val current = player.currentWindowIndex
        qualityChanging = true
        player.stop()
        player.clearMediaItems()
        val media = mediaInfoes.get(player.currentWindowIndex)
        media.getMediaItem(tempQuality!!)?.let {
            for (item in mediaInfoes) {
                buildMediaSource(item)?.let { player.addMediaSource(it) }
            }
            player.seekToDefaultPosition(current)
            player.prepare()
            player.play()
            if (pos > media.seek)
                player.seekTo(pos)
            else
                player.seekTo(media?.seek)
        }
    }


    fun addMediaItem(index: Int, mediaInfo: MediaInfo) {
        mediaInfoes.add(index, mediaInfo)
        buildMediaSource(mediaInfo)?.let { player.addMediaSource(index, it) }
    }

    fun addMediaItem(mediaInfo: MediaInfo) {
        mediaInfoes.add(mediaInfo)
        buildMediaSource(mediaInfo)?.let { player.addMediaSource(it) }
    }

    fun addMediaItems(index: Int, mediaInfoes: List<MediaInfo>) {
        this.mediaInfoes.addAll(index, mediaInfoes)
        for (item in mediaInfoes) {
            buildMediaSource(item)?.let { player.addMediaSource(index, it) }
        }
    }

    fun addMediaItems(mediaInfoes: List<MediaInfo>) {
        this.mediaInfoes.addAll(mediaInfoes)
        for (item in mediaInfoes) {
            buildMediaSource(item)?.let { player.addMediaSource(it) }
        }
    }

    private fun buildMediaSource(mediaInfo: MediaInfo): MediaSource? {


        val dataSourceFactory = if (!mediaInfo.isMediaLocal(tempQuality!!)) dataSourceFactoryHttp else dataSourceFactoryFile
        var mediaSource: MediaSource
        if (!mediaInfo.isLive) {
            mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaInfo.getMediaItem(tempQuality!!)!!)
        } else
            mediaSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaInfo.getMediaItem(tempQuality!!)!!)
        if (mediaInfo.getCurrentSubtitle() == null) {
            return mediaSource
        }
//        val subtitleSource: MediaSource = SingleSampleMediaSource.Factory(dataSourceFactory).createMediaSource(mediaInfo.getCurrentSubtitle()!!.uri,textFormat, C.TIME_UNSET)
        val subtitleSource: MediaSource = SingleSampleMediaSource.Factory(dataSourceFactory).createMediaSource(mediaInfo.getCurrentSubtitle()!!, C.TIME_UNSET)
        val subtitleSource2: MediaSource = SingleSampleMediaSource.Factory(dataSourceFactory).createMediaSource(mediaInfo.getCurrentSubtitle()!!, C.TIME_UNSET)

        return MergingMediaSource(mediaSource, subtitleSource, subtitleSource2)
    }


    private fun setData() {
        val item = mediaInfoes.get(player.currentWindowIndex)
        titleTextView.setText(item.title)
        if (item.description != null && hasVideo) {
            descriptionTextView.setText(item.description)
            descriptionTextView.visibility = View.VISIBLE
        } else {
            descriptionTextView.visibility = View.GONE
        }
        avatarImageView.visibility = View.GONE

        if (item.logoUrl != null) {
            Picasso.get().load(item.logoUrl).into(avatarImageView, object : Callback {
                override fun onSuccess() {
                    avatarImageView.visibility = View.VISIBLE
                }

                override fun onError(e: Exception?) {
                }
            })
        }
        if (item.backgroundUrl != null && !hasVideo) {
            Picasso.get().load(item.backgroundUrl).into(backImageView)
            backImageView.visibility = View.VISIBLE
        } else {
            backImageView.visibility = View.GONE
        }
        if (item.getQualityList().size > 1) {
            qualityButton.visibility = View.VISIBLE
            qualityButton.setImageDrawable(TextDrawable(resources, item.currentQuality))
        } else {
            qualityButton.visibility = View.GONE
        }

        if (item.subtitles == null || item.subtitles!!.size == 0) {
            subtitleButon.visibility = View.GONE
        } else {
            subtitleButon.visibility = View.VISIBLE
        }
    }

    fun start() {
        player.seekToDefaultPosition(currentIndex)
        if(mediaInfoes.get(currentIndex).seek > player.currentPosition){
            player.seekTo(mediaInfoes.get(currentIndex).seek)
        }
        subtitleView!!.setStyle(CaptionStyleCompat(Color.WHITE, Color.TRANSPARENT, Color.TRANSPARENT, CaptionStyleCompat.EDGE_TYPE_OUTLINE, Color.BLACK, typeface))
        player.prepare()
        player.play()
//        setData()
    }

    fun pause() {
        player.pause()
    }

    fun resume() {
        player.play()
    }

    fun stop() {
        player.stop()
    }

    fun showController() {
        playerView?.showController()
    }

    fun hideController() {
        playerView?.hideController()
    }

    fun release() {
        stop()
        player.release()
    }
}