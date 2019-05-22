package treecompany.cityofideas.responses

import treecompany.cityofideas.models.UserClasses.User

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val userId: String
)