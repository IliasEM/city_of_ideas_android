package treecompany.cityofideas.adapters.ideation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import treecompany.cityofideas.R
import treecompany.cityofideas.activities.project.phase.IdeaActivity
import treecompany.cityofideas.activities.project.phase.PhaseIdeationActivity
import treecompany.cityofideas.models.ProjectClasses.IdeationClasses.Idea


class IdeaAdapter(private val context: Context,private val ideas: Array<Idea>) : RecyclerView.Adapter<IdeaAdapter.ViewHolder>() {

    private var expandedPosition = -1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idea_title = itemView.findViewById(R.id.list_idea_title) as TextView
        val idea_desc = itemView.findViewById(R.id.list_idea_desc) as TextView
        val idea_image = itemView.findViewById(R.id.list_idea_image) as ImageView
        val line_image = itemView.findViewById(R.id.line_idea_image) as LinearLayout

        val idea_item = itemView.findViewById(R.id.list_idea_item) as CardView
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.list_ideation_idea_layout, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ideas.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val idea: Idea = ideas[p1]
        p0.idea_title.text = idea.title
        p0.idea_desc.text = idea.description
        p0.idea_image.setImageResource(context.resources.getIdentifier(splitImagePath(idea.image), "drawable", context.packageName))

        val isExpanded : Boolean = p1==expandedPosition
        if(isExpanded){
            p0.line_image.setVisibility(View.VISIBLE)
        }
        else
        {
            p0.line_image.setVisibility(View.GONE   )
        }
        p0.idea_item.setActivated(isExpanded)
        p0.idea_item.setOnLongClickListener {
            expandedPosition = if (isExpanded) -1 else p1
            notifyDataSetChanged()
            true
        }

        p0.idea_item.setOnClickListener {

            val intent : Intent = Intent(context, IdeaActivity::class.java)
            intent.putExtra("ideaid", idea.id)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent)
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