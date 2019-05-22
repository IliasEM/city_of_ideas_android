package treecompany.cityofideas.responses

import android.text.BoringLayout
import treecompany.cityofideas.models.UserClasses.User

data class ProfileResponse(
    val error : Boolean,
    val user : User,
    val votes : Int,
    val likes : Int
)