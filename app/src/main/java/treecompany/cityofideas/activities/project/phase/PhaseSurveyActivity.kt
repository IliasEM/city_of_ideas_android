package treecompany.cityofideas.activities.project.phase

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import treecompany.cityofideas.R

class PhaseSurveyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phase_survey)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
