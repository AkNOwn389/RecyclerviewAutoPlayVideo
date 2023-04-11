package com.example.mysocialapp.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.mysocialapp.activities.HomePost
import com.example.mysocialapp.R
import com.example.mysocialapp.activities.MainFragmentContainer
import com.example.mysocialapp.activities.PostViewActivity
import com.example.mysocialapp.activities.UserView
import com.example.mysocialapp.api.retroInstance.PostInstance
import com.example.mysocialapp.models.LikesPostBodyModel
import com.example.mysocialapp.api.retroInstance.RetrofitInstance
import com.example.mysocialapp.activities.CommentViewActivity
import com.example.mysocialapp.adapters.homeFeedRecyclerView.HomeFeedRecyclerViewHolder
import com.example.mysocialapp.models.LikesPostResponseModel
import com.example.mysocialapp.models.PostDataModel
import com.example.mysocialapp.models.postmodel.CommentBody
import com.example.mysocialapp.utils.DataManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.Exception

//Todo data class ng home feed carview
//Todo itemTdobinding automatic na nagagawa itype ang binding basta may recycleview na
class HomeFeedCardViewAdapter(
    private val postListdata: ArrayList<PostDataModel>,
    private val requestManager: RequestManager
): RecyclerView.Adapter<ViewHolder>() {
    private val TAG = "HOME PAGE LOG"
    private lateinit var context:Context
    private lateinit var manager: DataManager
    private lateinit var token: String
    private lateinit var username:String
    private lateinit var commentFragment: CommentViewActivity
    private lateinit var parent: ViewGroup
    /*
    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val postImage: ImageView? = itemView.findViewById(R.id.postCardHomeCardViewImageStorage)
        val caption: TextView? = itemView.findViewById(R.id.postCardHomeCaption)
        val creatorAvatar: ImageView? = itemView.findViewById(R.id.postCardHomeCreatorAvatar)
        val creatorName: TextView? = itemView.findViewById(R.id.postCardHomeAvatarName)
        val createDate: TextView? = itemView.findViewById(R.id.postCardHomeTimeCreated)
        val myAvatar: ImageView? = itemView.findViewById(R.id.myAvatarOnhomeFeed)
        val noOflikes: TextView? = itemView.findViewById(R.id.NoOfLikes)
        val noOfComments: TextView? = itemView.findViewById(R.id.NoOfComments)
        val iconheart: CardView? = itemView.findViewById(R.id.postCardHomeHeartReadIconCardView)
        val iconheart2: ImageView? = itemView.findViewById(R.id.HeartReadIcon)
        val viewallcomment: TextView? = itemView.findViewById(R.id.postCardHomeViewAllComment)
        val likeBtn: ImageButton? = itemView.findViewById(R.id.LikeButton)
        val commentBtn: ImageButton? = itemView.findViewById(R.id.CommentBtn)
        val shareBtn: ImageButton? = itemView.findViewById(R.id.ShareBtn)
        val sendCommenBtn: ImageButton? = itemView.findViewById(R.id.postCardHomeSendComment)
        val edtComment: EditText? = itemView.findViewById(R.id.editTextPostCardHomeAddComment)
        val isProfileUpdate: TextView? = itemView.findViewById(R.id.isUpdateProfile)
        val createPostBar:ConstraintLayout? = itemView.findViewById(R.id.homefeedcreatpostbar)
        val createpostbarimage:ImageView? = itemView.findViewById(R.id.craetepostbarimage)
        val imagelenght:TextView? = itemView.findViewById(R.id.imagelenght)
        val menuBtn:ImageButton? = itemView.findViewById(R.id.postCardHomeMenuBtn)
        val privacy:TextView? = itemView.findViewById(R.id.privacy)

        //type 6
        val mediaContainer: FrameLayout? = itemView.findViewById(R.id.media_container)
        val thumbnail:ImageView? = itemView.findViewById(R.id.thumbnail)
        val progressBar1: ProgressBar? = itemView.findViewById(R.id.progressBar)
        val progressBar2: ProgressBar? = itemView.findViewById(R.id.progressBar2)
        val volumeControl:ImageView? = itemView.findViewById(R.id.volume_control)
        val playerView: StyledPlayerView? = itemView.findViewById(R.id.player_view)
    }

     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFeedRecyclerViewHolder {
        this.context = parent.context
        this.parent = parent
        Log.d(TAG, "Oncreate")
        return when(viewType){
            99 -> HomeFeedRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.home_feed_create_post_bar, parent, false))
            3 -> HomeFeedRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_feed_change_profile_picture, parent, false))
            6 -> HomeFeedRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_card_video, parent, false))
            else -> HomeFeedRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_feed_cardview, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        Log.d(TAG, "OnGetItemType")
        return postListdata[position].media_type!!
    }

    override fun onBindViewHolder(viewGroup: ViewHolder, position: Int) {
        Log.d(TAG, "Onbind")
        (viewGroup as HomeFeedRecyclerViewHolder).onBind(postListdata[position], requestManager = this.requestManager)
        this.manager = DataManager()
        this.commentFragment = CommentViewActivity()
        this.token = manager.getAccessToken(context).toString()
        this.username = manager.getInfoUsername(context).toString()
        val currentItem = postListdata[position]
        Log.d(TAG, "Onwhen")
        when (currentItem.media_type){
            99 -> loadCreatePostBar(viewGroup, currentItem)
            1 -> loadtype1(viewGroup, currentItem)
            2 -> loadtype1(viewGroup, currentItem)
            3 -> loadChangeProfile(viewGroup, currentItem)
            6 -> loadType6(viewGroup, currentItem)
            else -> loadtype1(viewGroup, currentItem)
        }
    }
    private fun loadType6(holder:  HomeFeedRecyclerViewHolder, currentItem: PostDataModel){
        setUIType6(holder, currentItem)
        //setupLoaderType6(currentItem, holder)
        setupListernerType6(holder, currentItem)
    }

    private fun setUIType6(
        holder: HomeFeedRecyclerViewHolder,
        data: PostDataModel
    ) {
        holder.caption?.text = data.description
        holder.creatorName?.text = data.creator_full_name
        holder.createDate?.text = data.created_at
        holder.caption?.text = data.description
        holder.noOflikes?.text = data.NoOflike.toString()
        holder.noOfComments?.text = data.NoOfcomment.toString()
        if (data.NoOfcomment == 0){
            holder.noOfComments?.visibility = View.GONE
        }
        if (data.NoOflike == 0){
            holder.noOflikes?.visibility = View.GONE
            holder.iconheart?.visibility = View.GONE
        }
        Glide.with(parent.context)
            .load(data.creator_avatar)
            .placeholder(R.drawable.progress_animation)
            .error(R.mipmap.white_background)
            .into(holder.creatorAvatar!!)
        if (data.is_like == true){
            holder.likeBtn?.setImageResource(R.drawable.baseline_thumb_up_24)
        }else{
            holder.likeBtn?.setImageResource(R.drawable.baseline_thumb_up_off_alt_24)
        }
        when(data.privacy){
            'P' -> holder.privacy?.text = parent.context.getString(R.string.fublic)
            'F' -> holder.privacy?.text = parent.context.getString(R.string.friends)
            'O' -> holder.privacy?.text = parent.context.getString(R.string.only_me)
        }
        setVideoPlayer(holder, data)

    }
    private fun setVideoPlayer(holder:  HomeFeedRecyclerViewHolder, data: PostDataModel) {
        holder.thumbnail?.visibility = View.INVISIBLE
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory: AdaptiveTrackSelection.Factory = AdaptiveTrackSelection.Factory()
        val trackSelector: TrackSelector = DefaultTrackSelector(context, videoTrackSelectionFactory)
        val exoPlayer = ExoPlayer.Builder(context).setBandwidthMeter(bandwidthMeter).setTrackSelector(trackSelector).build()
        holder.playerView?.player = exoPlayer
        val uri = Uri.parse(data.videos.toString())
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.seekTo(0)
        exoPlayer.prepare()
        holder.playerView!!.visibility = View.VISIBLE
        holder.playerView!!.useController = false
        holder.playerView!!.hideController()
    }

    private fun setupLoaderType6(
        currentItem: PostDataModel,
        holder:  HomeFeedRecyclerViewHolder
    ) {
        when(currentItem.privacy){
            'P' -> {
                holder.privacy?.text = context.getString(R.string.fublic)
            }
            'F' -> {
                holder.privacy?.text = context.getString(R.string.friendsSmall)
            }
            'O' -> {
                holder.privacy?.text = context.getString(R.string.only_me)
            }
        }
        if (currentItem.is_like!!){
            holder.likeBtn?.setImageResource(R.drawable.baseline_thumb_up_24)
        }else{
            holder.likeBtn?.setImageResource(R.drawable.baseline_thumb_up_off_alt_24)
        }
        if (currentItem.NoOflike == 0){
            holder.noOflikes?.isVisible  = false
            holder.iconheart?.isVisible  = false
            holder.iconheart2?.isVisible  = false
        }else{
            holder.noOflikes?.text = currentItem.NoOflike.toString()
            holder.iconheart?.isVisible  = true
            holder.iconheart2?.isVisible  = true
        }
    }

    private fun setupListernerType6(
        holder:  HomeFeedRecyclerViewHolder,
        currentItem: PostDataModel
    ) {
        setDiaLog(holder, currentItem)
        holder.likeBtn?.setOnClickListener {
            liKe(holder, currentItem)
        }
        holder.creatorAvatar?.setOnClickListener {
            (context as? MainFragmentContainer)?.let {
                val intent = Intent(it, UserView::class.java).putExtra("username", currentItem.creator)
                it.startActivity(intent)
            }
        }
        holder.creatorName?.setOnClickListener {
            (context as? MainFragmentContainer)?.let {
                val intent = Intent(it, UserView::class.java).putExtra("username", currentItem.creator)
                it.startActivity(intent)
            }
        }
        holder.commentBtn?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, CommentViewActivity::class.java)
                intent.putExtra("postId", currentItem.id)
                intent.putExtra("userAvatar", currentItem.creator_avatar)
                intent.putExtra("username", currentItem.creator)
                intent.putExtra("user_full_name", currentItem.creator_full_name)
                intent.putExtra("noOfcomment", currentItem.NoOfcomment)
                intent.putExtra("like", currentItem.NoOflike)
                intent.putExtra("time", currentItem.created_at)
                intent.putExtra("title", currentItem.title)
                it.startActivity(intent)
            }
        }
        holder.noOfComments?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, CommentViewActivity::class.java)
                intent.putExtra("postId", currentItem.id)
                intent.putExtra("userAvatar", currentItem.creator_avatar)
                intent.putExtra("username", currentItem.creator)
                intent.putExtra("user_full_name", currentItem.creator_full_name)
                intent.putExtra("noOfcomment", currentItem.NoOfcomment)
                intent.putExtra("like", currentItem.NoOflike)
                it.startActivity(intent)
            }
        }
    }

    private fun loadChangeProfile(
        holder:  HomeFeedRecyclerViewHolder,
        currentItem: PostDataModel
    ){
        holder.caption?.text = currentItem.description
        holder.creatorName?.text = currentItem.creator_full_name
        holder.createDate?.text = currentItem.created_at
        holder.isProfileUpdate?.isVisible  = true
        holder.isProfileUpdate?.text = currentItem.title
        setupLoader(currentItem, holder)
        setupListerner(holder, currentItem)
    }

    private fun loadtype1(
        holder:  HomeFeedRecyclerViewHolder,
        currentItem: PostDataModel
    ) {
        holder.caption?.text = currentItem.description
        holder.creatorName?.text = currentItem.creator_full_name
        holder.createDate?.text = currentItem.created_at
        setupLoader(currentItem, holder)
        setupListerner(holder, currentItem)
    }

    private fun loadCreatePostBar(
        holder:  HomeFeedRecyclerViewHolder,
        currentItem: PostDataModel
    ) {
        setCreatePostBarImage(holder)
        holder.createPostBar?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, HomePost::class.java)
                it.startActivity(intent)
            }
        }
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun setCreatePostBarImage(holder:  HomeFeedRecyclerViewHolder){
        GlobalScope.launch(Dispatchers.Main) {
            val response = try {
                RetrofitInstance.api.getAvatarR(username, token)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful){
                Glide.with(context)
                    .load(response.body()!!.avatar)
                    .placeholder(R.drawable.progress_animation)
                    .error(R.mipmap.greybg)
                    .into(holder.createpostbarimage!!)
            }
        }
    }

    private fun setupListerner(
        holder:  HomeFeedRecyclerViewHolder,
        currentItem: PostDataModel
    ) {
        setDiaLog(holder, currentItem)
        holder.likeBtn?.setOnClickListener {
            liKe(holder, currentItem)
        }
        holder.sendCommenBtn?.setOnClickListener {
            comment(holder, currentItem)
        }
        holder.creatorAvatar?.setOnClickListener {
            (context as? MainFragmentContainer)?.let {
                val intent = Intent(it, UserView::class.java).putExtra("username", currentItem.creator)
                it.startActivity(intent)
            }
        }
        holder.creatorName?.setOnClickListener {
            (context as? MainFragmentContainer)?.let {
                val intent = Intent(it, UserView::class.java).putExtra("username", currentItem.creator)
                it.startActivity(intent)
            }
        }
        holder.postImage?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, PostViewActivity::class.java)
                intent.putExtra("postId", currentItem.id)
                intent.putExtra("userAvatar", currentItem.creator_avatar)
                intent.putExtra("username", currentItem.creator)
                intent.putExtra("user_full_name", currentItem.creator_full_name)
                intent.putExtra("noOfComment", currentItem.NoOfcomment.toString())
                intent.putExtra("like", currentItem.NoOflike.toString())
                it.startActivity(intent)
            }
        }
        holder.commentBtn?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, CommentViewActivity::class.java)
                intent.putExtra("postId", currentItem.id)
                intent.putExtra("userAvatar", currentItem.creator_avatar)
                intent.putExtra("username", currentItem.creator)
                intent.putExtra("user_full_name", currentItem.creator_full_name)
                intent.putExtra("noOfcomment", currentItem.NoOfcomment)
                intent.putExtra("like", currentItem.NoOflike)
                intent.putExtra("time", currentItem.created_at)
                intent.putExtra("title", currentItem.title)
                it.startActivity(intent)
            }
        }
        holder.viewallcomment?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, CommentViewActivity::class.java)
                intent.putExtra("postId", currentItem.id)
                intent.putExtra("userAvatar", currentItem.creator_avatar)
                intent.putExtra("username", currentItem.creator)
                intent.putExtra("user_full_name", currentItem.creator_full_name)
                intent.putExtra("noOfcomment", currentItem.NoOfcomment)
                intent.putExtra("like", currentItem.NoOflike)
                it.startActivity(intent)
            }
        }
        holder.noOfComments?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, CommentViewActivity::class.java)
                intent.putExtra("postId", currentItem.id)
                intent.putExtra("userAvatar", currentItem.creator_avatar)
                intent.putExtra("username", currentItem.creator)
                intent.putExtra("user_full_name", currentItem.creator_full_name)
                intent.putExtra("noOfcomment", currentItem.NoOfcomment)
                intent.putExtra("like", currentItem.NoOflike)
                it.startActivity(intent)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun changePrivacy(holder:  HomeFeedRecyclerViewHolder, currentItem: PostDataModel, privacy:String){
        GlobalScope.launch(Dispatchers.Main) {
            if (isActive){
                val response = try {
                    PostInstance.api.changePrivacy(token, currentItem.id.toString(), privacy)
                }catch (e:Exception){
                    Log.d("Exception", e.message.toString())
                    return@launch
                }
                if (response.isSuccessful){
                    if (response.body()!!.status){
                        val pos = postListdata.indexOf(currentItem)
                        when(privacy){
                            "Public" -> currentItem.privacy = 'P'
                            "Friends" -> currentItem.privacy = 'F'
                            "Only-Me" -> currentItem.privacy = 'O'
                        }
                        notifyItemChanged(pos, currentItem)
                        return@launch
                    }
                }
            }
        }
    }
    private fun showDialogChangePostPrivacy(holder: HomeFeedRecyclerViewHolder, currentItem:PostDataModel){
        val dialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_change_privacy, parent, false)
        val public:TextView? = view.findViewById(R.id.dialogPublic)
        val friends:TextView? = view.findViewById(R.id.friends)
        val onlyMe:TextView? = view.findViewById(R.id.onlyMe)
        dialog.setContentView(view)
        dialog.show()

        public?.setOnClickListener {
            changePrivacy(holder, currentItem, "Public")
            dialog.dismiss()
        }
        friends?.setOnClickListener {
            changePrivacy(holder, currentItem, "Friends")
            dialog.dismiss()
        }
        onlyMe?.setOnClickListener {
            changePrivacy(holder, currentItem, "Only-Me")
            dialog.dismiss()
        }
    }
    private fun showFriendDialog(holder:  HomeFeedRecyclerViewHolder, currentItem: PostDataModel){
        val dialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_home_feed_cardview, parent, false)
        val favorite: TextView? = view.findViewById(R.id.addtoFavorite)
        val unfollow: TextView? = view.findViewById(R.id.unfollow)
        val hidePosts: TextView? = view.findViewById(R.id.hideposts)
        val report: TextView? = view.findViewById(R.id.report)
        dialog.setContentView(view)
        dialog.show()
        favorite?.text = "Add ${currentItem.creator_full_name} to favorite"
        unfollow?.text = "Unfollow ${currentItem.creator_full_name}"
        hidePosts?.text = "Hide all ${currentItem.creator_full_name} posts"
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun showMyDialog(holder:  HomeFeedRecyclerViewHolder, currentItem: PostDataModel){
        val dialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_my_home_feed_cardview, parent, false)
        val changePrivacy: TextView? = view.findViewById(R.id.changePrivacy)
        val delete:TextView? = view.findViewById(R.id.deletePosts)
        val EditPosts: TextView? = view.findViewById(R.id.editPosts)
        val hidePosts: TextView? = view.findViewById(R.id.ArchivePosts)
        dialog.setContentView(view)
        dialog.show()
        changePrivacy?.setOnClickListener {
            showDialogChangePostPrivacy(holder, currentItem)
        }
        delete?.setOnClickListener {
            val builder = AlertDialog.Builder((context as AppCompatActivity))
            builder.setPositiveButton("Yes"){_, _ ->
                GlobalScope.launch(Dispatchers.Main) {
                    val response = try {
                        PostInstance.api.deletePost(token, currentItem.id.toString())
                    }catch (e:Exception){
                        e.printStackTrace()
                        return@launch
                    }
                    if (response.isSuccessful && response.body()!!.status){
                        val body = response.body()!!
                        if (body.message == "posts deleted." || body.status){
                            try {
                                val pos = postListdata.indexOf(currentItem)
                                notifyItemRemoved(pos)
                                postListdata.removeAt(pos)
                            }catch (e:Exception){
                                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    dialog.dismiss()
                }
            }
            builder.setNegativeButton("No"){_, _ -> }
            builder.setTitle("Delete posts?")
            builder.setMessage("Are you sure you want yo delete?")
            builder.create().show()
        }
    }
    private fun setDiaLog(
        holder:  HomeFeedRecyclerViewHolder,
        currentItem: PostDataModel
    ) {
        holder.menuBtn?.setOnClickListener {
            if (currentItem.me == false) {
                showFriendDialog(holder, currentItem)
            }else{
                showMyDialog(holder, currentItem)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun comment(holder: HomeFeedRecyclerViewHolder, currentItem:PostDataModel){
        val mycomment = holder.edtComment?.text.toString()
        if (mycomment.isEmpty()){
            return
        }
        val body = CommentBody(post_id = currentItem.id!!, comment = mycomment)
        GlobalScope.launch {
            val response = try {
                PostInstance.api.comment(token,  body)
            }catch (e:Exception){
                e.stackTrace
                return@launch
            }
            if (response.isSuccessful && response.body() != null){
                val resBody = response.body()!!
                if (resBody.status){
                    withContext(Dispatchers.Main){
                        currentItem.NoOfcomment?.plus(1)
                        holder.noOfComments?.isVisible  = true
                        holder.viewallcomment?.isVisible  = true
                        holder.noOfComments?.text = "${currentItem.NoOfcomment?.plus(1)} comments"
                        holder.viewallcomment?.text = "View All ${currentItem.NoOfcomment?.plus(1)} comments"
                        holder.edtComment?.setText("")
                    }
                }
            }
        }
    }
    private fun setupLoader(
        currentItem: PostDataModel,
        holder: HomeFeedRecyclerViewHolder
    ) {
        when(currentItem.privacy){
            'P' -> {
                holder.privacy?.text = context.getString(R.string.fublic)
            }
            'F' -> {
                holder.privacy?.text = context.getString(R.string.friendsSmall)
            }
            'O' -> {
                holder.privacy?.text = context.getString(R.string.only_me)
            }
        }
        if (currentItem.is_like!!){
            holder.likeBtn?.setImageResource(R.drawable.baseline_thumb_up_24)
        }else{
            holder.likeBtn?.setImageResource(R.drawable.baseline_thumb_up_off_alt_24)
        }
        if (currentItem.NoOflike == 0){
            holder.noOflikes?.isVisible  = false
            holder.iconheart?.isVisible  = false
            holder.iconheart2?.isVisible  = false
        }else{
            holder.noOflikes?.text = currentItem.NoOflike.toString()
            holder.iconheart?.isVisible  = true
            holder.iconheart2?.isVisible  = true
        }
        if (currentItem.NoOfcomment == 0){
            holder.noOfComments?.isVisible  = false
            holder.viewallcomment?.isVisible  = false
        }else{
            holder.noOfComments?.text = "${currentItem.NoOfcomment} comments"
            holder.viewallcomment?.text = "View All ${currentItem.NoOfcomment} comments"
        }
        if (currentItem.media_type == 4){
            holder.isProfileUpdate?.isVisible  = true
            holder.isProfileUpdate?.text = currentItem.title
        }

        Glide.with(context)
            .load(currentItem.your_avatar)
            .placeholder(R.drawable.progress_animation)
            .error(R.drawable.try_later)
            .into(holder.myAvatar!!)
        Glide.with(context)
            .load(currentItem.creator_avatar)
            .placeholder(R.drawable.progress_animation)
            .error(R.drawable.try_later)
            .into(holder.creatorAvatar!!)

        try {
            val option = RequestOptions().placeholder(R.mipmap.greybg)
            Glide.with(context)
                .load(currentItem.image_url?.get(0)?.image)
                .apply(option)
                .error(R.drawable.try_later)
                .into(holder.postImage!!)
            if (currentItem.image_url!!.size > 1){
                holder.imagelenght?.isVisible = true
                holder.imagelenght?.text = "${currentItem.image_url.size-1}+ more"
            }else{
                holder.imagelenght?.isVisible = false
            }
        }catch (e: Exception){
            e.stackTrace
        }
    }
    private fun liKe(holder: HomeFeedRecyclerViewHolder, currentItem: PostDataModel) {
        if (holder.likeBtn?.imageAlpha == R.drawable.baseline_thumb_up_24){
            holder.likeBtn!!.setImageResource(R.drawable.baseline_thumb_up_off_alt_24)
        }else{
            holder.likeBtn?.setImageResource(R.drawable.baseline_thumb_up_24)
            holder.iconheart?.isVisible = true
            holder.iconheart2?.isVisible  = true
        }
        val body = LikesPostBodyModel(currentItem.id!!)
        val request = manager.getAccessToken(context)
            ?.let { it1 -> RetrofitInstance.retrofitBuilder.likepostC(it1, body) }
        request?.enqueue(object : Callback<LikesPostResponseModel?> {
            override fun onResponse(
                call: Call<LikesPostResponseModel?>,
                response: Response<LikesPostResponseModel?>
            ) {
                if (response.isSuccessful && response.body() != null){
                    val res = response.body()!!
                    if (res.status){
                        if (res.message == "post like"){
                            holder.likeBtn?.setImageResource(R.drawable.baseline_thumb_up_24)
                            currentItem.NoOflike?.plus(1)

                        }
                        if (res.message == "post unlike"){
                            holder.likeBtn?.setImageResource(R.drawable.baseline_thumb_up_off_alt_24)
                            currentItem.NoOflike?.minus(1)
                        }
                        if (res.post_likes == 0){
                            holder.noOflikes?.isVisible = false
                            holder.iconheart?.isVisible = false
                            holder.iconheart2?.isVisible = false
                        }else{
                            holder.iconheart?.isVisible = true
                            holder.iconheart2?.isVisible = true
                            holder.noOflikes?.isVisible = true
                            holder.noOflikes?.text = res.post_likes.toString()
                        }

                    }else{
                        return
                    }

                }else{
                    return
                }
            }

            override fun onFailure(call: Call<LikesPostResponseModel?>, t: Throwable) {
            }
        })
    }
    override fun getItemCount(): Int {
        return postListdata.size
    }
}