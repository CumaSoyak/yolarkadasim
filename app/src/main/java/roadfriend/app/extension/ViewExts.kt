package roadfriend.app.extension

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.text.InputType
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import roadfriend.app.R
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.visibility(b: Boolean) {
    if (b)
        visibility = View.VISIBLE
    else
        visibility = View.GONE
}
fun Fragment.className()=this::class.java.name

fun DialogFragment.className()=this::class.java.name

fun ViewGroup.convertDpiToPx(dpi: Int): Int {
    var pixel = 0f
    try {
        val r = context.resources
        pixel = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dpi.toFloat(),
            r.displayMetrics
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }


    return pixel.toInt()
}

fun EditText.showHidePassword(action: ((isShown: Boolean) -> Unit)) {
    when (this.inputType) {
        InputType.TYPE_TEXT_VARIATION_PASSWORD, InputType.TYPE_TEXT_VARIATION_PASSWORD + 1 -> {
            this.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            action.invoke(false)
        }
        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD -> {
            this.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD + 1
            action.invoke(true)
        }
    }
    this.setSelection(this.text.length)
}

fun EditText.isValidEmail(): Boolean {
    val isValid =
        !TextUtils.isEmpty(this.text) && android.util.Patterns.EMAIL_ADDRESS.matcher(this.text)
            .matches()
    return isValid
}

fun TextView.fromHtml(htmlStr: String) {
    this.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(htmlStr, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(htmlStr)
    }
}

fun CheckBox.fromHtml(htmlStr: String) {
    this.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(htmlStr, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(htmlStr)
    }
}

fun ImageView.setColorFilter(@ColorInt color: Int) {
    this.colorFilter =
        BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_ATOP)
}


fun Context.drawable(@DrawableRes drawableResId: Int) =
    ContextCompat.getDrawable(this, drawableResId)

fun Context.color(colorId: Int) = ContextCompat.getColor(this, colorId)

fun getFilter(@ColorInt color:Int): ColorFilter? {
    return BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_ATOP)
}
fun Context.setFont(fontId: Int): Typeface? {
    val face = Typeface.createFromAsset(
        this.assets,
        fontId.toString()
    )
    return face
}

fun HorizontalScrollView.scrollToCenter() {
    val scrollView = this
    CoroutineScope(Dispatchers.Main).launch {
        delay(100)
        val center: Int = scrollView.scrollX + scrollView.width / 2
        val child = scrollView.getChildAt(0)
        val viewLeft: Int = child.left
        val viewWidth: Int = child.width
        if (center >= viewLeft && center <= viewLeft + viewWidth) {
            scrollView.smoothScrollBy(viewLeft + viewWidth / 2 - center, 0)
        }
    }
}

fun RecyclerView.setDivider(@DrawableRes drawableRes: Int) {
    val divider = DividerItemDecoration(
        this.context,
        DividerItemDecoration.VERTICAL
    )
    val drawable = ContextCompat.getDrawable(
        this.context,
        drawableRes
    )
    drawable?.let {
        divider.setDrawable(it)
        addItemDecoration(divider)
    }
}

fun ImageView.loadImage(url: String?) {
    Glide.with(context)
        .load(url)
        .into(this)
}

fun TextView.underline() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}


fun View.margin(
    left: Float? = null,
    top: Float? = null,
    right: Float? = null,
    bottom: Float? = null
) {
    layoutParams<ViewGroup.MarginLayoutParams> {
        left?.run { leftMargin = dpToPx(this) }
        top?.run { topMargin = dpToPx(this) }
        right?.run { rightMargin = dpToPx(this) }
        bottom?.run { bottomMargin = dpToPx(this) }
    }
}

inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
    if (layoutParams is T) block(layoutParams as T)
}

fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
fun Context.dpToPx(dp: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

fun AppCompatButton.buttonClickable(isClickable: Boolean,id: Int = 0,inactiveId:Int = 0) {
    var color = if (isClickable){
        if(id == 0) {
            resources.getColor(R.color.blue)
        } else {
            resources.getColor(id)
        }
    } else {
        if(inactiveId == 0){
            resources.getColor(R.color.red)
        }else{
            resources.getColor(inactiveId)
        }
    }
    backgroundTintList = ColorStateList.valueOf(color)
    this.isClickable = isClickable
    this.isEnabled = isClickable
}