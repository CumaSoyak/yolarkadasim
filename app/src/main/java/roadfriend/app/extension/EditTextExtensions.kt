package roadfriend.app.extension

import android.text.*
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.annotation.CheckResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

val notAllowedChars = arrayListOf('π','∏')

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}
fun EditText.onTextChanged(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            onTextChanged.invoke(p0.toString())
        }

        override fun afterTextChanged(editable: Editable?) {

        }
    })
}

fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
    this.setOnTouchListener { v, event ->
        var hasConsumed = false
        if (v is EditText) {
            if (event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
        }
        hasConsumed
    }
}

fun EditText.addFilter(filter: InputFilter) {
    filters =
        if (filters.isNullOrEmpty()) {
            arrayOf(filter)
        } else {
            filters
                .toMutableList()
                .apply {
                    removeAll { it.javaClass == filter.javaClass }
                    add(filter)
                }
                .toTypedArray()
        }
}

fun EditText.nextFocus(nextFocusView: View) {
    setOnEditorActionListener { _, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_GO
            || actionId == EditorInfo.IME_ACTION_DONE
            || actionId == EditorInfo.IME_ACTION_NEXT
            || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)
        ) {
            nextFocusView.requestFocus()
            true
        }

        false
    }
}


fun EditText.disableEmojis() {
    val emojiFilter = InputFilter { source, start, end, dest, dstart, dend ->
        val result = StringBuilder()
        var keepOriginal = true
        for (index in start until end) {
            val type = Character.getType(source[index])
            val isEmojiType = type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()
            if (!isEmojiType) {
                result.append(source[index])
            } else {
                keepOriginal = false
            }
        }
        when {
            keepOriginal -> {
                null
            }
            source is Spanned -> {
                val spannableString = SpannableString(result)
                TextUtils.copySpansFrom(source, start, result.length, null, spannableString, 0)
                spannableString
            }
            else -> {
                result
            }
        }

    }
    this.addFilter(emojiFilter)
}
fun EditText.disableSpecialChars(vararg allowedChars: Char) {
    val emojiFilter = InputFilter { source, start, end, dest, dstart, dend ->
        val result = StringBuilder()
        var keepOriginal = true
        for (index in start until end) {
            val char = source[index]
            val isAllowChars = allowedChars.isNotEmpty()
            val isCharValid = allowedChars.contains(char)
            if(notAllowedChars.contains(char)){
                keepOriginal = false
            }else{
                if ( char.isLetterOrDigit()|| (isAllowChars && isCharValid) ) {
                    result.append(char)
                } else {
                    keepOriginal = false
                }
            }
        }
        when {
            keepOriginal -> {
                null
            }
            source is Spanned -> {
                val spannableString = SpannableString(result)
                TextUtils.copySpansFrom(source, start, result.length, null, spannableString, 0)
                spannableString
            }
            else -> {
                result
            }
        }
    }
    this.addFilter(emojiFilter)
}
fun EditText.acceptOnlyLetters(vararg allowedChars: Char) {
    val emojiFilter = InputFilter { source, start, end, dest, dstart, dend ->
        val result = StringBuilder()
        var keepOriginal = false
        for (index in start until end) {
            val char = source[index]
            val isAllowChars = allowedChars.isNotEmpty()
            val isCharValid = allowedChars.contains(char)
            if(notAllowedChars.contains(char)){
                keepOriginal = false
            }else{
                if ( char.isLetter()|| (isAllowChars && isCharValid) ) {
                    result.append(char)
                } else {
                    keepOriginal = false
                }
            }

        }
        when {
            keepOriginal -> {
                null
            }
            source is Spanned -> {
                val spannableString = SpannableString(result)
                TextUtils.copySpansFrom(source, start, result.length, null, spannableString, 0)
                spannableString
            }
            else -> {
                result
            }
        }
    }
    this.addFilter(emojiFilter)
}

fun EditText.acceptOnlyLettersAndNumbers(vararg allowedChars: Char) {
    val emojiFilter = InputFilter { source, start, end, dest, dstart, dend ->
        val result = StringBuilder()
        var keepOriginal = false
        for (index in start until end) {
            val char = source[index]
            val isAllowChars = allowedChars.isNotEmpty()
            val isCharValid = allowedChars.contains(char)
            if(notAllowedChars.contains(char)){
                keepOriginal = false
            }else{
                if ( char.isLetter()||char.isDigit() ||(isAllowChars && isCharValid) ) {
                    result.append(char)
                } else {
                    keepOriginal = false
                }
            }
        }
        when {
            keepOriginal -> {
                null
            }
            source is Spanned -> {
                val spannableString = SpannableString(result)
                TextUtils.copySpansFrom(source, start, result.length, null, spannableString, 0)
                spannableString
            }
            else -> {
                result
            }
        }
    }
    this.addFilter(emojiFilter)
}

@ExperimentalCoroutinesApi
@CheckResult
fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow {
        val listener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                trySend(s)
            }
        }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}
fun EditText.disableInput(value: Boolean) {
    isFocusable = !value
    isFocusableInTouchMode = !value
    isEnabled=!value
    this.inputType = if (value) InputType.TYPE_NULL else InputType.TYPE_CLASS_TEXT
}