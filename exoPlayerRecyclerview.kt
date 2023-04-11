package com.example.mysocialapp.adapters.homeFeedRecyclerView

import android.content.Context
import android.graphics.Point
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.mysocialapp.R
import com.example.mysocialapp.adapters.recyclerViewV4.ExoPlayerRecyclerView
import com.example.mysocialapp.models.PostDataModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.util.ArrayList
import java.util.Objects

class HomeFeedRecyclerview : RecyclerView {
    private var viewHolderParent: View? = null

    private var mediaContainer: FrameLayout? = null
    private var thumbnail: ImageView? = null
    private var progressBar1: ProgressBar? = null
    private var progressBar2: ProgressBar? = null
    private var volumeControl: ImageView? = null
    private var videoPlayer: ExoPlayer? = null

    private var mediaObjects = ArrayList<PostDataModel>()
    private var videoSurfaceDefaultHeight = 0
    private var screenDefaultHeight = 0
    private var myContext: Context? = null
    private var playPosition = -1
    private var requestManager: RequestManager? = null
    private var volumeState:VolumeState? = null
    private val videoViewClickListener = OnClickListener { toggleVolume() }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }
    private fun init(context: Context) {
        this.myContext = context.applicationContext
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    if (thumbnail != null) {
                        thumbnail!!.visibility = VISIBLE
                    }
                    if (!recyclerView.canScrollVertically(1)) {
                        playVideo(true)
                    } else {
                        playVideo(false)
                    }
                }
            }
        })
        addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {}
            override fun onChildViewDetachedFromWindow(view: View) {
                if (viewHolderParent != null && viewHolderParent == view) {
                }
            }
        })
    }
    fun playVideo(isEndOfList: Boolean) {
        val targetPosition: Int
        if (!isEndOfList) {
            val startPosition =
                (layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
            var endPosition =
                (layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()

            // if there is more than 2 list-items on the screen, set the difference to be 1
            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1
            }

            // something is wrong. return.
            if (startPosition < 0 || endPosition < 0) {
                return
            }

            // if there is more than 1 list-item on the screen
            targetPosition = if (startPosition != endPosition) {
                val startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition)
                val endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition)
                if (startPositionVideoHeight > endPositionVideoHeight) startPosition else endPosition
            } else {
                startPosition
            }
        } else {
            targetPosition = mediaObjects.size - 1
        }
        Log.d(ExoPlayerRecyclerView.TAG, "playVideo: target position: $targetPosition")

        // video is already playing so return
        if (targetPosition == this.playPosition) {
            return
        }

        // set the position of the list-item that is to be played
        this.playPosition = targetPosition
        val currentPosition = targetPosition - (Objects.requireNonNull(
            layoutManager
        ) as LinearLayoutManager).findFirstVisibleItemPosition()
        val child = getChildAt(currentPosition) ?: return
        val holder = child.tag as HomeFeedRecyclerViewHolder
        progressBar1 = holder.progressBar1
        progressBar2 = holder.progressBar2
        volumeControl = holder.volumeControl
        viewHolderParent = holder.itemView
        requestManager = holder.requestManager
        mediaContainer = holder.mediaContainer
        if (holder.playerView == null){
            try {
                if (this.videoPlayer!!.playWhenReady){
                    pauseCurrentPlayer()
                }
            }catch (e:NullPointerException){
                return
            }
            return
        }
        if (holder.playerView!!.player == null){
            try {
                if (this.videoPlayer!!.playWhenReady){
                    pauseCurrentPlayer()
                }
            }catch (e:NullPointerException){
                return
            }
            return
        }
        val display = (Objects.requireNonNull(
            context.getSystemService(Context.WINDOW_SERVICE)
        ) as WindowManager).defaultDisplay
        val point = Point()
        display.getSize(point)
        videoSurfaceDefaultHeight = point.x
        screenDefaultHeight = point.y
        holder.playerView!!.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        if (this.videoPlayer != null){
            try {
                if (this.videoPlayer!!.playWhenReady){
                    pauseCurrentPlayer()
                }
            }catch (e:NullPointerException){
                return
            }
        }
        this.videoPlayer = holder.playerView!!.player as ExoPlayer
        setVolumeControl(VolumeState.ON)
        if (holder.playerView!!.player != null){
            if (viewHolderParent != null){
                viewHolderParent!!.setOnClickListener(videoViewClickListener)
            }
            try {
                val mediaUrl = mediaObjects[targetPosition].videos
                if (mediaUrl != null) {
                    addPlayerListener(videoPlayer!!, holder)
                    videoPlayer!!.playWhenReady = true

                }else{
                    return
                }
            }catch (e:Exception){
                Log.d("HOME FEED LOG", e.toString())
                return
            }
        }else{
            return
        }
    }

    private fun addPlayerListener(exoPlayer: ExoPlayer, holder: HomeFeedRecyclerViewHolder) {
        exoPlayer.addListener(object :Player.Listener{
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        if (holder.progressBar1 != null || holder.progressBar2 != null) {
                            holder.progressBar1!!.visibility = View.VISIBLE
                            holder.progressBar2!!.visibility = View.VISIBLE
                        }
                    }
                    Player.STATE_ENDED -> {
                        exoPlayer.seekTo(0)
                    }
                    Player.STATE_IDLE -> {
                    }
                    Player.STATE_READY -> {
                        if (holder.progressBar1 != null || holder.progressBar2 != null) {
                            holder.progressBar1!!.visibility = View.GONE
                            holder.progressBar2!!.visibility = View.GONE
                        }
                    }
                    Player.COMMAND_PREPARE -> {
                        Log.d(TAG, "command prepare")
                    }
                }
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
            }
        })
    }

    private fun getVisibleVideoSurfaceHeight(playPosition: Int): Int {
        val at = playPosition - (Objects.requireNonNull(
            layoutManager
        ) as LinearLayoutManager).findFirstVisibleItemPosition()
        Log.d(ExoPlayerRecyclerView.TAG, "getVisibleVideoSurfaceHeight: at: $at")
        val child = getChildAt(at) ?: return 0
        val location = IntArray(2)
        child.getLocationInWindow(location)
        return if (location[1] < 0) {
            location[1] + videoSurfaceDefaultHeight
        } else {
            screenDefaultHeight - location[1]
        }
    }
    private fun pauseCurrentPlayer(){
        if (this.videoPlayer == null){
            return
        }
        this.videoPlayer!!.pause()
        this.videoPlayer = null
    }

    // Remove the old player
    fun onResumePlayer(){
        if (videoPlayer != null) {
            videoPlayer!!.play()
        }
    }

    fun releasePlayer() {
        if (videoPlayer != null) {
            videoPlayer!!.release()
            videoPlayer = null
        }
        viewHolderParent = null
    }

    fun onPausePlayer() {
        if (videoPlayer != null) {
            videoPlayer!!.pause()
        }
    }

    private fun toggleVolume() {
        if (videoPlayer != null) {
            if (volumeState == VolumeState.OFF) {
                Log.d(ExoPlayerRecyclerView.TAG, "togglePlaybackState: enabling volume.")
                setVolumeControl(VolumeState.ON)
            } else if (volumeState == VolumeState.ON) {
                Log.d(ExoPlayerRecyclerView.TAG, "togglePlaybackState: disabling volume.")
                setVolumeControl(VolumeState.OFF)
            }
        }
    }
    private fun setVolumeControl(state: VolumeState) {
        volumeState = state
        if (state == VolumeState.OFF) {
            videoPlayer!!.volume = 0f
            animateVolumeControl()
        } else if (state == VolumeState.ON) {
            videoPlayer!!.volume = 1f
            animateVolumeControl()
        }
    }
    private fun animateVolumeControl() {
        if (volumeControl != null) {
            volumeControl!!.bringToFront()
            if (volumeState == VolumeState.OFF) {
                requestManager!!.load(R.drawable.baseline_volume_off_24)
                    .into(volumeControl!!)
            } else if (volumeState == VolumeState.ON) {
                requestManager!!.load(R.drawable.baseline_volume_up_24)
                    .into(volumeControl!!)
            }
            volumeControl!!.animate().cancel()
            volumeControl!!.alpha = 1f
            volumeControl!!.animate()
                .alpha(0f)
                .setDuration(600).startDelay = 1000
        }
    }

    fun setMediaObjects(mediaObjects: ArrayList<PostDataModel>) {
        this.mediaObjects = mediaObjects
    }



    private enum class VolumeState {
        ON, OFF
    }
    companion object{
        const val TAG = "HOME FEED RECYCLERVIEW"
        private const val AppName = "Android ExoPlayer"

    }
}