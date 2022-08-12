package roadfriend.app.extension

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.load(path: String) {
    Glide.with(context).load(path)
        .into(this)
}