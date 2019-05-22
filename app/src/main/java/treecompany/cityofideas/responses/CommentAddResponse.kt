package treecompany.cityofideas.responses

import treecompany.cityofideas.models.ProjectClasses.IdeationClasses.Comment

data class CommentAddResponse(
    val error: Boolean,
    val comments : Array<Comment>
)