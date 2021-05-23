package com.uisrael.worknow.Model.Data

import com.google.firebase.database.PropertyName

class CalificacionData (
    @PropertyName("calificacion") var calificacion: Double = 0.0,
    @PropertyName("uidPublicacion") var uidPublicacion: String = "")