package treecompany.cityofideas.activities.project.phase

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import treecompany.cityofideas.R
import treecompany.cityofideas.activities.platform.PlatformActivity
import treecompany.cityofideas.activities.profile.AccountActivity
import treecompany.cityofideas.activities.project.project.ProjectActivity
import treecompany.cityofideas.activities.qrscanner.QrScannerActivity
import treecompany.cityofideas.adapters.ideation.IdeaAdapter
import treecompany.cityofideas.api.IMyAPI
import treecompany.cityofideas.common.Common
import treecompany.cityofideas.responses.IdeasResponse
import treecompany.cityofideas.responses.PhaseIdeationResponse
import treecompany.cityofideas.utils.PreConfig

class PhaseIdeationActivity : AppCompatActivity() {

    private var ideationId: String = ""
    private lateinit var compositeDisposable: CompositeDisposable

    private lateinit var phase_ideation_title: TextView
    private lateinit var phase_ideation_desc: TextView
    private lateinit var phase_ideation_add_idea: FloatingActionButton
    private lateinit var recycler: RecyclerView

    companion object {
        lateinit var preConfig: PreConfig
        internal lateinit var mService: IMyAPI
        private const val ACTIVITY_NUM = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phase_ideation)
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        initialise()
        toolbarSetup()
        setUpBottomNavigationView()
        ideationId = intent.getStringExtra("phaseid")
        getIncomingIntent()
        addEventHandlers()
    }

    fun initialise() {
        compositeDisposable = CompositeDisposable()
        preConfig = PreConfig(this)
        mService = Common.api
        phase_ideation_title = findViewById(R.id.phase_ideation_title)
        phase_ideation_desc = findViewById(R.id.phase_ideation_desc)
        phase_ideation_add_idea = findViewById(R.id.phase_ideation_add_button)
        recycler = findViewById(R.id.phase_ideation_recycler)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

    }

    private fun getIncomingIntent() {
        if (intent.hasExtra("phaseid")) {
            loadData()
        }
    }

    fun addEventHandlers() {
        phase_ideation_add_idea.setOnClickListener {
            if (preConfig.readLoginStatus()) {
                val intent: Intent = Intent(this, IdeaAddActivity::class.java)
                intent.putExtra("ideationId", ideationId)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } else {
                val intent: Intent = Intent(this, AccountActivity::class.java)
                preConfig.displayToast("Je moet ingelogd zijn!")
                startActivity(intent)
            }
        }
    }

    fun loadData() {
        val disposable: Disposable = mService.getIdeation(ideationId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Consumer<PhaseIdeationResponse> {
                override fun accept(t: PhaseIdeationResponse?) {
                    if (t!!.error) {
                        phase_ideation_title.text = t.ideation.question
                        phase_ideation_desc.text = t.ideation.description
                    }
                }
            }, object : Consumer<Throwable> {
                override fun accept(t: Throwable?) {
                    preConfig.displayToast(t!!.message.toString())
                }
            })
        compositeDisposable.add(disposable)

        val disposable1: Disposable = mService.getIdeationIdeas(ideationId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Consumer<IdeasResponse> {
                override fun accept(t: IdeasResponse?) {
                    if (t!!.error) {
                        recycler.adapter =
                            IdeaAdapter(applicationContext, t.ideas)
                    }
                }
            }, object : Consumer<Throwable> {
                override fun accept(t: Throwable?) {
                    preConfig.displayToast(t!!.message.toString())
                }
            })

        compositeDisposable.add(disposable1)
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

    fun setUpBottomNavigationView() {
        val bottomNavigationViewEx: BottomNavigationViewEx =
            findViewById(R.id.bottomNavViewBar) as BottomNavigationViewEx
        PreConfig.setupBottomNavigationView(bottomNavigationViewEx)
        enableNavigation(this, bottomNavigationViewEx)
        val menu: Menu = bottomNavigationViewEx.menu
        val menuItem: MenuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.setChecked(true)
    }

    private fun toolbarSetup() {
        val toolbar = findViewById(R.id.toolbar) as android.support.v7.widget.Toolbar
        toolbar.setTitle(ProjectActivity.preConfig.getPlatformName())
        setSupportActionBar(toolbar)
    }

    fun enableNavigation(context: Context, view: BottomNavigationViewEx) {
        view.onNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.ic_house -> {
                        val intent = Intent(context, ProjectActivity::class.java)
                        context.startActivity(intent)
                        overridePendingTransition(0, 0)
                    }
                    R.id.ic_platform -> {
                        val intent = Intent(context, PlatformActivity::class.java)
                        context.startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                    R.id.ic_qr_code -> {
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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
