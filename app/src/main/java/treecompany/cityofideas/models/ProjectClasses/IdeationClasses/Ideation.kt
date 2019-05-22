package treecompany.cityofideas.models.ProjectClasses.IdeationClasses

import treecompany.cityofideas.models.LazyLoader2
import treecompany.cityofideas.models.LikeClasses.LikeIdea
import treecompany.cityofideas.models.LikeClasses.LikeIdeation
import treecompany.cityofideas.models.VoteClasses.VoteIdeation

data class Ideation(
    val voteIdeations: List<VoteIdeation>,
    val likeIdeations: List<LikeIdeation>,
    val isPropertyEnabled: List<IsPropertyEnabled>,
    val lazyLoader: LazyLoader2,
    val id: String,
    val question: String,
    val description: String,
    val link: String,
    val adminOnly: Boolean
)