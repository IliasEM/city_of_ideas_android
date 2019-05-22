package treecompany.cityofideas.models.UserClasses

import treecompany.cityofideas.models.LazyLoader2
import treecompany.cityofideas.models.PlatformClasses.Platform
import java.util.*

data class User(
    val address: Address,
    val id: String,
    val userName: String,
    val normalizedUserName: String = "",
    val email: String,
    val normalizedEmail: String = "",
    val emailConfirmed: Boolean,
    val passwordHash: String = "",
    val securityStamp: String = "",
    val concurrencyStamp: String = "",
    val phoneNumber: String,
    val phoneNumberConfirmed: Boolean = false,
    val twoFactorEnabled: Boolean = false,
    val lockoutEnd: Date = Date(),
    val lockoutEnabled: Boolean = false,
    val accessFailedCount: Int = 0,
    val lazyLoader: LazyLoader2,
    val isOauth: Boolean,
    val image: String,
    val roleName: String
){
    constructor() : this(
        Address(), "",
        "", "", "",
        "", false, "",
        "", "","",false, false, Date(), false, -1, LazyLoader2(),false,"",""
    )
}