package org.itb.nominas.core.navigation

import kotlinx.serialization.Serializable

interface Screen {
    val route: String
}

@Serializable
object HomeRoute : Screen {
    override val route = "Home"
}

@Serializable
object LoginRoute

@Serializable
object ProfileRoute

@Serializable
object PayRollRoute : Screen {
    override val route = "rolconsulta"
}

@Serializable
object DeductionsRoute : Screen {
    override val route = "descuentos"
}

@Serializable
object AttendanceRoute : Screen {
    override val route = "registroasistencia"
}

@Serializable
object TrackerRoute : Screen {
    override val route = "bitacora-asistencia"
}