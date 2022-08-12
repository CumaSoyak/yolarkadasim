package roadfriend.app.extension

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.jetbrains.annotations.NotNull
import roadfriend.app.YolArkadasiApp

val Any.classTag: String get() = this.javaClass.canonicalName.orEmpty()

val Any.methodTag get() = classTag + object : Any() {}.javaClass.enclosingMethod?.name

fun Any.hashCodeAsString(): String {
    return hashCode().toString()
}

inline fun <reified T : Any> Any.cast(): T {
    return this as T
}

fun Any?.isNull(): Boolean {
    return this == null
}

@NotNull
fun Any?.isNotNull(): Boolean {
    return !this.isNull()
}

fun List<*>?.isNotNullOrEmpty(): Boolean {
    return !this.isNullOrEmpty()
}


@Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
inline fun <reified Any> getDummyData(fileName:String): Any {
    val data: String = YolArkadasiApp.context.assets.open(fileName+".json").bufferedReader().use { it.readText() }
    return GsonBuilder().create().fromJson(data, Any::class.java)
}
@Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
inline fun <reified Any> getDummyArrayData(fileName:String): Any {
     val listOfCountryType = object : TypeToken<ArrayList<Any>>() {}.type
    val data: String = YolArkadasiApp.context.assets.open(fileName+".json").bufferedReader().use { it.readText() }
    return GsonBuilder().create().fromJson(data, listOfCountryType)
}
