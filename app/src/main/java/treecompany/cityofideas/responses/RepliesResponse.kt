package treecompany.cityofideas.responses

import treecompany.cityofideas.models.ProjectClasses.IdeationClasses.Reply

data class RepliesResponse(
    val error: Boolean,
    val replies : Array<Reply>
)