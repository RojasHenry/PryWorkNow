package com.uisrael.worknow.Model.Data

import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName

class ComentariosData (
    @Exclude var uid: String = "",
    @PropertyName("mensaje") var mensaje: String = "",
    @PropertyName("timespan") var timespan: String = "",
    @PropertyName("estado") var estado: String = "",
    @PropertyName("idEmisor") var idEmisor: String = "",
    @PropertyName("idReceptor") var idReceptor: String = "")