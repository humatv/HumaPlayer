package ir.huma.myExoplayerlib

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.chibde.visualizer.LineBarVisualizer
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioListener
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.text.CaptionStyleCompat
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.SubtitleView
import ir.huma.myExoplayerlib.util.MyAdapter
import ir.huma.myExoplayerlib.util.MyPlayerView
import ir.huma.myExoplayerlib.util.TextDrawable
import java.util.*
import kotlin.math.min

class HumaExoPlayerView : FrameLayout {
    private val TAG = "HumaExoPlayerView"

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var showDescriptionOnAudio = true
    var showVisualizer = true

    private var showControllerTimeout = 4000
    private var playerCurrentMediaId = ""

    var typeface: Typeface? = null
        set(value) {
            field = value
            titleTextView?.typeface = value
            skippTimeButton.typeface = value
            descriptionTextView?.typeface = value
            view.findViewById<TextView>(R.id.exo_duration).typeface = value
            view.findViewById<TextView>(R.id.exo_position).typeface = value
        }

    var adPlayer: HumaExoPlayer? = null
    var player: HumaExoPlayer? = null
        set(value) {
            field = value
            if (value == null) {
                playerView.player = null
                visualizer.release()
            } else {
                init()
            }
        }
    private lateinit var playerView: MyPlayerView
    private lateinit var adPlayerView: MyPlayerView
    private lateinit var adPlayerInterface: AdPlayer

    private lateinit var view: View
    private lateinit var visualizer: MyLineBarVisualizer
    private lateinit var avatarImageView: ImageView
    private lateinit var qualityButton: ImageView
    private lateinit var skippTimeButton: Button
    var subtitleButton: ImageView? = null
        private set
    var descriptionTextView: TextView? = null
        private set
    var backImageView: ImageView? = null
        private set
    var titleTextView: TextView? = null
        private set

    private var subtitleView: SubtitleView? = null

    private var adTimeToSkippS: Int = 0
    var adCanSkipp = false

    companion object {
        private const val SKIPP_TEXT = "رد کردن آگهی"

    }

    var isControllerVisible = playerView.isControllerVisible


    fun init() {
        view = LayoutInflater.from(context).inflate(R.layout.my_exo_player_mananger, this, true)
        playerView = view.findViewById(R.id.playerView)
        adPlayerView = view.findViewById(R.id.adPlayerView)
        skippTimeButton = view.findViewById(R.id.skippTimeButton)

        visualizer = view.findViewById(R.id.visualizer)
        titleTextView = view.findViewById(R.id.titleTextView)
        descriptionTextView = view.findViewById(R.id.descriptionTextView)
        avatarImageView = view.findViewById(R.id.avatarImageView)
        qualityButton = view.findViewById(R.id.exo_quality)
        backImageView = view.findViewById(R.id.backImageView)
        subtitleButton = view.findViewById(R.id.exo_sub)
//        val trackSelector = DefaultTrackSelector(context!!)
//        trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd())
        adPlayer = HumaExoPlayer(context)
        adPlayerView.player = adPlayer
        adPlayerView.controllerShowTimeoutMs = 0
        playerView.player = player
        playerView.controllerShowTimeoutMs = 0

        initVisualizer()
        initSubtitle()

        playerView.setOnBackListener {
            if (player?.getCurrentMedia()?.hasVideo == true && playerView.isControllerVisible) {
                playerView.hideController()
                true
            } else
                false
        }


        skippTimeButton.setOnClickListener {
            if (!adCanSkipp) return@setOnClickListener
            if (this::adPlayerInterface.isInitialized) adPlayerInterface.onSkippButtonClickListener()
            closeAdAndPlayMainVideo()
        }
        qualityButton.setOnClickListener {
            val item = player?.getCurrentMedia()
            if (player?.defaultQuality == null) {
                player?.defaultQuality = item?.currentQuality
            }
            player?.defaultQuality = item?.getNextQuality(player?.defaultQuality!!)
            qualityButton.setImageDrawable(
                TextDrawable(
                    resources,
                    player?.defaultQuality
                )
            )
            Handler(context.mainLooper).postDelayed(object : Runnable {
                val quality = player?.defaultQuality
                override fun run() {
                    if (quality == player?.defaultQuality) {
                        player?.notifyMediaChange()
                    }
                }
            }, 2000)
        }


        player?.updateViewListener = object : HumaExoPlayer.UpdateViewListener {
            override fun update(player: SimpleExoPlayer, mediaInfo: MediaInfo) {
                setData()
            }
        }


        if (player?.isPlaying!!) {
            Log.d(TAG, "init: isPlaying")
            Handler(Looper.getMainLooper()).postDelayed({
                setData()
            }, 1000)
        }

        adPlayer?.addListener(adPlayerEventListener)
    }

    private fun initVisualizer() {
        val visualizerColor =
            ContextCompat.getColor(context!!, R.color.huma_player_visualizer_Color)
        visualizer.setColor(visualizerColor)
        visualizer.setDensity(120f)
        val field = LineBarVisualizer::class.java.getDeclaredField("middleLine")
        field.isAccessible = true
        val middlePaint = Paint()
        middlePaint.color = visualizerColor
        field.set(visualizer, middlePaint)
        player?.addAudioListener(
            object : AudioListener {
                override fun onAudioSessionId(audioSessionId: Int) {
                    setAudioSession(audioSessionId)
                }
            })
        if (player!!.isPlaying && player!!.getCurrentMedia() != null && player!!.getCurrentMedia()?.hasVideo == false) {
            setAudioSession(player!!.audioSessionId)
        }
    }

    private fun setAudioSession(audioSessionId: Int) {
        if (visualizer.visualizer != null) {
//            if (visualizer.audioSessionId == audioSessionId)
//                return
            try {
                visualizer.release()
            } catch (e: Exception) {
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                visualizer.setPlayer(audioSessionId)
            } catch (e: Exception) {
                //                    setAudioSession(audioSessionId)
            }
        }, 1000)

    }

    private fun initSubtitle() {
        subtitleView = playerView.subtitleView
        subtitleView!!.setStyle(
            CaptionStyleCompat(
                Color.WHITE,
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                CaptionStyleCompat.EDGE_TYPE_OUTLINE,
                Color.BLACK,
                typeface
            )
        )
        player?.addTextOutput { cues ->
            Log.e("cues are ", cues.toString())
            if (subtitleView != null) {
                subtitleView!!.onCues(cues)
            }
        }

        subtitleButton?.setOnClickListener {
            val contentView = LinearLayout(context)
            contentView.orientation = LinearLayout.VERTICAL
            contentView.setBackgroundColor(Color.WHITE)
            contentView.layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val adapter = MyAdapter<String>(
                context,
                R.layout.layout_subtitle_popup_item,
                MediaInfo.MyHolder::class.java
            )

            var temp = false
            if (player?.getCurrentMedia() != null && player?.getCurrentMedia()?.subtitles != null) {
                temp = true
                for (item in player?.getCurrentMedia()?.subtitles!!) {
                    val loc = Locale(item.language.toString())
                    val name: String = loc.getDisplayLanguage(loc)
                    adapter.add(name)
                }
                adapter.add("غیرفعال")
                adapter.setObjects(
                    if (subtitleView!!.visibility != View.VISIBLE) adapter.count - 1
                    else player?.getCurrentMedia()?.currentSubtitle, typeface
                )
            }

            val popup = PopupWindow(
                contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            for (i in 0 until adapter.count) {
                val v = adapter.getView(i, null, contentView)
                v.setOnClickListener {
                    val x = contentView.indexOfChild(v)
                    if (x == adapter.count - 1) {
                        subtitleView!!.visibility = View.GONE
                        subtitleButton?.isSelected = true
                    } else {
                        subtitleView!!.visibility = View.VISIBLE
                        subtitleButton?.isSelected = false
                        if (player?.getCurrentMedia()?.currentSubtitle != x) {
                            player?.getCurrentMedia()?.currentSubtitle = x
                            player?.notifyMediaChange()
                        }
                    }
                    popup.dismiss()
                }
                contentView.addView(v)
            }
            popup.setOnDismissListener {
                playerView.controllerShowTimeoutMs = showControllerTimeout
            }

            if (temp) {
                popup.showAsDropDown(
                    it,
                    0,
                    -(it.height + (72 * adapter.count)),
                    Gravity.CENTER_HORIZONTAL
                )
                playerView.controllerShowTimeoutMs = 0
            }
        }
    }


    private fun handleVideoUI(hasVideo: Boolean?) {
        Log.d(TAG, "handleVideoUI: $hasVideo")
        if (hasVideo == false) {
            descriptionTextView?.visibility =
                if (showDescriptionOnAudio) GONE else VISIBLE
            visualizer.visibility = if (showVisualizer) VISIBLE else GONE
            playerView.controllerShowTimeoutMs = 0
            playerView.controllerHideOnTouch = false

        } else {
            descriptionTextView?.visibility = View.VISIBLE
            visualizer.visibility = View.GONE
            playerView.controllerShowTimeoutMs = showControllerTimeout
        }
    }


    fun setMarginBottom(value: Int) {
        val viewGroup = view.findViewById<ViewGroup>(R.id.exo_controller_bottom_view)
        (viewGroup.layoutParams as RelativeLayout.LayoutParams).bottomMargin = value
    }


    private fun setData() {
        val item = player?.getCurrentMedia() ?: return
        Log.d(TAG, "setData: $item")
        titleTextView?.text = item.title
        descriptionTextView?.text = item.description
        if (item.description != null && item.hasVideo == true) {
            descriptionTextView?.visibility = View.VISIBLE
        } else {
            descriptionTextView?.visibility =
                if (showDescriptionOnAudio) GONE else VISIBLE
        }
        handleVideoUI(item.hasVideo)
        if (item.logoUrl != null) {
            Glide.with(context).load(item.logoUrl)
                .into(object : DrawableImageViewTarget(avatarImageView) {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        Log.d(TAG, "onResourceReady: ready!")
                        super.onResourceReady(resource, transition)
                        avatarImageView.visibility = View.VISIBLE
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        avatarImageView.visibility = View.GONE
                    }
                })
        } else {
            avatarImageView.visibility = View.GONE
        }
        if (item.backgroundUrl != null && item.hasVideo == false) {
            Glide.with(context).load(item.backgroundUrl).into(backImageView!!)
            backImageView?.visibility = View.VISIBLE
        }
        if (item.getQualityList().size > 1) {
            qualityButton.visibility = View.VISIBLE
            qualityButton.setImageDrawable(TextDrawable(resources, item.currentQuality))
        } else {
            qualityButton.visibility = View.GONE
        }

        if (item.subtitles == null || item.subtitles!!.size == 0) {
            subtitleButton?.visibility = View.GONE
        } else {
            subtitleButton?.visibility = View.VISIBLE
        }
    }

    fun showController() {
        playerView.showController()

    }

    fun hideController() {
        playerView.hideController()
    }

    private val adPlayerEventListener = object : Player.EventListener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (::adPlayerInterface.isInitialized) adPlayerInterface.onIsPlayingChanged(isPlaying)
            if (isPlaying) {
                calculateMaxAdTime()
                skippTimeButton.visibility = VISIBLE
                updateAdTime()
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)
            if (::adPlayerInterface.isInitialized) adPlayerInterface.onPlayerStateChanged(
                playWhenReady,
                playbackState
            )
            if (playWhenReady && playbackState == Player.STATE_READY) {
                calculateMaxAdTime()
            }
            if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                adPlayer?.let { safeAdPlayer ->
                    if (::adPlayerInterface.isInitialized) adPlayerInterface.onSeenAdToEnd((safeAdPlayer.duration / 1e3).toInt())
                }
                closeAdAndPlayMainVideo()
            }
        }

        override fun onPositionDiscontinuity(reason: Int) {
            super.onPositionDiscontinuity(reason)
            if (::adPlayerInterface.isInitialized) adPlayerInterface.onPositionDiscontinuity(reason)
            if (reason == Player.DISCONTINUITY_REASON_PERIOD_TRANSITION) {
                adPlayer?.let { safeAdPlayer ->
                    if (::adPlayerInterface.isInitialized) adPlayerInterface.onSeenAdToEnd((safeAdPlayer.duration / 1e3).toInt())
                }
                closeAdAndPlayMainVideo()
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            super.onPlayerError(error)
            if (::adPlayerInterface.isInitialized) adPlayerInterface.onPlayerError(error)
        }

        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)
            if (::adPlayerInterface.isInitialized) adPlayerInterface.onPlaybackStateChanged(state)
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
            if (::adPlayerInterface.isInitialized) adPlayerInterface.onPlayWhenReadyChanged(
                playWhenReady,
                reason
            )
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
            super.onTracksChanged(trackGroups, trackSelections)
            if (::adPlayerInterface.isInitialized) adPlayerInterface.onTracksChanged(
                trackGroups,
                trackSelections
            )
        }
    }

    private fun calculateMaxAdTime() {
        player?.let { safePlayer ->
            adPlayer?.let { safeAdPlayer ->
                adTimeToSkippS = if (safePlayer.getCurrentMedia()?.adTimeToSkipp == 0) {
                    10
                } else {
                    min(
                        safeAdPlayer.duration.toInt() / 1000,
                        safePlayer.getCurrentMedia()?.adTimeToSkipp!!
                    )
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateAdTime() {
        adPlayer?.let { safeAdPlayer ->
            val timeToEndAd = (adTimeToSkippS - safeAdPlayer.currentPosition / 1000).toInt()
            if (timeToEndAd > 0) {
                skippTimeButton.text = "$SKIPP_TEXT (${timeToEndAd})"
            } else {
                if (this::adPlayerInterface.isInitialized) adPlayerInterface.onFinishForceAdvertisingTime()
                skippTimeButton.text = SKIPP_TEXT
                skippTimeButton.requestFocus()
                adCanSkipp = true
            }
            Handler(context.mainLooper).postDelayed({
                if (timeToEndAd > 0) updateAdTime()
            }, 1000)
        }
    }

    private fun closeAdAndPlayMainVideo() {
        adPlayerView.visibility = GONE
        adPlayerView.player = null
        adPlayer?.release()
        player?.let { safePlayer ->
            if (!safePlayer.isPlaying) safePlayer.start(getCurrentMediaIndex())
        }
    }

    private fun getCurrentMediaIndex(): Int {
        var index = 0
        player?.let { safePlayer ->
            safePlayer.mediaInfoes.forEachIndexed { i, mediaInfo ->
                if (mediaInfo.id.equals(playerCurrentMediaId)) index = i
            }
        }
        return index

    }

    fun playVideo(currentIndex: Int = 0) {
        player?.let { safePlayer ->
            if (!safePlayer.getCurrentMedia()?.mediaAd?.isEmpty()!!) {
                safePlayer.getCurrentMedia()?.id?.let {
                    playerCurrentMediaId = it
                }
                adPlayerView.visibility = VISIBLE
                adPlayer?.addMedia(
                    0, MediaInfo().addMediaQuality(
                        "",
                        safePlayer.getCurrentMedia()?.mediaAd!!
                    )
                )
                adPlayer?.start(0)
            } else {
                adPlayerView.visibility = GONE
                safePlayer.start(currentIndex)
                Handler(Looper.getMainLooper()).postDelayed({
                    playerView.requestFocus()
                }, 1000)
            }
        }
    }

    fun addAdPlayerListener(adPlayer: AdPlayer) {
        adPlayerInterface = adPlayer
    }
}