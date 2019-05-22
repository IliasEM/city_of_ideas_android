package treecompany.cityofideas.responses

import treecompany.cityofideas.models.ProjectClasses.IdeationClasses.Ideation
import treecompany.cityofideas.models.ProjectClasses.PhaseClasses.Phase

data class PhaseIdeationResponse(
    val error: Boolean,
    val ideation: Ideation
)