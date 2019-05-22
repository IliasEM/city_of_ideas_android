package treecompany.cityofideas.adapters.ideation

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import treecompany.cityofideas.R
import treecompany.cityofideas.activities.project.phase.IdeaActivity
import treecompany.cityofideas.models.ProjectClasses.IdeationClasses.Comment
import treecompany.cityofideas.models.ProjectClasses.IdeationClasses.Reply
import treecompany.cityofideas.responses.DefaultResponse
import treecompany.cityofideas.responses.RepliesResponse
import java.text.SimpleDateFormat

class CommentAdapter(private val context: Context, private val comments: Array<Comment>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    var sdf = SimpleDateFormat("yyyy-MM-dd HH:MM:ss")
    private var expandedPosition = -1
    private lateinit var adapter: ReplyAdapter
    private lateinit var replies_list: Array<Reply>

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val comment_image = itemView.findViewById(R.id.list_comment_image) as ImageView

        val comment_user = itemView.findViewById(R.id.list_comment_user) as TextView
        val comment_comment = itemView.findViewById(R.id.list_comment_comment) as TextView
        val comment_date = itemView.findViewById(R.id.list_comment_date) as TextView

        val replies_comment = itemView.findViewById(R.id.recycler_replies) as RecyclerView

        val add_reply_edit = itemView.findViewById(R.id.reply_edit) as EditText
        val add_reply_button = itemView.findViewById(R.id.reply_edit_button) as Button

        val report_comment = itemView.findViewById(R.id.report_comment) as ImageButton

        val expand_replies = itemView.findViewById(R.id.expand_replies) as LinearLayout
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.list_comment_idea, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(viewHolder: ViewHolder, p1: Int) {
        val comment: Comment = comments[p1]

        viewHolder.comment_image.setImageResource(
            context.resources.getIdentifier(
                splitImagePath(comment.user.image),
                "drawable",
                context.packageName
            )
        )
        viewHolder.comment_user.text = comment.user.userName
        viewHolder.comment_comment.text = comment.value
        viewHolder.comment_date.text = sdf.format(comment.postDate)

        viewHolder.replies_comment.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        replies_list = comment.replies
        adapter = ReplyAdapter(context, replies_list)
        viewHolder.replies_comment.adapter = adapter

        val isExpanded: Boolean = p1 == expandedPosition
        if (isExpanded) {
            viewHolder.expand_replies.setVisibility(View.VISIBLE)
        } else {
            viewHolder.expand_replies.setVisibility(View.GONE)
        }
        viewHolder.itemView.setActivated(isExpanded)
        viewHolder.itemView.setOnClickListener {
            expandedPosition = if (isExpanded) -1 else p1
            notifyDataSetChanged()
        }

        viewHolder.add_reply_button.setOnClickListener {
            val value = viewHolder.add_reply_edit.text

            if (value.isEmpty()) {
                viewHolder.add_reply_edit.setError("Reply is required")
                viewHolder.add_reply_edit.requestFocus()
            } else {
                IdeaActivity.mService.postReply(
                    comment.id,
                    value.toString(),
                    IdeaActivity.preConfig.getUserId()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Consumer<RepliesResponse> {
                        override fun accept(p0: RepliesResponse?) {
                            if (p0!!.error) {
                                IdeaActivity.preConfig.displayToast("Uw reactie is toegevoegd")
                                replies_list = p0.replies
                                viewHolder.replies_comment.adapter = ReplyAdapter(context, replies_list)

                                viewHolder.add_reply_edit.text = "".toEditable()
                            }
                        }
                    }, object : Consumer<Throwable> {
                        override fun accept(p0: Throwable?) {
                            IdeaActivity.preConfig.displayToast(p0!!.message.toString())
                        }
                    })
            }
        }

        viewHolder.report_comment.setOnClickListener{
            IdeaActivity.mService.reportComment(comment.id, IdeaActivity.preConfig.getUserId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Consumer<DefaultResponse>{
                    override fun accept(t: DefaultResponse?) {
                        if(t!!.error){
                            IdeaActivity.preConfig.displayToast("De comment is gereport!")
                        }
                    }

                }, object : Consumer<Throwable>{
                    override fun accept(t: Throwable?) {
                        IdeaActivity.preConfig.displayToast(t!!.message.toString())
                    }

                })
        }
    }


    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    fun splitImagePath(imagePath: String): String {
        val image = imagePath.toLowerCase().split("""\\""".toRegex())
        val finalImage = image[2].split(".")
        return finalImage[0]
    }
}