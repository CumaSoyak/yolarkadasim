package roadfriend.app.base

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import roadfriend.app.helper.DispatchGroup

abstract class BaseViewModel : ViewModel(), KoinComponent {
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val errorMessageAlerter = MutableLiveData<String>()
    val succesMessage = MutableLiveData<String>()
    val succesMessageAlerter = MutableLiveData<String>()
    val dispatchGroup = DispatchGroup()

    fun showLoading() {
        if (dispatchGroup.count == 0) {
            isLoading.postValue(true)
        }
        dispatchGroup.enter()
    }

    fun hideLoading() {
        dispatchGroup.leave()
        dispatchGroup.notify {
            isLoading.postValue(false)
        }
    }

    fun showError(message: String) {
        errorMessage.postValue(message)
    }

    fun showSucces(message: String) {
        succesMessage.postValue(message)
    }

    fun showErrorAlerter(message: String) {
        errorMessageAlerter.postValue(message)
    }

    fun showSuccesAlerter(message: String) {
        succesMessageAlerter.postValue(message)
    }



}

