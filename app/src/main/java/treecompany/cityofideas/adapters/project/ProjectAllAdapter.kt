package treecompany.cityofideas.adapters.project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import treecompany.cityofideas.R
import treecompany.cityofideas.activities.profile.AccountActivity
import treecompany.cityofideas.activities.project.project.ProjectActivity
import treecompany.cityofideas.activities.project.project.ProjectDetailActivity
import treecompany.cityofideas.models.ProjectClasses.Project
import treecompany.cityofideas.responses.VoteLikeResponse

class ProjectAllAdapter(private var context: Context?, private var projects: Array<Project>) : RecyclerView.Adapter<ProjectAllAdapter.ViewHolder>() {

    public var userId: String = ProjectActivity.preConfig.getUserId()

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val project_title = itemView.findViewById(R.id.list_project_title) as TextView
        val project_content = itemView.findViewById(R.id.list_project_content) as TextView
        val project_image = itemView.findViewById(R.id.list_project_image) as ImageView

        val project_like = itemView.findViewById(R.id.list_project_like) as ImageView
        val project_likes = itemView.findViewById(R.id.list_project_likes) as TextView
        val project_like_check = itemView.findViewById(R.id.list_project_like_check) as TextView
        val project_share = itemView.findViewById(R.id.list_project_share) as ImageView
        val project_item = itemView.findViewById(R.id.list_project_item) as CardView
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v : View
        v = LayoutInflater.from(p0.context).inflate(R.layout.list_project_layout, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return projects.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val project: Project = projects[p1]

        p0.project_title.setText(project.title)
        p0.project_content.setText(project.content)
        p0.project_image.setImageResource(context!!.resources.getIdentifier(splitImagePath(project.image), "drawable", context!!.packageName))

        p0.project_likes.text = "" + project.likeProjects


        ProjectActivity.mService.checkUserLiked(project.id, userId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Consumer<VoteLikeResponse> {
                override fun accept(t: VoteLikeResponse?) {
                    if (t!!.error) {
                        p0.project_like.setImageDrawable(context!!.getDrawable(R.drawable.ic_like_yes))
                        p0.project_like_check.text = "1"
                    }
                }
            })

        p0.project_like.setOnClickListener {
            if (ProjectActivity.preConfig.readLoginStatus()) {
                if (p0.project_like_check.text.equals("1")) {

                    ProjectActivity.mService.revokeLikeOnProject(
                        project.id,
                        userId
                    )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(object : Consumer<VoteLikeResponse> {
                            override fun accept(t: VoteLikeResponse?) {
                                if (t!!.error) {
                                    p0.project_like.setImageDrawable(context!!.getDrawable(R.drawable.ic_like_not))
                                    p0.project_like_check.text = "0"
                                    var likes = p0.project_likes.text.toString().toInt()
                                    likes = likes - 1
                                    p0.project_likes.text = "" + likes
                                } else {
                                    ProjectActivity.preConfig.displayToast(t.message)
                                }
                            }

                        }, object : Consumer<Throwable> {
                            override fun accept(t: Throwable?) {
                                ProjectActivity.preConfig.displayToast(t!!.message.toString())
                            }
                        })
                } else {
                    ProjectActivity.mService.likeOnProject(
                        project.id,
                        userId
                    )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(object : Consumer<VoteLikeResponse> {
                            override fun accept(t: VoteLikeResponse?) {
                                if (t!!.error) {
                                    p0.project_like.setImageDrawable(context!!.getDrawable(R.drawable.ic_like_yes))
                                    p0.project_like_check.text = "1"
                                    var likes = p0.project_likes.text.toString().toInt()
                                    likes = likes + 1
                                    p0.project_likes.text = "" + likes
                                } else {
                                    ProjectActivity.preConfig.displayToast(t.message)
                                }
                            }
                        }, object : Consumer<Throwable> {
                            override fun accept(t: Throwable?) {
                                ProjectActivity.preConfig.displayToast(t!!.message.toString())
                            }
                        })
                }
            } else {
                val intent: Intent = Intent(context, AccountActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ProjectActivity.preConfig.displayToast("Je moet ingelogd zijn!")
                context!!.startActivity(intent)
                val mContext = p0.itemView.getContext() as Activity
                mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        p0.project_share.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            val shareSub: String = project.title
            val shareBody: String = project.content
            intent.putExtra(Intent.EXTRA_SUBJECT, shareSub)
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            context!!.startActivity(Intent.createChooser(intent, "Share using"))
        }


        p0.project_item.setOnClickListener {
            val intent : Intent = Intent(context, ProjectDetailActivity::class.java)
            intent.putExtra("projectid", project.id)
            context?.startActivity(intent)
            val mContext = p0.itemView.getContext() as Activity
            mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

    }

    fun splitImagePath(imagePath : String) : String{
        val image = imagePath.toLowerCase().split("""\\""".toRegex())
        val finalImage = image[2].split(".")
        return finalImage[0]
    }
}