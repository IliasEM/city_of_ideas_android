package treecompany.cityofideas.responses

data class RegisterResponse(
    val error : Boolean,
    val message: String,
    val userId : String
)