package treecompany.cityofideas.adapters.platform

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
import treecompany.cityofideas.R
import treecompany.cityofideas.activities.platform.PlatformActivity
import treecompany.cityofideas.activities.project.project.ProjectActivity
import treecompany.cityofideas.models.PlatformClasses.Platform

class PlatformAdapter(private val context : Context, val platforms : Array<Platform>) : RecyclerView.Adapter<PlatformAdapter.ViewHolder>() {

    class ViewHolder(view: View)  : RecyclerView.ViewHolder(view){
        val title = view.findViewById(R.id.platform_title) as TextView
        val location = view.findViewById(R.id.platform_location) as TextView
        val image = view.findViewById(R.id.platform_image) as ImageView

        val platform_card = view.findViewById(R.id.platform_card) as CardView
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.list_platform_layout, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int  = platforms.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val platform : Platform =  platforms[p1]
        p0.title.text = platform.name
        p0.image.setImageDrawable(context.resources.getDrawable(R.drawable.antwerp_logo))

        p0.platform_card.setOnClickListener {
            PlatformActivity.preConfig.savePlatform(platform.id)
            PlatformActivity.preConfig.savePlatformName(platform.name)
            val intent : Intent = Intent(context, ProjectActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent)
            val mContext = p0.itemView.getContext() as Activity
            mContext.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            PlatformActivity.preConfig.displayToast("Platform veranderd naar " + platform.name)
        }
    }

}