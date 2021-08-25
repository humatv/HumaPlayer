package ir.huma.humaleanbackvideolib

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import ir.huma.myExoplayerlib.MediaInfo
import ir.huma.myExoplayerlib.MyExoPlayerManager
import java.io.File


class MainActivity : FragmentActivity() {
    lateinit var myExoPlayer: MyExoPlayerManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !== PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.RECORD_AUDIO), 102)
        }

        myExoPlayer = findViewById(R.id.myExoPlayer)
        var rootFile = File(Environment.getExternalStorageDirectory(), "HumaFit");
//        myExoPlayer.addMediaItem(MediaInfo().addMediaQuality("", "file://"+rootFile.absoluteFile+"/285ccb9c-e2be-4ee8-97fc-64cc5fc27f3a.mp4"))
//
        myExoPlayer.addMediaItem(
                MediaInfo().setTitle("تهران عاشق")
                        .setLogoUrl("https://music-fa.com/wp-content/uploads/2020/12/Mohammad-Motamedi-Tehrane-Ashegh-Cover-Music-fa.com_-1.jpg")
                        .setBackgroundUrl("https://music-fa.com/wp-content/uploads/2020/12/Mohammad-Motamedi-Tehrane-Ashegh-Cover-Music-fa.com_-1.jpg")
                        .setDescription("آلبوم تهران عاشق محمد معتمدی به صورت تکی و یکجا")
                        .addMediaQuality("128", "https://dls.music-fa.com/tagdl/99/Mohammad%20Motamedi%20-%20Gole%20Sang%20(320).mp3")
        )

        myExoPlayer.addMediaItem(MediaInfo().setTitle("دیرین دیرین").setSeek(20000)
                .setDescription(
                        "https://www.forbes.com/sites/laurashin/2017/02/21/this-is-the-worlds-first-cryptocurrency-issued-by-a-hedge-fund/#6de7a9b560b6\n" +
                                "\n" +
                                "https://futurism.com/a-new-startup-just-proved-that-blockchain-is-going-to-utterly-transform-osadd\n\n"
                )
                .setLogoUrl("https://cdn.isna.ir/d/2017/05/03/3/57478643.jpg?ts=1498045331768")
                .addMediaQuality("144p", "https://as9.cdn.asset.aparat.com/aparat-video/ab6d4bed0e8142d8a2648dc7cce4af6727728814-144p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6ImM4N2JiY2JhYjgxNDk2YWFkNWJhYWM4OGRmZTU2N2M3IiwiZXhwIjoxNjA3NTE3NTM5LCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.QpofxWAV4uwu0UC8liXbeVRNgv1cyTO9vwjojP9Vt64")
                .addMediaQuality("240p", "https://as4.cdn.asset.aparat.com/aparat-video/ab6d4bed0e8142d8a2648dc7cce4af6727728814-240p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6ImIwZDUyYjBjYzcxZGUwYTdjYTk4MzlkMmFjNTE0OTA5IiwiZXhwIjoxNjA3NTM3Mjk5LCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.t8FENFALXpjBewE6UVuIWj_abEmsPAu9OlT0kmAUZ3o")
                .addMediaQuality("360p", "https://as4.cdn.asset.aparat.com/aparat-video/ab6d4bed0e8142d8a2648dc7cce4af6727728814-360p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6ImIwZDUyYjBjYzcxZGUwYTdjYTk4MzlkMmFjNTE0OTA5IiwiZXhwIjoxNjA3NTM3Mjk5LCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.t8FENFALXpjBewE6UVuIWj_abEmsPAu9OlT0kmAUZ3o")
                .addMediaQuality("480p", "https://as9.cdn.asset.aparat.com/aparat-video/ab6d4bed0e8142d8a2648dc7cce4af6727728814-480p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6Ijg0NTU0ZjM3NWUwYTBkZjlhMDQ3MDg3NDRiNjcwY2Q3IiwiZXhwIjoxNjA3NTI3MjIxLCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.VDqPiZI6DFwopLQFJ8sxJ9lD036YoqlBSKnx0Zv8yXU")
//                .addSubtitle("https://cloudspace.huma.ir/s/mrziWnyAM4sEimw/download","en")
//                .addSubtitle("https://cloudspace.huma.ir/s/68q8tKu1I9XTiXg/download", "fa")
//                .addSubtitle("https://cloudspace.huma.ir/s/ruRcEJ12X3806Df/download", "en")
        )
        myExoPlayer.addMediaItem(MediaInfo().setSeek(10000).setTitle("طرز تهیه نان سیمیت")
                .setDescription("آرد، ۵۰۰ گرم\n" +
                        "خمیرمایه فوری، ۱ قاشق چای\u200Cخوری\n" +
                        "شکر، ۱ قاشق غذاخوری\n" +
                        "روغن مایع، ۳ قاشق غذاخوری\n" +
                        "شیر ولرم، ۳ قاشق غذاخوری\n" +
                        "آب ولرم، ۱/۴ پیمانه\n" +
                        "تخم\u200Cمرغ، ۱ عدد\n" +
                        "نمک، ۲ پنس\n" +
                        "شیره خرما، ۴ قاشق غذاخوری\n" +
                        "آب، ۳ قاشق غذاخوری    ")

                .addMediaQuality("144p", "https://hw18.cdn.asset.aparat.com/aparat-video/4ed9428eeb3ee5cde21cc2878860215627721511-144p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6IjBjYWJhN2NlYTE2YjY5MmYzMGZmMjU4MWNlYzE3NGYxIiwiZXhwIjoxNjA3NTM3NTAwLCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.0CX-RXwDPnd8bsL1X3JzWBVoUuI-Ai-xbOswffh6CMg")
                .addMediaQuality("240p", "https://hw18.cdn.asset.aparat.com/aparat-video/4ed9428eeb3ee5cde21cc2878860215627721511-240p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6IjBjYWJhN2NlYTE2YjY5MmYzMGZmMjU4MWNlYzE3NGYxIiwiZXhwIjoxNjA3NTM3NTAwLCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.0CX-RXwDPnd8bsL1X3JzWBVoUuI-Ai-xbOswffh6CMg")
                .addMediaQuality("360p", "https://hw18.cdn.asset.aparat.com/aparat-video/4ed9428eeb3ee5cde21cc2878860215627721511-360p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6IjBjYWJhN2NlYTE2YjY5MmYzMGZmMjU4MWNlYzE3NGYxIiwiZXhwIjoxNjA3NTM3NTAwLCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.0CX-RXwDPnd8bsL1X3JzWBVoUuI-Ai-xbOswffh6CMg")
                .addMediaQuality("480p", "https://hw18.cdn.asset.aparat.com/aparat-video/4ed9428eeb3ee5cde21cc2878860215627721511-480p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6IjBjYWJhN2NlYTE2YjY5MmYzMGZmMjU4MWNlYzE3NGYxIiwiZXhwIjoxNjA3NTM3NTAwLCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.0CX-RXwDPnd8bsL1X3JzWBVoUuI-Ai-xbOswffh6CMg")
                .addMediaQuality("720p", "https://hw18.cdn.asset.aparat.com/aparat-video/4ed9428eeb3ee5cde21cc2878860215627721511-720p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6IjBjYWJhN2NlYTE2YjY5MmYzMGZmMjU4MWNlYzE3NGYxIiwiZXhwIjoxNjA3NTM3NTAwLCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.0CX-RXwDPnd8bsL1X3JzWBVoUuI-Ai-xbOswffh6CMg")
                .setCurrentQuality("360p")
        )
        myExoPlayer.addMediaItem(
                MediaInfo().setTitle("تست پخش زنده").setLive(true).addMediaQuality("", "http://demo.unified-streaming.com/video/tears-of-steel/tears-of-steel.ism/.m3u8")
        )


        myExoPlayer.typeface = Typeface.createFromAsset(assets, "fonts/BYekan.ttf")
        myExoPlayer.start()
    }

    override fun onPause() {
        super.onPause()
        myExoPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        myExoPlayer.release()
    }

}