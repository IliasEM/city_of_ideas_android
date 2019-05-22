package treecompany.cityofideas.common

import treecompany.cityofideas.api.IMyAPI
import treecompany.cityofideas.api.RestClient

object Common {
    val api:IMyAPI
        get() = RestClient.getClient().create(IMyAPI::class.java)
}