package ir.huma.myExoplayerlib

import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray

interface AdPlayer {

    fun onIsPlayingChanged(isPlaying: Boolean) {}
    fun onPlayerError(error: ExoPlaybackException) {}
    fun onPositionDiscontinuity(reason: Int) {}
    fun onPlayerStateChanged(playWhenReady: Boolean?, playbackState: Int?) {}
    fun onPlaybackStateChanged(state: Int) {}
    fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {}
    fun onTracksChanged(
        trackGroups: TrackGroupArray,
        trackSelections: TrackSelectionArray
    ) {
    }

    fun onSkippButtonClickListener() {}
    fun onFinishForceAdvertisingTime() {}
    fun onSeenAdToEnd(duration: Int) {}
}