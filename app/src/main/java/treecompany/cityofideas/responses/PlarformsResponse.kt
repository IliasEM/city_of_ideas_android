package treecompany.cityofideas.responses

import android.text.BoringLayout
import treecompany.cityofideas.models.PlatformClasses.Platform

data class PlarformsResponse(
    val error: Boolean,
    val platforms : Array<Platform>
)