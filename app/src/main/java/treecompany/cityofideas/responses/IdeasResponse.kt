package treecompany.cityofideas.responses

import treecompany.cityofideas.models.ProjectClasses.IdeationClasses.Idea

data class IdeasResponse(
    val error: Boolean,
    val ideas : Array<Idea>
)