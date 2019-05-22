package treecompany.cityofideas.activities.project.phase

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_idea.*
import treecompany.cityofideas.R
import treecompany.cityofideas.activities.profile.AccountActivity
import treecompany.cityofideas.adapters.ideation.CommentAdapter
import treecompany.cityofideas.api.IMyAPI
import treecompany.cityofideas.common.Common
import treecompany.cityofideas.models.ProjectClasses.IdeationClasses.Comment
import treecompany.cityofideas.responses.CommentAddResponse
import treecompany.cityofideas.responses.IdeaResponse
import treecompany.cityofideas.responses.VoteLikeResponse
import treecompany.cityofideas.utils.PreConfig


class IdeaActivity : AppCompatActivity() {

    private lateinit var compositeDisposable: CompositeDisposable

    private var ideaId: String = ""
    private var comment: String = ""

    private lateinit var idea_title: TextView
    private lateinit var idea_desc: TextView
    private lateinit var idea_image: ImageView

    private lateinit var comments_recycler: RecyclerView
    private lateinit var adapter: CommentAdapter
    private lateinit var comments: Array<Comment>

    private lateinit var add_comment: FloatingActionButton
    private lateinit var like_button: FloatingActionButton

    private lateinit var likes: TextView

    companion object {
        lateinit var preConfig: PreConfig
        internal lateinit var mService: IMyAPI
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idea)
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        initialise()
        ideaId = intent.getStringExtra("ideaid")
        getIncomingIntent()

        comments_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        addEventHandlers()
    }

    fun initialise() {
        preConfig = PreConfig(this)
        mService = Common.api
        compositeDisposable = CompositeDisposable()


        idea_title = findViewById(R.id.idea_title)
        idea_desc = findViewById(R.id.idea_desc)
        idea_image = findViewById(R.id.idea_image_upload)

        comments_recycler = findViewById(R.id.reycler_comments)
        comments = arrayOf()

        add_comment = findViewById(R.id.idea_add_comment)
        like_button = findViewById(R.id.idea_detail_likeButton)

        likes = findViewById(R.id.idea_detail_likes)
    }

    private fun addEventHandlers() {
        add_comment.setOnClickListener {

            if (preConfig.readLoginStatus()) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Reactie")

                val input = EditText(this)
                input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_CLASS_TEXT
                builder.setView(input)

                builder.setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, which ->
                        comment = input.text.toString()
                        addComment()
                    })
                builder.setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
                builder.show()
            } else {
                val intent: Intent = Intent(this, AccountActivity::class.java)
                preConfig.displayToast("Je moet ingelogd zijn!")
                startActivity(intent)
            }

        }

        like_button.setOnClickListener {
            if (preConfig.readLoginStatus()) {
                if (
                //idea_detail_likeButton.text.equals("Liked")
                    idea_detail_likeButton.drawable.constantState!!.equals(resources.getDrawable(R.drawable.ic_like_yes).constantState)
                ) {
                    val disposable: Disposable = mService.revokeLikeOnIdea(ideaId, preConfig.getUserId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(object : Consumer<VoteLikeResponse> {
                            override fun accept(t: VoteLikeResponse?) {
                                if (t!!.error) {
                                    loadData()
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
                    val disposable1: Disposable = mService.likeOnIdea(ideaId, preConfig.getUserId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(object : Consumer<VoteLikeResponse> {
                            override fun accept(t: VoteLikeResponse?) {
                                if (t!!.error) {
                                    loadData()
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
                loadData()
            } else {
                val intent: Intent = Intent(this, AccountActivity::class.java)
                preConfig.displayToast("Je moet ingelogd zijn!")
                startActivity(intent)
            }
        }
    }

    private fun getIncomingIntent() {
        if (intent.hasExtra("ideaid")) {
            loadData()
        }
    }

    private fun loadData() {
        val disposable: Disposable = mService.getIdea(ideaId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Consumer<IdeaResponse> {
                override fun accept(t: IdeaResponse?) {
                    if (t!!.error) {
                        idea_title.text = t.idea.title
                        idea_desc.text = t.idea.description
                        idea_image.setImageResource(
                            resources.getIdentifier(
                                splitImagePath(t.idea.image),
                                "drawable",
                                packageName
                            )
                        )
                        val disposable: Disposable = mService.checkUserLikedIdea(ideaId, preConfig.getUserId())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(object : Consumer<VoteLikeResponse> {
                                override fun accept(t: VoteLikeResponse?) {
                                    if (t!!.error) {
                                        like_button.setImageDrawable(resources.getDrawable(R.drawable.ic_like_yes))
                                    } else {
                                        like_button.setImageDrawable(resources.getDrawable(R.drawable.ic_like_not))
                                    }
                                }
                            })
                        compositeDisposable.add(disposable)

                        if (t.idea.likeIdeas == 0) {
                            likes.text = "" + 0
                        } else {
                            likes.text = "" + t.idea.likeIdeas
                        }
                        comments = t.comments
                        adapter = CommentAdapter(applicationContext, comments)
                        comments_recycler.adapter = CommentAdapter(applicationContext, comments)
                    } else {
                        preConfig.displayToast("niet gelukt")
                    }
                }
            }, object : Consumer<Throwable> {
                override fun accept(t: Throwable?) {
                    preConfig.displayToast(t!!.message.toString())
                }
            })
        compositeDisposable.add(disposable)
    }

    private fun addComment() {
        val disposable: Disposable = mService.postComment(
            ideaId,
            comment,
            preConfig.getUserId()
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Consumer<CommentAddResponse> {
                override fun accept(p0: CommentAddResponse?) {
                    if (p0!!.error) {
                        preConfig.displayToast("Uw reactie is toegevoegd")
                        comments = p0.comments
                        comments_recycler.adapter = CommentAdapter(applicationContext, comments)
                    }
                }
            }, object : Consumer<Throwable> {
                override fun accept(p0: Throwable?) {
                    preConfig.displayToast(p0!!.message.toString())
                }
            })
        compositeDisposable.add(disposable)
    }

    fun splitImagePath(imagePath: String): String {
        val image = imagePath.toLowerCase().split("""\\""".toRegex())
        val finalImage = image[2].split(".")
        return finalImage[0]
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
