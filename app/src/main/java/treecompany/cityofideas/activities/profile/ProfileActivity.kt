package treecompany.cityofideas.activities.profile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import treecompany.cityofideas.R
import treecompany.cityofideas.api.IMyAPI
import treecompany.cityofideas.common.Common
import treecompany.cityofideas.fragments.profile.ProfileAccountFragment
import treecompany.cityofideas.fragments.profile.ProfileOverviewFragment
import treecompany.cityofideas.fragments.profile.ProfileSecurityFragment
import treecompany.cityofideas.fragments.profile.SectionsProfilePagerAdapter
import treecompany.cityofideas.utils.PreConfig
import android.content.DialogInterface
import android.os.StrictMode
import android.support.v7.app.AlertDialog
import android.view.KeyEvent
import treecompany.cityofideas.activities.project.project.ProjectActivity


class ProfileActivity : AppCompatActivity() {

    companion object{
        lateinit var preConfig : PreConfig
        internal lateinit  var mService : IMyAPI
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        initialise()
        toolbarSetup()
        setupViewPager()
    }

    fun initialise(){
        preConfig = PreConfig(this)
        mService = Common.api
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        if (id == R.id.ic_logout) {
            alert()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun alert(){
        AlertDialog.Builder(this)
            .setMessage("Wil je zeker weten uitloggen")
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialogInterface, i -> logOut() })
            .setNegativeButton(android.R.string.cancel,
                DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() }).create().show()
    }

    fun logOut(){
        preConfig.writeLoginStatus(false)
        preConfig.saveUserId("")
        val intent : Intent = Intent(this, ProjectActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun toolbarSetup() {
        val toolbar = findViewById(R.id.toolbar) as android.support.v7.widget.Toolbar
        setSupportActionBar(toolbar)
    }

    private fun setupViewPager(){
        val adapter : SectionsProfilePagerAdapter = SectionsProfilePagerAdapter(supportFragmentManager)
        adapter.addFragment(ProfileOverviewFragment())
        adapter.addFragment(ProfileAccountFragment())
        adapter.addFragment(ProfileSecurityFragment())
        val viewPager : ViewPager = findViewById(R.id.viewpager)
        viewPager.adapter = adapter

        val tabLayout : TabLayout = findViewById(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)

        tabLayout.getTabAt(0)?.setText("Overzicht")
        tabLayout.getTabAt(1)?.setText("Account instellingen")
        tabLayout.getTabAt(2)?.setText("Beveiligings instellingen")
    }

    override fun finish() {
        super.finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if((keyCode == KeyEvent.KEYCODE_BACK)){
            val intent : Intent = Intent(this, ProjectActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        return super.onKeyDown(keyCode, event)
    }

}
