package treecompany.cityofideas.responses

import treecompany.cityofideas.models.ProjectClasses.Project

data class ProjectsResponse(
    val error : Boolean,
    val projects : Array<Project>
)