package treecompany.cityofideas.activities.project.project

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
import android.widget.ImageView
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
import treecompany.cityofideas.activities.qrscanner.QrScannerActivity
import treecompany.cityofideas.adapters.phase.PhaseAdapter
import treecompany.cityofideas.api.IMyAPI
import treecompany.cityofideas.common.Common
import treecompany.cityofideas.responses.ProjectResponse
import treecompany.cityofideas.responses.VoteLikeResponse
import treecompany.cityofideas.utils.PreConfig

class ProjectDetailActivity : AppCompatActivity() {

    private lateinit var compositeDisposable: CompositeDisposable

    private lateinit var project_detail_title: TextView
    private lateinit var project_detail_content: TextView
    private lateinit var project_detail_votes: TextView
    private lateinit var project_detail_likes: TextView
    private lateinit var project_detail_image: ImageView
    private lateinit var project_detail_voteButton: FloatingActionButton
    private lateinit var project_detail_likeButton: FloatingActionButton
    private lateinit var project_share_button: FloatingActionButton
    private lateinit var phase_recycler: RecyclerView
    private lateinit var projectId: String

    private var userId: String = ""

    companion object {
        lateinit var preConfig: PreConfig
        internal lateinit var mService: IMyAPI
        private const val ACTIVITY_NUM = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        initialise()
        toolbarSetup()
        setUpBottomNavigationView()
        projectId = intent.getStringExtra("projectid")
        getIncomingIntent()
        addEventHandlers()

        userId = preConfig.getUserId()
    }

    fun initialise() {
        compositeDisposable = CompositeDisposable()
        preConfig = PreConfig(this)
        mService = Common.api
        project_detail_title = findViewById(R.id.project_detail_title)
        project_detail_content = findViewById(R.id.project_detail_content)
        project_detail_votes = findViewById(R.id.project_detail_votes)
        project_detail_likes = findViewById(R.id.project_detail_likes)
        project_detail_image = findViewById(R.id.project_detail_image)
        project_detail_voteButton = findViewById(R.id.project_detai_voteButton)
        project_detail_likeButton = findViewById(R.id.project_detai_likeButton)
        project_share_button = findViewById(R.id.project_detai_shareButton)
        phase_recycler = findViewById(R.id.phase_recycler)
        phase_recycler.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
    }

    private fun getIncomingIntent() {
        if (intent.hasExtra("projectid")) {
            loadData(projectId)
        }
    }

    private fun loadData(id: String) {
        val disposable: Disposable = mService.getProject(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Consumer<ProjectResponse> {
                override fun accept(t: ProjectResponse?) {
                    if (t!!.error) {
                        project_detail_title.text = t.project.title
                        project_detail_content.text = t.project.content

                        val disposable1 : Disposable = mService.checkUserVoted(projectId, userId)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(object : Consumer<VoteLikeResponse> {
                                override fun accept(t: VoteLikeResponse?) {
                                    if (t!!.error) {
                                        project_detail_voteButton.setImageDrawable(resources.getDrawable(R.drawable.ic_vote_yes))
                                    } else {
                                        project_detail_voteButton.setImageDrawable(resources.getDrawable(R.drawable.ic_vote_not))
                                    }
                                }
                            })

                        val disposable2 : Disposable = mService.checkUserLiked(projectId, userId)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(object : Consumer<VoteLikeResponse> {
                                override fun accept(t: VoteLikeResponse?) {
                                    if (t!!.error) {
                                        project_detail_likeButton.setImageDrawable(resources.getDrawable(R.drawable.ic_like_yes))
                                    } else {
                                        project_detail_likeButton.setImageDrawable(resources.getDrawable(R.drawable.ic_like_not))
                                    }
                                }
                            })

                        compositeDisposable.addAll(disposable1, disposable2)

                        if (t.project.voteProjects == 0) {
                            project_detail_votes.text = "" + 0
                        } else {
                            project_detail_votes.text = "" + t.project.voteProjects
                        }

                        if (t.project.likeProjects == 0) {
                            project_detail_likes.text = "" + 0
                        } else {
                            project_detail_likes.text = "" + t.project.likeProjects
                        }
                        project_detail_image.setImageResource(
                            resources.getIdentifier(
                                splitImagePath(t.project.image),
                                "drawable",
                                packageName
                            )
                        )
                        phase_recycler.adapter =
                            PhaseAdapter(applicationContext, t.project.phases)
                    } else {
                        preConfig.displayToast(t.toString())
                    }
                }
            }, object : Consumer<Throwable> {
                override fun accept(t: Throwable?) {
                    preConfig.displayToast(t!!.message.toString())
                }
            })
        compositeDisposable.add(disposable)
    }


    fun addEventHandlers() {
        project_detail_voteButton.setOnClickListener {
            if (preConfig.readLoginStatus()) {
                if (project_detail_voteButton.drawable.constantState!!.equals(resources.getDrawable(R.drawable.ic_vote_yes).constantState)) {
                    val disposable: Disposable = mService.revokeVoteOnProject(projectId, userId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(object : Consumer<VoteLikeResponse> {
                            override fun accept(t: VoteLikeResponse?) {
                                if (t!!.error) {
                                    loadData(projectId)
                                } else {
                                    preConfig.displayToast(t.message)
                                }
                            }
                        }, object : Consumer<Throwable> {
                            override fun accept(t: Throwable?) {
                                preConfig.displayToast(t!!.message.toString())
                            }
                        })

                    compositeDisposable.add(disposable)
                } else {
                    val disposable1: Disposable = mService.voteOnProject(projectId, userId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(object : Consumer<VoteLikeResponse> {
                            override fun accept(t: VoteLikeResponse?) {
                                if (t!!.error) {
                                    loadData(projectId)
                                } else {
                                    preConfig.displayToast(t.message)
                                }
                            }
                        }, object : Consumer<Throwable> {
                            override fun accept(t: Throwable?) {
                                preConfig.displayToast(t!!.message.toString())
                            }
                        })

                    compositeDisposable.add(disposable1)
                }

                loadData(projectId)
            } else {
                val intent: Intent = Intent(this, AccountActivity::class.java)
                preConfig.displayToast("Je moet ingelogd zijn!")
                startActivity(intent)
            }
        }

        project_detail_likeButton.setOnClickListener {
            if (preConfig.readLoginStatus()) {
                if (project_detail_likeButton.drawable.constantState!!.equals(resources.getDrawable(R.drawable.ic_like_yes).constantState)) {
                    val disposable: Disposable = mService.revokeLikeOnProject(projectId, userId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(object : Consumer<VoteLikeResponse> {
                            override fun accept(t: VoteLikeResponse?) {
                                if (t!!.error) {
                                    loadData(projectId)
                                } else {
                                    preConfig.displayToast(t.message)
                                }
                            }

                        }, object : Consumer<Throwable> {
                            override fun accept(t: Throwable?) {
                                preConfig.displayToast(t!!.message.toString())
                            }
                        })
                    compositeDisposable.add(disposable)
                } else {
                    val disposable1: Disposable = mService.likeOnProject(projectId, userId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(object : Consumer<VoteLikeResponse> {
                            override fun accept(t: VoteLikeResponse?) {
                                if (t!!.error) {
                                    loadData(projectId)
                                } else {
                                    preConfig.displayToast(t.message)
                                }
                            }
                        }, object : Consumer<Throwable> {
                            override fun accept(t: Throwable?) {
                                preConfig.displayToast(t!!.message.toString())
                            }
                        })
                    compositeDisposable.add(disposable1)
                }

                loadData(projectId)
            } else {
                val intent: Intent = Intent(this, AccountActivity::class.java)
                preConfig.displayToast("Je moet ingelogd zijn!")
                startActivity(intent)
            }
        }

        project_share_button.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            val shareSub: String = project_detail_title.text.toString()
            val shareBody: String = project_detail_content.text.toString()
            intent.putExtra(Intent.EXTRA_SUBJECT, shareSub)
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(intent, "Share using"))
        }
    }

    override fun onResume() {
        super.onResume()
        loadData(projectId)
    }

    fun splitImagePath(imagePath: String): String {
        val image = imagePath.split("""\\""".toRegex())
        val finalImage = image[2].split(".")
        return finalImage[0].toLowerCase()
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


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
