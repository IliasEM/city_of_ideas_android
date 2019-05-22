package treecompany.cityofideas.models.ProjectClasses.IdeationClasses

import treecompany.cityofideas.models.LazyLoader5
import treecompany.cityofideas.models.UserClasses.User
import java.util.*

data class Comment(
    val user: User,
    val lazyLoader: LazyLoader5,
    val id: String,
    val value: String,
    val postDate: Date,
    val replies : Array<Reply>
)