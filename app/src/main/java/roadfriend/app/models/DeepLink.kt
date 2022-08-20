package roadfriend.app.models

import android.net.Uri

data class DeepLink(var uriString: String, var queries: Map<String, String>)
