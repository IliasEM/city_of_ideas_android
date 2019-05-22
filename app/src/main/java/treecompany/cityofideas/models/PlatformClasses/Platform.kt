package treecompany.cityofideas.models.PlatformClasses

import treecompany.cityofideas.models.LazyLoader

data class Platform(
    val lazyLoader: LazyLoader,
    val id: String,
    val isDistrict: Boolean,
    val name: String,
    val url: String,
    val logo: String
)