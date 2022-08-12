package roadfriend.app.extension

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun <T> LifecycleOwner.observeFlow(property: Flow<T>, isTriggerWhenActive:Boolean = false, block: (T?) -> Unit) {
    lifecycleScope.launch {
        property.collect {
            if (lifecycle.currentState == Lifecycle.State.RESUMED || isTriggerWhenActive){
                block.invoke(it)
            }
        }
    }
}

fun <T> AppCompatActivity.observeLiveData(property: LiveData<T>, isTriggerWhenActive:Boolean = false, block: (T?) -> Unit) {
    property.observe(this, Observer {
        if (lifecycle.currentState == Lifecycle.State.RESUMED || isTriggerWhenActive){
            block.invoke(it)
        }
    })
}

fun <T> Fragment.observeLiveData(property: LiveData<T>, isTriggerWhenActive:Boolean = false, block: (T) -> Unit) {
    property.observe(viewLifecycleOwner, Observer {
        if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED || isTriggerWhenActive){
            block.invoke(it)
        }
    })
}
