package treecompany.cityofideas.activities.qrscanner

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import treecompany.cityofideas.R
import treecompany.cityofideas.activities.platform.PlatformActivity
import treecompany.cityofideas.utils.PreConfig
import treecompany.cityofideas.activities.project.project.ProjectActivity
import treecompany.cityofideas.fragments.qrscanner.QrScannerFragment

class QrScannerActivity : AppCompatActivity() {

    companion object {
        lateinit var preConfig: PreConfig
        private const val ACTIVITY_NUM = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)
        initialise()
        toolbarSetup()
        setUpBottomNavigationView()
        replaceFragment(QrScannerFragment())
    }

    fun initialise() {
        preConfig = PreConfig(this)
    }

    private fun toolbarSetup() {
        val toolbar = findViewById(R.id.toolbar) as android.support.v7.widget.Toolbar
        toolbar.setTitle(ProjectActivity.preConfig.getPlatformName())
        setSupportActionBar(toolbar)
    }

    fun setUpBottomNavigationView() {
        val bottomNavigationViewEx: BottomNavigationViewEx =
            findViewById(R.id.bottomNavViewBar) as BottomNavigationViewEx
        PreConfig.setupBottomNavigationView(bottomNavigationViewEx)
        enableNavigation(this, bottomNavigationViewEx)
        val menu: Menu = bottomNavigationViewEx.menu
        val menuItem: MenuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.setChecked(true)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.qrscanner_fragment_container, fragment)
        fragmentTransaction.commit()
    }

    fun enableNavigation(context: Context, view: BottomNavigationViewEx) {
        view.onNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.ic_house -> {
                        val intent = Intent(context, ProjectActivity::class.java)
                        context.startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                    R.id.ic_platform -> {
                        val intent = Intent(context, PlatformActivity::class.java)
                        context.startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                }
                return false
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}
