package presentation.androidAutoTest

import com.kg.gettransfer.presentation.data.Constants
import okhttp3.OkHttpClient
import okhttp3.Request

class NetworkRequests {

    @Suppress("UnsafeCallOnNullableType")
    fun receiveEmail(urlChangeEmail: String, apiKey: String): CharSequence {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(urlChangeEmail)
            .get()
            .addHeader(Constants.TEXT_HEADER, apiKey)
            .build()
        val response = client.newCall(request).execute()
        val converted = response.body()!!.string()
        return converted
    }

    fun emptyInbox(urlEmptyInbox: String, apiKey: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(urlEmptyInbox)
            .delete()
            .addHeader(Constants.TEXT_HEADER, apiKey)
            .build()
        val response = client.newCall(request).execute()
    }
}
