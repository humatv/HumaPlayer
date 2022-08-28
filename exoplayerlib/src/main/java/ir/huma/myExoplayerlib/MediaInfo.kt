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
    var id: String? = null
    var title: String? = null
    var description: String? = null
    var logoUrl: String? = null
    var index: Int = 0
    var backgroundUrl: String? = null
    var mediaQualities = LinkedHashMap<String, Uri>()
    var mediaAd: String = ""
//    var adTime: Int = 0
    var subtitles: ArrayList<MediaItem.Subtitle>? = null
    var seek: Long = 0
    var hasVideo: Boolean? = null
    var tag: Any? = null

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
    var isLive: Boolean = false
    var currentQuality: String? = null
        private set
        get() {
            if (field == null) if (mediaQualities != null && mediaQualities.size > 0) {
                return mediaQualities.keys.toList().get(0)
            } else {
                return ""
            }
            else return field
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

    fun setSeek(seek: Long): MediaInfo {
        this.seek = seek
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

    fun setId(id: String): MediaInfo {
        this.id = id
        return this
    }

    fun setHasVideo(hasVideo: Boolean): MediaInfo {
        this.hasVideo = hasVideo
        return this
    }

    fun setTag(tag: Any): MediaInfo {
        this.tag = tag
        return this
    }


    override fun equals(other: Any?): Boolean {
        if (!(other is MediaInfo)) {
            return false
        }
        if (other.id != null && id != null && other.id == id) {
            return true
        }
        if (other.mediaQualities != null && mediaQualities != null && other.mediaQualities.size == mediaQualities.size) {
            if (other.mediaQualities.values.toList().get(0) == mediaQualities.values.toList()
                    .get(0)
            ) {
                return true
            }
        }
        return super.equals(other)
    }

    fun addSubtitle(subtitleUrl: String, language: String): MediaInfo {
        val sub = MediaItem.Subtitle(
            Uri.parse(subtitleUrl),
            MimeTypes.TEXT_VTT,
            language,
            Format.NO_VALUE
        )
        if (subtitles == null) subtitles = ArrayList()
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
                mediaItem = mediaItemBuilder.setUri(
                    if (mediaQualities.get(quality) == null) Uri.EMPTY else mediaQualities.get(
                        quality
                    )
                )
                    .build()
                currentQuality = quality;
            } else if (mediaItem == null) {
                mediaItem = mediaItemBuilder.setUri(
                    if (mediaQualities.get(currentQuality) == null) Uri.EMPTY else mediaQualities.get(
                        currentQuality
                    )
                )
                    .build()
            }
        }

        return mediaItem;
    }

    fun isMediaLocal(quality: String?): Boolean {
        try {
            if (mediaQualities.containsKey(quality)) {
                if (Uri.parse(mediaQualities.get(quality)?.toString()).scheme == "file") return true
            } else {
                if (Uri.parse(
                        mediaQualities.get(currentQuality)?.toString()
                    ).scheme == "file"
                ) return true
            }
        } catch (e: Exception) {

        }
        return false
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

    fun setMediaAdLink(uri: String): MediaInfo {
        mediaAd = uri
        return this
    }

//    fun setMediaAdTime(timeS: Int): MediaInfo {
//        adTime = timeS
//        return this
//    }

    fun getCurrentSubtitle(): MediaItem.Subtitle? {
        if (subtitles != null && currentSubtitle != null) {
            return subtitles!!.get(currentSubtitle!!)
        }
        return null;
    }

    class MyHolder(context: Context?, view: View?, arrayAdapter: ArrayAdapter<String>?) :
        MyViewHolder<String>(context, view, arrayAdapter) {


        override fun fill(t: String?, pos: Int) {
            val textView = view.findViewById<TextView>(R.id.textView)
            textView.setText(t)

            if (objects != null && objects.size != 0) {
                if (pos == objects[0]) {
                    view.findViewById<ImageView>(R.id.imageView).visibility = View.VISIBLE
                }
                if (objects.size > 1) textView.setTypeface(objects[1] as Typeface?)
            }
        }
    }

}