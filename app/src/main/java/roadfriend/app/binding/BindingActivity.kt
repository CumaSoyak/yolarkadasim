package roadfriend.app.binding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BindingActivity<VB : ViewBinding> : AppCompatActivity() {

    lateinit var binding: VB

    abstract fun createBinding(): VB

    open fun onViewReady(bundle: Bundle?) = Unit

    open fun onViewListener() = Unit

    open fun onObserveState() = Unit

    open fun showLoading(isTransfer: Boolean = false) = Unit

    open fun hideLoading() = Unit

    open fun showErrorMessage(message: String) = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (::binding.isInitialized.not()) {
            binding = createBinding()
            setContentView(binding.root)
        }
        onObserveState()
        onViewReady(savedInstanceState)
        onViewListener()
    }

}