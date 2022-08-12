package roadfriend.app.di

 import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

import roadfriend.app.MainViewModel
 import roadfriend.app.ui.home.HomeViewModel

val viewModelModule = module {
    viewModel { MainViewModel() }
    viewModel { HomeViewModel() }


}