package treecompany.cityofideas.models.ProjectClasses.IdeationClasses

import treecompany.cityofideas.models.LazyLoader
import treecompany.cityofideas.models.UserClasses.User
import java.util.*

data class Reply(
    val user: User,
    val lazyLoader: LazyLoader,
    val id: String,
    val value: String,
    val postDate: Date
)