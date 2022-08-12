package roadfriend.app.ui.home

import android.os.Bundle
import org.koin.androidx.viewmodel.ext.android.viewModel
import roadfriend.app.base.BaseFragment
import roadfriend.app.databinding.FragmentHomeBinding

class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by viewModel()

    override fun createBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onViewReady(bundle: Bundle?) {
    }

    override fun onObserveState() {
        super.onObserveState()

    }

    override fun onViewListener() {
        super.onViewListener()

    }

}