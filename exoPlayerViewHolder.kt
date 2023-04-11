package com.example.mysocialapp.adapters.homeFeedRecyclerView

import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.mysocialapp.R
import com.example.mysocialapp.models.PostDataModel
import com.google.android.exoplayer2.ui.StyledPlayerView

class HomeFeedRecyclerViewHolder(val parent: View): ViewHolder(parent) {
    @JvmField
    var requestManager: RequestManager? = null
    @JvmField
    var mediaContainer: FrameLayout? = null
    @JvmField
    var thumbnail: ImageView? = null
    @JvmField
    var progressBar1: ProgressBar? = null
    @JvmField
    var progressBar2: ProgressBar? = null
    @JvmField
    var volumeControl: ImageView? = null
    @JvmField
    var postImage: ImageView? = null
    @JvmField
    var caption: TextView? = null
    @JvmField
    var creatorAvatar: ImageView? = null
    @JvmField
    var creatorName: TextView? = null
    @JvmField
    var createDate: TextView? = null
    @JvmField
    var myAvatar: ImageView? = null
    @JvmField
    var noOflikes: TextView? = null
    @JvmField
    var noOfComments: TextView? = null
    @JvmField
    var iconheart: CardView? = null
    @JvmField
    var iconheart2: ImageView? = null
    @JvmField
    var viewallcomment: TextView? = null
    @JvmField
    var likeBtn: ImageButton? = null
    @JvmField
    var commentBtn: ImageButton? = null
    @JvmField
    var shareBtn: ImageButton? = null
    @JvmField
    var sendCommenBtn: ImageButton? = null
    @JvmField
    var edtComment: EditText? = null
    @JvmField
    var isProfileUpdate: TextView? = null
    @JvmField
    var createPostBar: ConstraintLayout? = null
    @JvmField
    var createpostbarimage:ImageView? = null
    @JvmField
    var imagelenght:TextView? = null
    @JvmField
    var menuBtn:ImageButton? = null
    @JvmField
    var privacy:TextView? = null
    @JvmField
    var playerView: StyledPlayerView? = null
    fun onBind(mediaObject: PostDataModel, requestManager: RequestManager?) {
        this.requestManager = requestManager
        parent.tag = this
        if (thumbnail != null && mediaObject.videos != null){
            this.requestManager!!.load(mediaObject.videos)
                .into(thumbnail!!).toString()
        }
    }
    init {
        playerView = itemView.findViewById(R.id.player_view)
        mediaContainer = itemView.findViewById(R.id.media_container)
        thumbnail = itemView.findViewById(R.id.thumbnail)
        progressBar1 = itemView.findViewById(R.id.progressBar)
        progressBar2 = itemView.findViewById(R.id.progressBar2)
        volumeControl = itemView.findViewById(R.id.volume_control)
        postImage = itemView.findViewById(R.id.postCardHomeCardViewImageStorage)
        caption = itemView.findViewById(R.id.postCardHomeCaption)
        creatorAvatar = itemView.findViewById(R.id.postCardHomeCreatorAvatar)
        creatorName = itemView.findViewById(R.id.postCardHomeAvatarName)
        createDate = itemView.findViewById(R.id.postCardHomeTimeCreated)
        myAvatar = itemView.findViewById(R.id.myAvatarOnhomeFeed)
        noOflikes = itemView.findViewById(R.id.NoOfLikes)
        noOfComments = itemView.findViewById(R.id.NoOfComments)
        iconheart = itemView.findViewById(R.id.postCardHomeHeartReadIconCardView)
        iconheart2 = itemView.findViewById(R.id.HeartReadIcon)
        viewallcomment = itemView.findViewById(R.id.postCardHomeViewAllComment)
        likeBtn = itemView.findViewById(R.id.LikeButton)
        commentBtn = itemView.findViewById(R.id.CommentBtn)
        shareBtn = itemView.findViewById(R.id.ShareBtn)
        sendCommenBtn = itemView.findViewById(R.id.postCardHomeSendComment)
        edtComment = itemView.findViewById(R.id.editTextPostCardHomeAddComment)
        isProfileUpdate = itemView.findViewById(R.id.isUpdateProfile)
        createPostBar = itemView.findViewById(R.id.homefeedcreatpostbar)
        createpostbarimage = itemView.findViewById(R.id.craetepostbarimage)
        imagelenght = itemView.findViewById(R.id.imagelenght)
        menuBtn = itemView.findViewById(R.id.postCardHomeMenuBtn)
        privacy = itemView.findViewById(R.id.privacy)

    }
}