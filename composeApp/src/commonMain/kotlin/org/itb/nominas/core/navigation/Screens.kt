package org.itb.nominas.core.navigation

import kotlinx.serialization.Serializable

interface Screen {
    val route: String
}

@Serializable
object Home : Screen {
    override val route = "Home"
}

@Serializable
object Login

@Serializable
object Profile

@Serializable
object PayRoll : Screen {
    override val route = "rolconsulta"
}

@Serializable
object Deductions : Screen {
    override val route = "descuentos"
}