package treecompany.cityofideas.models.ProjectClasses.IdeationClasses

import treecompany.cityofideas.models.LikeClasses.LikeIdea
import treecompany.cityofideas.models.UserClasses.User

data class Idea(
    val id: String,
    val user: User,
    val title: String,
    val description: String,
    val map: String,
    val video: String,
    val image: String,
    val verified: Boolean,
    val comments: List<Comment>,
    val likeIdeas : Int,
    val tags: List<Tag>
)