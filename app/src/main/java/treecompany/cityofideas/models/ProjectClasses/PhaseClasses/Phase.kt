package treecompany.cityofideas.models.ProjectClasses.PhaseClasses

import treecompany.cityofideas.models.LazyLoader
import treecompany.cityofideas.models.ProjectClasses.IdeationClasses.Ideation
import java.util.*

data class Phase(
    val ideation: Ideation,
    val lazyLoader: LazyLoader,
    val id: String,
    val phaseNumber: Int,
    val startDate: Date,
    val endDate: Date,
    val isSurvey: Boolean
)