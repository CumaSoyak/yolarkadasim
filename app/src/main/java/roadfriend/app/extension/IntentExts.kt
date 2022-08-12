package roadfriend.app.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


inline fun <reified T : Any> Activity.launchActivity(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivityForResult(intent, requestCode, options)
}

inline fun <reified T : Any> Context.launchActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent, options)
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)

//findNavController().changeNodeDestination(R.id.nav_graph_login,R.id.continueWithSocialFragment).navigate(R.id.action_flightPassengerCountFragment_to_nav_graph_login)

fun NavController.changeNodeDestination(nodeId: Int, destinationId: Int): NavController {
    val graph = graph.findNode(nodeId) as NavGraph
    graph.setStartDestination(destinationId)
    return this
}

fun NavController.changeNodeDestination(vararg nodeIds: Int, destinationId: Int): NavController {
    var currentNode = graph

    nodeIds.forEachIndexed { index, i ->
        currentNode = currentNode.findNode(nodeIds[index]) as NavGraph
    }
    currentNode.setStartDestination(destinationId)
    return this
}
fun BottomSheetDialogFragment.open(){
    this.show(childFragmentManager, this::class.java.toString())
}
fun Context.updateApp() {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse("market://details?id=" + this.packageName)
    this.startActivity(intent)
}
inline fun Any.cM(): String = Any::class.java.toString()

fun Context.share(text: String) =
    this.startActivity(Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    })