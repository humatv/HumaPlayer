package ir.huma.humaleanbackvideolib

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View.VISIBLE
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.exoplayer2.ExoPlaybackException
import ir.huma.myExoplayerlib.AdPlayer
import ir.huma.myExoplayerlib.HumaExoPlayer
import ir.huma.myExoplayerlib.HumaExoPlayerView
import ir.huma.myExoplayerlib.MediaInfo
import java.io.File


class MainActivity : FragmentActivity() {
    lateinit var playerView: HumaExoPlayerView
    lateinit var player: HumaExoPlayer

    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.RECORD_AUDIO),
                102
            )
        }

        playerView = findViewById(R.id.myExoPlayer)
        player = HumaExoPlayer(this)
        playerView.showDescriptionOnAudio = true
        playerView.showVisualizer = false
        playerView.player = player
        var rootFile = File(Environment.getExternalStorageDirectory(), "HumaFit");
//        myExoPlayer.addMediaItem(MediaInfo().addMediaQuality("", "file://"+rootFile.absoluteFile+"/285ccb9c-e2be-4ee8-97fc-64cc5fc27f3a.mp4"))
//

//        Glide.with(this)
//            .load("https://music-fa.com/wp-content/uploads/2020/12/Mohammad-Motamedi-Tehrane-Ashegh-Cover-Music-fa.com_-1.jpg")
//            .into(playerView.backImageView!!)
        playerView.backImageView?.visibility = VISIBLE

//        player.addMedia(
//            MediaInfo().setTitle("تست پخش زنده").setLive(true).addMediaQuality(
//                "",
//                "https://onlines.uptvs.com/stream/movie/Day_Shift_UPTV.co_Dub/720p.m3u8"
//            )
//        )
        player.addMedia(
            MediaInfo().setTitle("دیرین دیرین")
                .setLogoUrl("http://shirintanz.ir/wp-content/uploads/2018/06/%D9%88%DB%8C-%D8%AF%DB%8C%D8%B1%DB%8C%D9%86-%D8%AF%DB%8C%D8%B1%DB%8C%D9%86.png")
//                .setBackgroundUrl("https://music-fa.com/wp-content/uploads/2020/12/Mohammad-Motamedi-Tehrane-Ashegh-Cover-Music-fa.com_-1.jpg")
                .setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
                .addMediaQuality(
                    "480",
                    "https://hajifirouz5.asset.aparat.com/aparat-video/57de0422a208be124be5d2dd2a4c8e9942530607-720p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6ImY1MjY5MGM1YzZlYjA0YTI5NWRkMzA2Mzc1Mjk5ODQ3IiwiZXhwIjoxNjYyODk0MjA2LCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.mSuSnt4FFDkuaFxXhwYfac0ysdB267GjIq-6BWhYk3c"
                )
                .setMediaAdLink("https://persian4.asset.aparat.com/aparat-video/6f44a7a05130f05aeccb4e905cf1ad3f45695174-720p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6IjQ0ZjI2ZDBmNjAzZWUzYTJmNTAwYzBjZjg4Yjc1NTc2IiwiZXhwIjoxNjYyODk0MTMwLCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.Nfu0ySYGzVxUZbpUtQXiLzcTQOpZszYDex3TumMH6NU")
                .setTimeToSkipp(8)
        )

        player.addMedia(
            MediaInfo().setTitle("دیرین دیرین").setSeek(20000)
                .setDescription(
                    "https://www.forbes.com/sites/laurashin/2017/02/21/this-is-the-worlds-first-cryptocurrency-issued-by-a-hedge-fund/#6de7a9b560b6\n" +
                            "\n" +
                            "https://futurism.com/a-new-startup-just-proved-that-blockchain-is-going-to-utterly-transform-osadd\n\n"
                )
                .setLogoUrl("https://cdn.isna.ir/d/2017/05/03/3/57478643.jpg?ts=1498045331768")
                .addMediaQuality(
                    "144p",
                    "https://hajifirouz5.cdn.asset.aparat.com/aparat-video/f1b3edd1ce4b45fcc9f274afb35c673147236048-480p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6Ijg2YWIwY2Q4MWQ0NDQ1NmZmYTE4ZWM1MWVlOWY4YjIyIiwiZXhwIjoxNjYxNjI1Mjc0LCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.zZIti5f6of3ejK7JHihwMnQIqx4vGm5QZ6Mnnhp42NU"
                )

                .addMediaQuality(
                    "240p",
                    "https://as4.cdn.asset.aparat.com/aparat-video/ab6d4bed0e8142d8a2648dc7cce4af6727728814-240p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6ImIwZDUyYjBjYzcxZGUwYTdjYTk4MzlkMmFjNTE0OTA5IiwiZXhwIjoxNjA3NTM3Mjk5LCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.t8FENFALXpjBewE6UVuIWj_abEmsPAu9OlT0kmAUZ3o"
                )
                .addMediaQuality(
                    "360p",
                    "https://as4.cdn.asset.aparat.com/aparat-video/ab6d4bed0e8142d8a2648dc7cce4af6727728814-360p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6ImIwZDUyYjBjYzcxZGUwYTdjYTk4MzlkMmFjNTE0OTA5IiwiZXhwIjoxNjA3NTM3Mjk5LCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.t8FENFALXpjBewE6UVuIWj_abEmsPAu9OlT0kmAUZ3o"
                )
                .addMediaQuality(
                    "480p",
                    "https://as9.cdn.asset.aparat.com/aparat-video/ab6d4bed0e8142d8a2648dc7cce4af6727728814-480p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6Ijg0NTU0ZjM3NWUwYTBkZjlhMDQ3MDg3NDRiNjcwY2Q3IiwiZXhwIjoxNjA3NTI3MjIxLCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.VDqPiZI6DFwopLQFJ8sxJ9lD036YoqlBSKnx0Zv8yXU"
                )
                .setMediaAdLink("https://dms.licdn.com/playlist/C4D05AQHcxSP518psyw/feedshare-ambry-analyzed_servable_progressive_video/0/1661530843805?e=2147483647&v=beta&t=6z_Mfhn_0PsqgKWd-70R8TUBEEymcbUcSY8RZD3wfuQ")
//                .setMediaAdTime(10)

//                .addSubtitle("https://cloudspace.huma.ir/s/mrziWnyAM4sEimw/download","en")
//                .addSubtitle("https://cloudspace.huma.ir/s/68q8tKu1I9XTiXg/download", "fa")
//                .addSubtitle("https://cloudspace.huma.ir/s/ruRcEJ12X3806Df/download", "en")
        )
        player.addMedia(
            MediaInfo().setSeek(10000).setTitle("طرز تهیه نان سیمیت")
                .setDescription(
                    "آرد، ۵۰۰ گرم\n" +
                            "خمیرمایه فوری، ۱ قاشق چای\u200Cخوری\n" +
                            "شکر، ۱ قاشق غذاخوری\n" +
                            "روغن مایع، ۳ قاشق غذاخوری\n" +
                            "شیر ولرم، ۳ قاشق غذاخوری\n" +
                            "آب ولرم، ۱/۴ پیمانه\n" +
                            "تخم\u200Cمرغ، ۱ عدد\n" +
                            "نمک، ۲ پنس\n" +
                            "شیره خرما، ۴ قاشق غذاخوری\n" +
                            "آب، ۳ قاشق غذاخوری    "
                )

                .addMediaQuality(
                    "144p",
                    "https://hw18.cdn.asset.aparat.com/aparat-video/4ed9428eeb3ee5cde21cc2878860215627721511-144p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6IjBjYWJhN2NlYTE2YjY5MmYzMGZmMjU4MWNlYzE3NGYxIiwiZXhwIjoxNjA3NTM3NTAwLCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.0CX-RXwDPnd8bsL1X3JzWBVoUuI-Ai-xbOswffh6CMg"
                )
                .addMediaQuality(
                    "240p",
                    "https://hw18.cdn.asset.aparat.com/aparat-video/4ed9428eeb3ee5cde21cc2878860215627721511-240p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6IjBjYWJhN2NlYTE2YjY5MmYzMGZmMjU4MWNlYzE3NGYxIiwiZXhwIjoxNjA3NTM3NTAwLCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.0CX-RXwDPnd8bsL1X3JzWBVoUuI-Ai-xbOswffh6CMg"
                )
                .addMediaQuality(
                    "360p",
                    "https://hw18.cdn.asset.aparat.com/aparat-video/4ed9428eeb3ee5cde21cc2878860215627721511-360p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6IjBjYWJhN2NlYTE2YjY5MmYzMGZmMjU4MWNlYzE3NGYxIiwiZXhwIjoxNjA3NTM3NTAwLCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.0CX-RXwDPnd8bsL1X3JzWBVoUuI-Ai-xbOswffh6CMg"
                )
                .addMediaQuality(
                    "480p",
                    "https://hw18.cdn.asset.aparat.com/aparat-video/4ed9428eeb3ee5cde21cc2878860215627721511-480p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6IjBjYWJhN2NlYTE2YjY5MmYzMGZmMjU4MWNlYzE3NGYxIiwiZXhwIjoxNjA3NTM3NTAwLCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.0CX-RXwDPnd8bsL1X3JzWBVoUuI-Ai-xbOswffh6CMg"
                )
                .addMediaQuality(
                    "720p",
                    "https://hw18.cdn.asset.aparat.com/aparat-video/4ed9428eeb3ee5cde21cc2878860215627721511-720p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6IjBjYWJhN2NlYTE2YjY5MmYzMGZmMjU4MWNlYzE3NGYxIiwiZXhwIjoxNjA3NTM3NTAwLCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.0CX-RXwDPnd8bsL1X3JzWBVoUuI-Ai-xbOswffh6CMg"
                )
                .setCurrentQuality("360p")
        )
        player.addMedia(
            MediaInfo().setTitle("تست پخش زنده").setLive(true).addMediaQuality(
                "",
                "http://demo.unified-streaming.com/video/tears-of-steel/tears-of-steel.ism/.m3u8"
            )
        )


        playerView.typeface = Typeface.createFromAsset(assets, "fonts/BYekan.ttf")
//        player.start()

        playerView.addAdPlayerListener(adPlayerEventListener)
        playerView.playVideo()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    private val adPlayerEventListener = object : AdPlayer {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            Log.d(TAG, "onIsPlayingChanged: isPlaying $isPlaying")
        }

        override fun onPlayerError(error: ExoPlaybackException) {
        }

        override fun onPositionDiscontinuity(reason: Int) {
        }

    }
}