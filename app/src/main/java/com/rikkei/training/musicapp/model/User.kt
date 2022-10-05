package com.rikkei.training.musicapp.model

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.view.View
import android.widget.TextView
import com.google.gson.annotations.SerializedName
import com.rikkei.training.musicapp.ui.moduleMusic.PlayMusicFragment
import com.rikkei.training.musicapp.ui.moduleMusic.PlayMusicFragment.Companion.thumbView
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.ui.personal.LocalFavouriteFragment
import java.util.concurrent.TimeUnit

class User {
    @SerializedName("password")
    private var password: String? = ""
    @SerializedName("username")
    private var username: String? = ""
    @SerializedName("id")
    private var id: String? = ""
    @SerializedName("dob")
    private var dob: String? = ""
    @SerializedName("first_name")
    private var first_name: String? = ""
    @SerializedName("last_name")
    private var last_name: String? = ""
    constructor(
                id: String?,
                username: String?,
                password: String?,
                dob:String?,
                first_name: String?,
                last_name: String?){
        this.password = password
        this.username = username
        this.id = id
        this.first_name = first_name
        this.last_name = last_name
        this.dob = dob
    }
    constructor(username: String?,
                password: String?) {
        this.password = password
        this.username = username
    }
    constructor()

}
data class UserList(
    @SerializedName("data")
    val data: List<User>)

data class UserResponse(
    @SerializedName("user")
    val user: User)



fun createTimeText(time: Long): String{
    val min = TimeUnit.MINUTES.convert(time, TimeUnit.MILLISECONDS)
    val sec = (TimeUnit.SECONDS.convert(time, TimeUnit.MILLISECONDS) -
            min * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%2d:%2d", min, sec)
}

fun getImgArt(path: String?): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

fun setSongPosition(increment: Boolean){
    if (increment){
        if (PlayMusicFragment.song.size - 1 == PlayMusicFragment.songPosition){
            PlayMusicFragment.songPosition = 0
        } else ++PlayMusicFragment.songPosition
    } else{
        if (0 == PlayMusicFragment.songPosition){
            PlayMusicFragment.songPosition = PlayMusicFragment.song.size - 1
        } else --PlayMusicFragment.songPosition
    }
}


@SuppressLint("SetTextI18n")
fun getThumb(progress: Int, context: Context): Drawable {

    val total = createTimeText(PlayMusicFragment.musicPlayService!!.songPlayer!!.duration.toLong())
    val current = createTimeText(progress.toLong())

    thumbView.findViewById<TextView>(R.id.tvProgress).text = "$current/$total"
    thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    val bitmap = Bitmap.createBitmap(thumbView.measuredWidth, thumbView.measuredHeight, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(bitmap)
    thumbView.layout(0, 0, thumbView.measuredWidth, thumbView.measuredHeight)
    thumbView.draw(canvas)

    return BitmapDrawable(context.resources, bitmap)
}

fun favouriteChecker(id: Long): Int{
    PlayMusicFragment.isFavourite = false
    LocalFavouriteFragment.favouriteList.forEachIndexed { index, song ->
        if (id == song.thisId){
            PlayMusicFragment.isFavourite = true
            return index
        }
    }
    return -1
}