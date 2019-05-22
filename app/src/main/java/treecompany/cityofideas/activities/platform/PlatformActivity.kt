package treecompany.cityofideas.activities.platform

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import treecompany.cityofideas.R
import treecompany.cityofideas.activities.profile.AccountActivity
import treecompany.cityofideas.activities.project.project.ProjectActivity
import treecompany.cityofideas.activities.qrscanner.QrScannerActivity
import treecompany.cityofideas.adapters.platform.PlatformAdapter
import treecompany.cityofideas.api.IMyAPI
import treecompany.cityofideas.common.Common
import treecompany.cityofideas.responses.PlarformsResponse
import treecompany.cityofideas.utils.PreConfig


class PlatformActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var compositeDisposable: CompositeDisposable

    companion object{
        lateinit var preConfig : PreConfig
        internal lateinit  var mService : IMyAPI
        private const val ACTIVITY_NUM = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platform)
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        initialise()
        toolbarSetup()
        setUpBottomNavigationView()

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        loadPlatforms();

    }

    fun initialise(){
        compositeDisposable = CompositeDisposable()
        preConfig = PreConfig(this)
        mService = Common.api
        recyclerView = findViewById(R.id.platform_recycler)
    }

    private fun loadPlatforms() {
      val disposeable : Disposable =   mService.getAllPlatforms()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
            object: Consumer<PlarformsResponse> {
                override fun accept(p0: PlarformsResponse) {
                    if(p0.error){
                        recyclerView.adapter =
                            PlatformAdapter(applicationContext, p0.platforms)
                    }
                    else
                    {
                        //Als de view leeg is
                    }
                }
            }, object : Consumer<Throwable>{
                override fun accept(p0: Throwable) {
                    preConfig.displayToast(p0.message.toString())
                }
            })
        compositeDisposable.add(disposeable)
    }


    /***
     * sets the activity_account_menu to the toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_account_menu, menu)
        return true
    }

    /***
     * gives the menu_profile an action
     */
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

    /**
     * Bottom navigationView setup
     */
    fun setUpBottomNavigationView(){
        val bottomNavigationViewEx: BottomNavigationViewEx = findViewById(R.id.bottomNavViewBar) as BottomNavigationViewEx
        PreConfig.setupBottomNavigationView(bottomNavigationViewEx)
        enableNavigation(this, bottomNavigationViewEx)
        val menu : Menu = bottomNavigationViewEx.menu
        val menuItem : MenuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.setChecked(true)
    }

    /**
     * Setup the toolbar
     */
    private fun toolbarSetup() {
        val toolbar = findViewById(R.id.toolbar) as android.support.v7.widget.Toolbar
        setSupportActionBar(toolbar)
    }

    /***
     * enables bottom navigation
     */
    fun enableNavigation(context: Context, view : BottomNavigationViewEx){
        view.onNavigationItemSelectedListener = object: BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.ic_house -> {
                        val intent = Intent(context, ProjectActivity::class.java)
                        context.startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
                    }
                    R.id.ic_platform ->{
                        val intent = Intent(context, PlatformActivity::class.java)
                        context.startActivity(intent)
                        overridePendingTransition(0, 0)
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

    /***
     * if activity is finished do animation
     */
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    /***
     * if activity is finished clear the compositeDisposable
     */
    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
