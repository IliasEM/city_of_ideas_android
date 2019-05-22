package treecompany.cityofideas.adapters.phase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import treecompany.cityofideas.R
import treecompany.cityofideas.activities.project.phase.PhaseIdeationActivity
import treecompany.cityofideas.activities.project.phase.PhaseSurveyActivity
import treecompany.cityofideas.activities.project.project.ProjectDetailActivity
import treecompany.cityofideas.models.ProjectClasses.PhaseClasses.Phase
import java.text.SimpleDateFormat

class PhaseAdapter(private val context : Context, private val phases : Array<Phase>) : RecyclerView.Adapter<PhaseAdapter.ViewHolder>() {
    var sdf = SimpleDateFormat("yyyy-MM-dd")

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val phase_number = itemView.findViewById(R.id.phase_number) as TextView
        val phase_start_date = itemView.findViewById(R.id.phase_start_date) as TextView
        val phase_end_date = itemView.findViewById(R.id.phase_end_date) as TextView

        val phase_item = itemView.findViewById(R.id.list_phase_item) as CardView
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.list_phase_layout, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int  = phases.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val phase : Phase = phases[p1]
        p0.phase_number.text = ""+phase.phaseNumber
        p0.phase_start_date.text = sdf.format(phase.startDate)
        p0.phase_end_date.text = sdf.format(phase.endDate)

        p0.phase_item.setOnClickListener {
            if(phase.isSurvey){
                val intent : Intent = Intent(context, PhaseSurveyActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                val mContext = p0.itemView.getContext() as Activity
                mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            else
            {
                val intent : Intent = Intent(context, PhaseIdeationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("phaseid", phase.ideation.id)
                context.startActivity(intent)
                val mContext = p0.itemView.getContext() as Activity
                mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }

}