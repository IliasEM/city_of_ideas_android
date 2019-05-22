package treecompany.cityofideas.responses

import treecompany.cityofideas.models.ProjectClasses.Project

data class ProjectResponse(
    val error : Boolean,
    val project : Project
)