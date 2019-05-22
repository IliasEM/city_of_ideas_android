package treecompany.cityofideas.models.ProjectClasses.IdeationClasses

import treecompany.cityofideas.models.LazyLoader

data class IsPropertyEnabled(
    val lazyLoader: LazyLoader,
    val id: String,
    val value: String,
    val enabled: Boolean
)