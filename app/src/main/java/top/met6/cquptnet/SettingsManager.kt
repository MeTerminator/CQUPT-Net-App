package top.met6.cquptnet

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cqupt_net_prefs", Context.MODE_PRIVATE)

    var keepLogin: Boolean
        get() = prefs.getBoolean("keep_login", false)
        set(value) = prefs.edit().putBoolean("keep_login", value).apply()

    var studentId: String
        get() = prefs.getString("student_id", "") ?: ""
        set(value) = prefs.edit().putString("student_id", value).apply()

    var password: String
        get() = prefs.getString("password", "") ?: ""
        set(value) = prefs.edit().putString("password", value).apply()

    var isp: String
        get() = prefs.getString("isp", "xyw") ?: "xyw"
        set(value) = prefs.edit().putString("isp", value).apply()

    fun importBundledAccount(assets: AssetManager) {
        if (prefs.contains("student_id") || prefs.contains("password")) return

        val account = try {
            assets.open("accounts.json").bufferedReader().use { JSONObject(it.readText()) }
        } catch (_: IOException) {
            return
        } catch (_: JSONException) {
            return
        }

        val bundledStudentId = account.opt("studentId") as? String ?: return
        val bundledPassword = account.opt("password") as? String ?: return
        if (bundledStudentId.isBlank() || bundledPassword.isBlank()) return

        val editor = prefs.edit()
            .putString("student_id", bundledStudentId)
            .putString("password", bundledPassword)
        when (val bundledIsp = account.opt("isp") as? String) {
            "xyw", "telecom", "cmcc", "unicom" -> editor.putString("isp", bundledIsp)
        }
        editor.apply()
    }
}
