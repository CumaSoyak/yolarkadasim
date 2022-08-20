package roadfriend.app.ui.profile.loginOrRegister

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import roadfriend.app.R
import roadfriend.app.base.BaseFragment
import roadfriend.app.databinding.LoginOrRegisterFragmentBinding
import roadfriend.app.enums.AuthProviders
import roadfriend.app.extension.*
import roadfriend.app.helper.FirebaseAuthHelper
import roadfriend.app.utils.PrefUtils


class LoginOrRegisterFragment :
    BaseFragment<LoginOrRegisterFragmentBinding, LoginOrRegisterViewModel>() {

    private val firebaseAuthHelper: FirebaseAuthHelper by inject()
    private val registeredActivityForGoogleSignInResult = onGoogleSignInActivityResult()
    private lateinit var facebookCallbackManager: CallbackManager
    private lateinit var unregistrar: Unregistrar

    override val viewModel: LoginOrRegisterViewModel by viewModel()

    override fun onViewReady(bundle: Bundle?) {
        checkLoginStatus()
        initFacebookLogin()
        binding.btnContinue.setOnClickListener {
            binding.tvEmailWarning.isVisible = false
            fetchSignInMethodsForEmail(binding.etEmail.text.toString())
        }
        initKeyboardEventListener()
        binding.googleSignIn.setOnClickListener { signInWithGoogle() }
        binding.facebookLogin.setOnClickListener {
            firebaseAuthHelper.facebookLoginManager.logInWithReadPermissions(
                this,
                listOf("email", "public_profile")
            )
        }
        binding.ivClose.setOnClickListener {
        }
        binding.etEmail.disableEmojis()
        setTheEgg()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregistrar.unregister()
    }


    private fun initKeyboardEventListener() {
        unregistrar =
            KeyboardVisibilityEvent.registerEventListener(requireActivity()) { isKeyboardVisible ->
                lifecycleScope.launch {
                    if (isKeyboardVisible) {
                        val view = binding.flWelcome
                        view.animate().translationY(-view.width.toFloat()).duration = 200
                        delay(200)
                        view.animate().translationY(0f).duration = 0
                    }
                    binding.flWelcome.isVisible = !isKeyboardVisible
                }

            }
    }

    private fun initFacebookLogin() {
        facebookCallbackManager = CallbackManager.Factory.create()
        firebaseAuthHelper.facebookLoginManager.registerCallback(
            facebookCallbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    showLoading()
                    FirebaseAuthHelper.facebookLoginResult = loginResult
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            firebaseAuthHelper.requestForFacebookEmail()
                        }
                        completeFacebookLogin()
                    }
                }

                override fun onCancel() = Unit

                override fun onError(exception: FacebookException) = Unit
            }
        )
    }


    private fun signInWithGoogle() {
        val signInIntent = firebaseAuthHelper.googleSignInClient.signInIntent
        registeredActivityForGoogleSignInResult.launch(signInIntent)
    }

    private fun onGoogleSignInActivityResult(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                showLoading()
                completeGoogleSignIn()
            } else {
                toast("googleSignInError")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
    }


    private fun completeGoogleSignIn() {
        val email = firebaseAuthHelper.googleSignInUser?.email ?: ""
        firebaseAuthHelper.fetchSignInMethods(email) { isSuccessful, signInMethods ->
            if (isSuccessful) {
                when {
                    signInMethods.contains(AuthProviders.Email.value) && !signInMethods.contains(
                        AuthProviders.Google.value
                    ) -> {
                        val bundle = bundleOf(
                            "currentProvider" to AuthProviders.Email.value,
                            "newProvider" to AuthProviders.Google.value,
                            "email" to email
                        )
                        findNavController().navigate(
                            R.id.action_loginOrRegisterFragment_to_continueWithSocialFragment,
                            bundle
                        )
                    }
                    signInMethods.contains(AuthProviders.Facebook.value) && !signInMethods.contains(
                        AuthProviders.Google.value
                    ) -> {
                        val bundle = bundleOf(
                            "currentProvider" to AuthProviders.Facebook.value,
                            "newProvider" to AuthProviders.Google.value,
                            "email" to email
                        )
                        findNavController().navigate(
                            R.id.action_loginOrRegisterFragment_to_continueWithSocialFragment,
                            bundle
                        )
                    }
                    else -> {
                        firebaseAuthHelper.firebaseAuthWithGoogle { isFirebaseSuccessful ->
                            if (isFirebaseSuccessful) {
                                toast(firebaseAuthHelper.googleSignInUser?.displayName.toString())
                                PrefUtils.isLogin = true
                                requireActivity().finish()
                            } else {
                                toast("googleSignInError")
                            }
                        }
                    }
                }
            } else {
                // toast(get("biletdukkani_general_error_occured"))
            }
            hideLoading()
        }
    }

    private fun completeFacebookLogin() {
        val email = FirebaseAuthHelper.facebookEmail ?: ""
        if (email.isEmpty()) {
            toast(get("biletdukkani_general_error_occured"))
            hideLoading()
            return
        }
        firebaseAuthHelper.fetchSignInMethods(email) { isSuccessful, signInMethods ->
            if (isSuccessful) {
                when {
                    signInMethods.contains(AuthProviders.Email.value) && !signInMethods.contains(
                        AuthProviders.Facebook.value
                    ) -> {
                        val bundle = bundleOf(
                            "currentProvider" to AuthProviders.Email.value,
                            "newProvider" to AuthProviders.Facebook.value,
                            "email" to email
                        )
                        findNavController().navigate(
                            R.id.action_loginOrRegisterFragment_to_continueWithSocialFragment,
                            bundle
                        )
                    }
                    signInMethods.contains(AuthProviders.Google.value) && !signInMethods.contains(
                        AuthProviders.Facebook.value
                    ) -> {
                        val bundle = bundleOf(
                            "currentProvider" to AuthProviders.Google.value,
                            "newProvider" to AuthProviders.Facebook.value,
                            "email" to email
                        )
                        findNavController().navigate(
                            R.id.action_loginOrRegisterFragment_to_continueWithSocialFragment,
                            bundle
                        )
                    }
                    else -> {
                        firebaseAuthHelper.firebaseAuthWithFacebook { isFirebaseSuccessful ->
                            if (isFirebaseSuccessful) {
                                PrefUtils.isLogin = true
                                requireActivity().finish()
                            } else {
                                toast("facebookLoginError")
                            }
                        }
                    }
                }
            } else {
                toast(get("biletdukkani_general_error_occured"))
            }
            hideLoading()
        }
    }

    private fun checkLoginStatus() {
        if (firebaseAuthHelper.isLoggedIn()) {
            PrefUtils.isLogin = true
            requireActivity().finish()
        }
    }

    private fun fetchSignInMethodsForEmail(email: String) {
        if (!binding.etEmail.isValidEmail()) {
            binding.tvEmailWarning.isVisible = true
            return
        }
        showLoading()
        firebaseAuthHelper.fetchSignInMethods(email) { isSuccessful, signInMethods ->
            hideLoading()
            if (isSuccessful) {
                if (signInMethods.isNullOrEmpty()) {
                    val bundle = bundleOf("isLogin" to false, "email" to email)
                    findNavController().navigate(R.id.action_loginOrRegisterFragment_to_passwordFragment, bundle)
                } else if (signInMethods.contains(AuthProviders.Email.value)) {
                    val bundle = bundleOf("isLogin" to true, "email" to email)
                    findNavController().navigate(R.id.action_loginOrRegisterFragment_to_passwordFragment, bundle)
                } else if (signInMethods.contains(AuthProviders.Google.value) && !signInMethods.contains(
                        AuthProviders.Email.value
                    )
                ) {
                    val bundle = bundleOf(
                        "currentProvider" to AuthProviders.Google.value,
                        "newProvider" to AuthProviders.Email.value,
                        "email" to email
                    )
                    findNavController().navigate(
                        R.id.action_loginOrRegisterFragment_to_continueWithSocialFragment,
                        bundle
                    )
                } else if (signInMethods.contains(AuthProviders.Facebook.value) && !signInMethods.contains(
                        AuthProviders.Email.value
                    )
                ) {
                    val bundle = bundleOf(
                        "currentProvider" to AuthProviders.Facebook.value,
                        "newProvider" to AuthProviders.Email.value,
                        "email" to email
                    )
                    findNavController().navigate(
                        R.id.action_loginOrRegisterFragment_to_continueWithSocialFragment,
                        bundle
                    )
                }
            } else {
                 toast(get("biletdukkani_general_error_occured"))
            }
        }
    }

    override fun createBinding(): LoginOrRegisterFragmentBinding {
        return LoginOrRegisterFragmentBinding.inflate(layoutInflater)
    }

    private fun setTheEgg() {
        var a = false
        var b = false
        var c: Boolean
        binding.tvOr.setOnLongClickListener {
            a.takeIf { a }?.let {
                a = true; b.takeIf { b }?.let {
                lifecycleScope.launch {
                    binding.ivPlane.apply {
                        visible(); Glide.with(this.context).asGif().load(com.google.android.material.R.drawable.ic_m3_chip_check)
                        .into(this); delay(1500); gone(); setImageDrawable(null)
                    }
                    a = false; b = false; c = false;
                }
            }
            } ?: kotlin.run { a = true }
            false
        }
        binding.tvOr.setOnClickListener { c = true; c.takeIf { c }?.let { b = true } }
    }
}