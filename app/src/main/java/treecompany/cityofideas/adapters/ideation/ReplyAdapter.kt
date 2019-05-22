package treecompany.cityofideas.adapters.ideation

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import treecompany.cityofideas.R
import treecompany.cityofideas.activities.project.phase.IdeaActivity
import treecompany.cityofideas.models.ProjectClasses.IdeationClasses.Reply
import treecompany.cityofideas.responses.DefaultResponse
import java.text.SimpleDateFormat

class ReplyAdapter(private val context: Context, private val replies: Array<Reply>) :
    RecyclerView.Adapter<ReplyAdapter.ViewHolder>() {

    var sdf = SimpleDateFormat("yyyy-MM-dd HH:MM:ss")

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reply_image = itemView.findViewById(R.id.list_reply_image) as ImageView

        val reply_user = itemView.findViewById(R.id.list_reply_user) as TextView
        val reply_comment = itemView.findViewById(R.id.list_reply_comment) as TextView
        val reply_date = itemView.findViewById(R.id.list_reply_date) as TextView

        val report_reply = itemView.findViewById(R.id.report_reply) as ImageButton
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.list_reply_comment, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = replies.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val reply: Reply = replies[p1]

        p0.reply_image.setImageResource(
            context.resources.getIdentifier(
                splitImagePath(reply.user.image),
                "drawable",
                context.packageName
            )
        )
        p0.reply_user.text = reply.user.userName
        p0.reply_comment.text = reply.value
        p0.reply_date.text = sdf.format(reply.postDate)

        p0.report_reply.setOnClickListener {
            IdeaActivity.mService.reportReply(reply.id, IdeaActivity.preConfig.getUserId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Consumer<DefaultResponse> {
                    override fun accept(p0: DefaultResponse) {
                        if(p0.error){
                            IdeaActivity.preConfig.displayToast("Reply is gerport!")
                        }
                    }

                }, object : Consumer<Throwable>{
                    override fun accept(t: Throwable?) {
                        IdeaActivity.preConfig.displayToast(t!!.message.toString())
                    }

                })
        }
    }

    fun splitImagePath(imagePath: String): String {
        val image = imagePath.toLowerCase().split("""\\""".toRegex())
        val finalImage = image[2].split(".")
        return finalImage[0]
    }
}