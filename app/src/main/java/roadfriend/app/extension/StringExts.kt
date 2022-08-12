package roadfriend.app.extension

import roadfriend.app.R
import roadfriend.app.extension.getDummyData
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

var localizedData: Map<String, String> =  HashMap()

fun String.clearSpace() = this.replace(" ", "")

fun String.isContainsNumber(): Boolean {
    return this.contains(Regex("[0-9]"))
}

fun String.isContainsLetter(): Boolean {
    return this.contains(Regex("[a-zA-ZğüşöçİĞÜŞÖÇ]"))
}

fun String.isContainsLetterNumber(): Boolean {
    return this.matches(Regex("^[a-zA-Z0-9ğüşöçİĞÜŞÖÇ. ]*$"))
}

fun Char.isContainsNumber(): Boolean {
    return this.toString().isContainsNumber()
}

fun Char.isContainsLetter(): Boolean {
    return this.toString().isContainsLetter()
}

fun String.checkFlightTimeParse(): Boolean {
    return try {
        val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        format.parse(this)
        true
    } catch (ex: Exception) {
        false
    }
}



fun String?.isNotNullOrEmpty() = !this.isNullOrEmpty()

fun CharSequence?.isNotNullOrEmpty() = !this.isNullOrEmpty()

fun String.creditCardValidation(): Boolean {
    val nDigits = this.length
    var nSum = 0
    var isSecond = false
    for (i in nDigits - 1 downTo 0) {
        var d = this[i] - '0'
        if (isSecond) d *= 2
        nSum += d / 10
        nSum += d % 10
        isSecond = !isSecond
    }
    return nSum % 10 == 0
}

fun String.capitalize(): String {
    val capBuffer = StringBuffer()
    val capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(this)
    while (capMatcher.find()) {
        capMatcher.appendReplacement(
            capBuffer,
            capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase()
        )
    }

    return capMatcher.appendTail(capBuffer).toString()
}

fun String.replaceTurkishChars(): String {
    return this.uppercase(Locale.ENGLISH)
        .replace("Ç", "C")
        .replace("Ğ", "G")
        .replace("Ö", "O")
        .replace("Ş", "S")
        .replace("Ü", "U")
        .replace("İ", "I")
}


fun get(key: String): String {

    if (localizedData[key].isNotNullOrEmpty()) {
        return localizedData[key].toString()
    } else {
        println("ERROR_STRING: $key")
        return "!!! String hatalı !!!"
    }
}


fun checkLocalizedData() {
    if (localizedData.isEmpty()) {
        localizedData = getDummyData<HashMap<String, String>>("localization")
    }
}

fun getLocaleLanguage(): String {
    if (Locale.getDefault().language.lowercase(Locale.ENGLISH) != "tr") {
        return "en"
    }

    return Locale.getDefault().language.lowercase(Locale.ENGLISH)
}

fun Double?.decimalFormat(format:String?=null)= DecimalFormat(format?:"#,###.###",
    DecimalFormatSymbols(Locale.US)
).format(this)

fun String?.doubleTo():Double{
    //13223.33
    if(this.isNullOrEmpty())
        return 0.0
    else
        return this.replace(",","").toDouble()
}
fun String.phoneSmsTitleFormat()="+"+replaceRange(3,9,"** **").toString()



