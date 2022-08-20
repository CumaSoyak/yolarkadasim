package roadfriend.app.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import com.google.gson.GsonBuilder
import java.util.*

object PrefUtils {
    private const val NAME = "BiletDukkani"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences
    private val testMap = mutableMapOf<String, Any>()

    private val INSTALLED_REFERRER_DATA = Pair("installedReferrerData", "")
    private val IS_REFER_NOT_SUPPORTED = Pair("isReferNotSupported", false)
    private val IS_FIRST_RUN_PREF = Pair("is_first_run", false)
    private val AUTH_TOKEN = Pair("authToken", "")
    private val EXAMPLE_INT = Pair("selectedLanguage", 0)
    private val MODEL = Pair("selessctedLanguage", "")
    private val ME = Pair("me", "")
    private val WALLET = Pair("wallet", "")
    private val CONTACT_ID = Pair("contactID", "")
    private val AREA_CODE = Pair("areaCode", "")
    private val PHONE_NUMBER = Pair("phoneNumber", "")


    var isLogin: Boolean
        get() = IS_FIRST_RUN_PREF.getBoolean()
        set(value) = IS_FIRST_RUN_PREF.setBoolean(value)

/*
    var me: Me?
        get() = ME.getModel<Me>()
        set(value) = ME.setModel(value)
    */

    var nameOfUser: String?
        get() = MODEL.getString()
        set(value) = MODEL.setString(value)


    var device_id: String?
        get() = PHONE_NUMBER.getString()
        set(value) = PHONE_NUMBER.setString(value)

    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    inline fun <reified Any> Pair<String, String>.getModel(): Any {
        val data = if (isTestRunner()) {
            (testMap[this.first] ?: this.second).toString()
        } else {
            preferences.getString(this.first, this.second)
        }
        return GsonBuilder().create().fromJson(data, Any::class.java)
    }


    fun Pair<String, String>.getString(): String {
        return if (isTestRunner()) {
            (testMap[this.first] ?: this.second).toString()
        } else {
            preferences.getString(this.first, this.second).toString()
        }
    }

    fun Pair<String, Int>.getInt(): Int {
        return if (isTestRunner()) {
            (testMap[this.first] ?: this.second).toString().toInt()
        } else {
            preferences.getInt(this.first, this.second)
        }
    }

    fun Pair<String, Boolean>.getBoolean(): Boolean {
        return if (isTestRunner()) {
            (testMap[this.first] ?: this.second).toString().toBoolean()
        } else {
            preferences.getBoolean(this.first, this.second)
        }
    }


    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    inline fun <reified Any> Pair<String, String>.setModel(value: Any) {
        val data = GsonBuilder().serializeNulls().create().toJson(value)
        if (isTestRunner()) {
            testMap[this.first] = data ?: "" as kotlin.Any
        } else {
            preferences.edit { it.putString(this.first, data) }
        }
    }

    fun Pair<String, String>.setString(value: String?) {
        if (isTestRunner()) {
            testMap[this.first] = value ?: "" as Any
        } else {
            preferences.edit { it.putString(this.first, value) }
        }
    }


    fun Pair<String, Int>.setInt(value: Int) {
        if (isTestRunner()) {
            testMap[this.first] = value as Any
        } else {
            preferences.edit { it.putInt(this.first, value) }
        }
    }

    fun Pair<String, Boolean>.setBoolean(value: Boolean) {
        if (isTestRunner()) {
            testMap[this.first] = value as Any
        } else {
            preferences.edit { it.putBoolean(this.first, value) }
        }
    }


    fun init(context: Context) {
        context.getSharedPreferences(NAME, MODE)
    }

    fun isTestRunner(): Boolean {
        return Build.FINGERPRINT.lowercase() == "robolectric"
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

}