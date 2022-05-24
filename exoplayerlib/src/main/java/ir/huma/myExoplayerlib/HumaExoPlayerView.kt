package ir.huma.myExoplayerlib

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.chibde.visualizer.LineBarVisualizer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioListener
import com.google.android.exoplayer2.text.CaptionStyleCompat
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SubtitleView
import ir.huma.myExoplayerlib.util.MyAdapter
import ir.huma.myExoplayerlib.util.MyPlayerView
import ir.huma.myExoplayerlib.util.TextDrawable
import java.util.*

class HumaExoPlayerView : FrameLayout {
    private val TAG = "HumaExoPlayerView"

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var showVisualizerInsteadOfDescription = true
    var showControllerTimeout = 4000

    var typeface: Typeface? = null
        set(value) {
            field = value
            titleTextView.typeface = value
            descriptionTextView.typeface = value
            view.findViewById<TextView>(R.id.exo_duration).typeface = value
            view.findViewById<TextView>(R.id.exo_position).typeface = value
        }

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

    private lateinit var view: View
    private lateinit var visualizer: MyLineBarVisualizer
    private lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    private lateinit var avatarImageView: ImageView
    private lateinit var qualityButton: ImageView
    private lateinit var subtitleButton: ImageView
    lateinit var backImageView: ImageView
        private set


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
        subtitleButton = view.findViewById(R.id.exo_sub)
//        val trackSelector = DefaultTrackSelector(context!!)
//        trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd())

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
            Handler().postDelayed({
                setData()
            }, 1000)
        }

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
        Handler().postDelayed(object : Runnable {
            override fun run() {
                try {
                    visualizer.setPlayer(audioSessionId)
                } catch (e: Exception) {
//                    setAudioSession(audioSessionId)
                }
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

        subtitleButton.setOnClickListener {
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
                    val loc = Locale(item.language)
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
                        subtitleButton.isSelected = true
                    } else {
                        subtitleView!!.visibility = View.VISIBLE
                        subtitleButton.isSelected = false
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
        Log.d(TAG, "handleVideoUI: ${hasVideo}")
        if (hasVideo == false) {
            descriptionTextView.visibility =
                if (showVisualizerInsteadOfDescription) GONE else VISIBLE
            visualizer.visibility = if (showVisualizerInsteadOfDescription) VISIBLE else GONE
            playerView.controllerShowTimeoutMs = 0
            playerView.controllerHideOnTouch = false

        } else {
            descriptionTextView.visibility = View.VISIBLE
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
        Log.d(TAG, "setData: ${item}")
        titleTextView.text = item.title
        descriptionTextView.text = item.description
        if (item.description != null && item.hasVideo == true) {
            descriptionTextView.visibility = View.VISIBLE
        } else {
            descriptionTextView.visibility =
                if (showVisualizerInsteadOfDescription) GONE else VISIBLE
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
            Glide.with(context).load(item.backgroundUrl).into(backImageView)
            backImageView.visibility = View.VISIBLE
        } else {

        }
        if (item.getQualityList().size > 1) {
            qualityButton.visibility = View.VISIBLE
            qualityButton.setImageDrawable(TextDrawable(resources, item.currentQuality))
        } else {
            qualityButton.visibility = View.GONE
        }

        if (item.subtitles == null || item.subtitles!!.size == 0) {
            subtitleButton.visibility = View.GONE
        } else {
            subtitleButton.visibility = View.VISIBLE
        }
    }

    fun showController() {
        playerView.showController()
    }

    fun hideController() {
        playerView.hideController()
    }

}