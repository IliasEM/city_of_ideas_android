package treecompany.cityofideas.models.ProjectClasses

import treecompany.cityofideas.models.LazyLoader2
import treecompany.cityofideas.models.LikeClasses.LikeProject
import treecompany.cityofideas.models.PlatformClasses.Platform
import treecompany.cityofideas.models.ProjectClasses.PhaseClasses.Phase
import treecompany.cityofideas.models.VoteClasses.VoteProject
import java.sql.Date

data class Project(
    val phases: Array<Phase>,
    val platform: Platform,
    val voteProjects: Int,
    val likeProjects: Int,
    val lazyLoader: LazyLoader2,
    val id: String,
    val title: String,
    val location: String,
    val content: String,
    val startDate: Date,
    val endDate: Date,
    val image: String
)