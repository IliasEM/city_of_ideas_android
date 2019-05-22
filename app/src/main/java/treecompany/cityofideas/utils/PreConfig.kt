package treecompany.cityofideas.utils

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import treecompany.cityofideas.R

class PreConfig(private val context: Context) {
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_file), Context.MODE_PRIVATE)
    }

    companion object{
        fun setupBottomNavigationView(bottomNavigationViewEx: BottomNavigationViewEx){
            bottomNavigationViewEx.enableAnimation(false)
            bottomNavigationViewEx.enableItemShiftingMode(false)
            bottomNavigationViewEx.enableShiftingMode(false)
            bottomNavigationViewEx.setTextVisibility(false)
        }


    }

    fun writeLoginStatus(status: Boolean) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(context.getString(R.string.pref_login_status), status)
        editor.apply()
    }

    fun readLoginStatus(): Boolean {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_login_status), false)
    }

    fun writeEmail(email: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(context.getString(R.string.pref_email), email)
        editor.apply()
    }

    fun readEmail(): String {
        return sharedPreferences.getString(context.getString(R.string.pref_email), "User") as String
    }

    fun displayToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun saveUserId(userId: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        //user
        editor.putString("userId", userId)
        editor.apply()
    }

    fun getUserId(): String {
        return sharedPreferences.getString("userId", "") as String
    }

    fun savePlatform(platformId: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("platformId", platformId)
        editor.apply()
    }

    fun getPlatform(): String {
        return sharedPreferences.getString("platformId", "Alle") as String
    }

    fun savePlatformName(platformName: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("platformName", platformName)
        editor.apply()
    }

    fun getPlatformName(): String {
        return sharedPreferences.getString("platformName", "Alle") as String
    }
}