package roadfriend.app.extension

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

import roadfriend.app.R


fun Activity.toast(message: String) {
    Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
}

fun Activity.toastLong(message: String) {
    Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show()
}

fun Fragment.toast(message: String) {
    context?.let {
        Toast.makeText(it, message.toString(), Toast.LENGTH_SHORT).show()
    }
}

fun Fragment.toastLong(message: String) {
    Toast.makeText(context, message.toString(), Toast.LENGTH_LONG).show()
}


fun Activity.errorAlerter(message: String?) {
   // val alerter = Alerter.create(this).setTitle(message ?: "")
   //     .setBackgroundColorRes(R.color.error_red)
   // val drawable = try {
   //     ContextCompat.getDrawable(applicationContext, R.drawable.ic_alerter)
   // } catch (ignored:Exception){null}
   // drawable?.let {
   //     alerter.setIcon(it)
   // }
   // alerter.show()
   // println("Alerter! -> $message")
}

fun Dialog.errorAlerter(message: String?) {
  // val alerter = Alerter.create(this).setTitle(message ?: "")
  //     .setBackgroundColorRes(R.color.error_red)
  // val drawable = try {
  //     ContextCompat.getDrawable(context, R.drawable.ic_alerter)
  // } catch (ignored:Exception){null}
  // drawable?.let {
  //     alerter.setIcon(it)
  // }
  // alerter.show()
  // println("Alerter! -> $message")
}

fun Activity.successAlerter(message: String?) {
    //Alerter.create(this).setTitle(message ?: "").setBackgroundColorRes(R.color.lima).show()
}

fun Dialog.successAlerter(message: String?) {
   // Alerter.create(this).setTitle(message ?: "").setBackgroundColorRes(R.color.lima).show()
}

fun Activity.infoAlerter(message: String?) {
   // Alerter.create(this).setTitle(message ?: "").setBackgroundColorRes(R.color.orange).show()
}

fun Context.showTooltip(view: View, text: String) {
   //Tooltip.on(view)
   //    .text(text)
   //    // .iconStart(android.R.drawable.ic_dialog_info)
   //    // .iconStartSize(30, 30)
   //    .color(ContextCompat.getColor(view.context, R.color.white))
   //    .textColor(ContextCompat.getColor(view.context,R.color.grayscale_70))
   //    .border(Color.BLUE, 1f)
   //    .clickToHide(true)
   //    .corner(8)
   //    .position(Position.TOP)
   //    .show(3000)
}