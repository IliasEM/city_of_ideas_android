package treecompany.cityofideas.models.UserClasses

import treecompany.cityofideas.models.LazyLoader
import treecompany.cityofideas.models.LazyLoader2
import java.util.*

data class Address(
    val lazyLoader : LazyLoader,
    val id : String,
    val street : String,
    val zipcode :  String,
    val city:  String
){
    constructor() : this(
        LazyLoader(), "",
        "", "", ""
    )
}