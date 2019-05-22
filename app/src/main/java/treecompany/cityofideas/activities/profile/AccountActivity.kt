package treecompany.cityofideas.activities.profile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import treecompany.cityofideas.R
import treecompany.cityofideas.api.IMyAPI
import treecompany.cityofideas.common.Common
import treecompany.cityofideas.fragments.profile.LoginFragment
import treecompany.cityofideas.utils.PreConfig


class AccountActivity : AppCompatActivity() {

    companion object{
        lateinit var preConfig : PreConfig
        internal lateinit  var mService : IMyAPI
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        initialise()
        if (preConfig.readLoginStatus()){
            val intent : Intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        else
        {
            replaceFragment(LoginFragment())
        }

    }

    fun initialise(){
        preConfig = PreConfig(this)
        mService = Common.api
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if((keyCode == KeyEvent.KEYCODE_BACK)){
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.profile_fragment_container, fragment)
        fragmentTransaction.commit()
    }
}
