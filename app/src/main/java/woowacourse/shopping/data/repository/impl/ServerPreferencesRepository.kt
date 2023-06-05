package woowacourse.shopping.data.repository.impl

import android.content.Context
import androidx.preference.PreferenceManager
import woowacourse.shopping.data.repository.ServerStoreRespository

class ServerPreferencesRepository(context: Context) : ServerStoreRespository {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun setServerUrl(url: String) {
        val editor = sharedPreferences.edit()
        editor.putString(SERVER, url).apply()
    }

    override fun getServerUrl(): String = sharedPreferences.getString(SERVER, "") ?: ""

    companion object {
        const val SERVER = "SERVER"
    }
}