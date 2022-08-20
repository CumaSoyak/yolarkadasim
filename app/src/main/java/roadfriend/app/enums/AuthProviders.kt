
package roadfriend.app.enums

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider


enum class AuthProviders(val value:String) {
    Email(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD),
    Google(GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD),
    Facebook(FacebookAuthProvider.FACEBOOK_SIGN_IN_METHOD)
}