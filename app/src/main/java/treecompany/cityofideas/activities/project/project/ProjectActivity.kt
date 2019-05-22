package treecompany.cityofideas.activities.project.project

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import treecompany.cityofideas.R
import treecompany.cityofideas.activities.platform.PlatformActivity
import treecompany.cityofideas.utils.PreConfig
import treecompany.cityofideas.activities.profile.AccountActivity
import treecompany.cityofideas.activities.qrscanner.QrScannerActivity
import treecompany.cityofideas.api.IMyAPI
import treecompany.cityofideas.common.Common
import treecompany.cityofideas.fragments.project.ProjectAllFragment
import treecompany.cityofideas.fragments.project.ProjectPopularFragment
import treecompany.cityofideas.fragments.project.SectionsProjectPagerAdapter

class ProjectActivity : AppCompatActivity() {

    companion object{
        lateinit var preConfig : PreConfig
        internal lateinit  var mService : IMyAPI
        private const val ACTIVITY_NUM = 1    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        initialise()
        toolbarSetup()
        setUpBottomNavigationView()
        setupViewPager()
    }

    fun initialise(){
        preConfig = PreConfig(this)
        mService = Common.api
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_account_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        if (id == R.id.menu_profile) {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    fun setUpBottomNavigationView(){
        val bottomNavigationViewEx: BottomNavigationViewEx = findViewById(R.id.bottomNavViewBar) as BottomNavigationViewEx
        PreConfig.setupBottomNavigationView(bottomNavigationViewEx)
        enableNavigation(this, bottomNavigationViewEx)
        val menu : Menu = bottomNavigationViewEx.menu
        val menuItem : MenuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.setChecked(true)
    }

    private fun toolbarSetup() {
        val toolbar = findViewById(R.id.toolbar) as android.support.v7.widget.Toolbar
        toolbar.setTitle(preConfig.getPlatformName())
        setSupportActionBar(toolbar)
    }

    private fun setupViewPager(){
        val adapter : SectionsProjectPagerAdapter = SectionsProjectPagerAdapter(supportFragmentManager)
        adapter.addFragment(ProjectPopularFragment())
        adapter.addFragment(ProjectAllFragment())
        val viewPager : ViewPager = findViewById(R.id.viewpager)
        viewPager.adapter = adapter

        val tabLayout : TabLayout = findViewById(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)

        tabLayout.getTabAt(0)?.setText("Populaire/Recente projecten")
        tabLayout.getTabAt(1)?.setText("Alle projecten")
    }

    fun enableNavigation(context: Context, view : BottomNavigationViewEx){
        view.onNavigationItemSelectedListener = object: BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.ic_house -> {
                        val intent = Intent(context, ProjectActivity::class.java)
                        context.startActivity(intent)
                        overridePendingTransition(0,0)
                    }
                    R.id.ic_platform ->{
                        val intent = Intent(context, PlatformActivity::class.java)
                        context.startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                    R.id.ic_qr_code->{
                        val intent = Intent(context, QrScannerActivity::class.java)
                        context.startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    }
                }
                return false
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
