package ir.huma.myExoplayerlib

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes
import ir.huma.myExoplayerlib.util.MyViewHolder

class MediaInfo {
    var title: String? = null
    var description: String? = null
    var logoUrl: String? = null
    var backgroundUrl: String? = null
    var mediaQualities = LinkedHashMap<String, Uri>()
    var subtitles: ArrayList<MediaItem.Subtitle>? = null
    var currentSubtitle: Int? = null
        set(value) {
            if (value == null) {
                field = null
            } else if (subtitles == null) {
                field = 0
            } else if (value >= subtitles!!.size) {
                field = subtitles!!.size - 1
            } else if (value < 0) {
                field = -1
            } else {
                field = value
            }
        }
    var isLive : Boolean = false
    var currentQuality: String? = null
        private set
        get() {
            if (field == null)
                return mediaQualities.keys.toList().get(0)
            else
                return field
        }
    var mediaItemBuilder: MediaItem.Builder = MediaItem.Builder()
        private set
    private var mediaItem: MediaItem? = null

    fun setTitle(title: String?): MediaInfo {
        this.title = title
        return this
    }

    fun setDescription(description: String?): MediaInfo {
        this.description = description
        return this
    }

    fun setLogoUrl(logoUrl: String?): MediaInfo {
        this.logoUrl = logoUrl
        return this
    }

    fun setBackgroundUrl(backgroundUrl: String?): MediaInfo {
        this.backgroundUrl = backgroundUrl
        return this
    }

    fun setCurrentSubtitle(currentSubtitle: Int): MediaInfo {
        this.currentSubtitle = currentSubtitle
        return this
    }

    fun setCurrentQuality(current: String): MediaInfo {
        currentQuality = current
        return this
    }


    fun setLive(live: Boolean): MediaInfo {
        isLive = live
        return this
    }

    fun addSubtitle(subtitleUrl: String, language: String): MediaInfo {
        val sub = MediaItem.Subtitle(Uri.parse(subtitleUrl), MimeTypes.APPLICATION_SUBRIP, language, Format.NO_VALUE)
        if (subtitles == null)
            subtitles = ArrayList()
        subtitles!!.add(sub)
        if (currentSubtitle == null) {
            currentSubtitle = 0
        }
        return this
    }

    fun getQualityList(): List<String> {
        if (mediaQualities != null) {
            return mediaQualities.keys.toList()
        }
        return return ArrayList<String>()
    }

    fun getMediaItem(quality: String?): MediaItem? {
        if (quality == null) {
            return getMediaItem()
        }
        if (mediaItem == null || currentQuality != quality) {
            if (mediaQualities.containsKey(quality)) {
                mediaItem = mediaItemBuilder.setUri(mediaQualities.get(quality)).build()
                currentQuality = quality;
            } else if (mediaItem == null) {
                mediaItem = mediaItemBuilder.setUri(mediaQualities.get(currentQuality)).build()
            }
        }

        return mediaItem;
    }

    fun getMediaItem(): MediaItem? {
        if (mediaItem == null) {
            if (mediaQualities.containsKey(currentQuality)) {
                mediaItem = mediaItemBuilder.setUri(mediaQualities.get(currentQuality)).build()
            } else if (mediaQualities.size > 0) {
                mediaItem = mediaItemBuilder.setUri(mediaQualities.values.toList().get(0)).build()
                currentQuality = mediaQualities.keys.toList().get(0)
            }
        }
        return mediaItem;
    }

    fun getNextQuality(quality: String): String? {
        var index = mediaQualities.keys.indexOf(quality)
        if (index + 1 >= mediaQualities.size) {
            index = 0;
        } else {
            index++;
        }
        return mediaQualities.keys.toList().get(index)
    }

    fun addMediaQuality(quality: String, uri: String): MediaInfo {
        mediaQualities.put(quality, Uri.parse(uri))
        return this
    }

    fun getCurrentSubtitle(): MediaItem.Subtitle? {
        if (subtitles != null && currentSubtitle != null) {
            return subtitles!!.get(currentSubtitle!!)
        }
        return null;
    }

    public class MyHolder(context: Context?, view: View?, arrayAdapter: ArrayAdapter<String>?) : MyViewHolder<String>(context, view, arrayAdapter) {
//        override fun fill(t: MediaItem.Subtitle?, pos: Int) {
//
//        }

        override fun fill(t: String?, pos: Int) {
//            val loc = Locale(t!!.language)
//            val name: String = loc.getDisplayLanguage(loc)
            val textView = view.findViewById<TextView>(R.id.textView)
            textView.setText(t)

            if (objects != null && objects.size != 0) {
                if (pos == objects[0]) {
                    view.findViewById<ImageView>(R.id.imageView).visibility = View.VISIBLE
                }
                if(objects.size >1)
                textView.setTypeface(objects[1] as Typeface?)
            }
        }
    }

}