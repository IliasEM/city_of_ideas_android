package treecompany.cityofideas.responses

import treecompany.cityofideas.models.ProjectClasses.IdeationClasses.Comment
import treecompany.cityofideas.models.ProjectClasses.IdeationClasses.Idea

data class IdeaResponse(
    val error: Boolean,
    val idea : Idea,
    val comments : Array<Comment>
)