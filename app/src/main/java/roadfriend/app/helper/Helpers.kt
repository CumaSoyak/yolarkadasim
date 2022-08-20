package roadfriend.app.helper

import android.content.res.Resources
import android.net.Uri
import android.view.View
import android.view.animation.TranslateAnimation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import roadfriend.app.extension.isNotNullOrEmpty
import roadfriend.app.models.DeepLink
import roadfriend.app.utils.PrefUtils
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.URL
import java.net.URLDecoder
import java.util.*


class Helpers {


    fun createDeepLink(deepLink: DeepLink) {
        val json = Gson().toJson(deepLink)

    }

    fun isDeepLinkExists(): Boolean {
        return true
    }

    fun handleDeepLink(): DeepLink? {
        if (!isDeepLinkExists()) {
            return null
        }
        return DeepLink("", hashMapOf("" to ""))
    }

    fun removeDeepLink() {

    }

    fun splitQuery(uri: Uri): LinkedHashMap<String, String> {
        val queryPairs: LinkedHashMap<String, String> = LinkedHashMap()
        val url = URL(uri.toString())
        if (url.query.isNotNullOrEmpty()) {
            val query: String = url.query
            val pairs = query.split("&").toTypedArray()
            for (pair in pairs) {
                val idx = pair.indexOf("=")
                queryPairs[URLDecoder.decode(pair.substring(0, idx), "UTF-8")] =
                    URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
            }
        }
        return queryPairs
    }

    fun isPasswordValid(password: String): Boolean {
        if (password.length < 8 || !(password.contains(Regex("[0-9]")) && password.contains(Regex("[a-zA-Z]")))) {
            return false
        }
        return true
    }


    fun getIpAddress(useIPv4: Boolean): String? {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (inter in interfaces) {
                val address: List<InetAddress> = Collections.list(inter.inetAddresses)
                for (addr in address) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.uppercase() else sAddr.substring(
                                    0,
                                    delim
                                ).uppercase()
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
        }
        return ""
    }

    fun slideUp(view: View) {
        val animate = TranslateAnimation(
            0f,
            0f,
            getScreenHeight().toFloat(),
            0f,
        )
        animate.duration = 800
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    fun isVersionLollipop(): Boolean {
        if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.LOLLIPOP) {
            return true
        }
        return false
    }

}