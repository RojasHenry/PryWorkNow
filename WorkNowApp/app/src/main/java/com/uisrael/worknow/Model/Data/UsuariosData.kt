package com.uisrael.worknow.Model.Data

import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName
import java.io.Serializable

data class UsuariosData(
        @PropertyName("nombre") var nombre: String = "",
        @PropertyName("apellido") var apellido:String = "",
        @PropertyName("ciudad") var ciudad:String = "",
        @PropertyName("telefono") var telefono:String = "",
        @PropertyName("foto") var foto:String = "",
        @PropertyName("rol") var rol:String = "",
        @PropertyName("datosProf") var datosProf:ProfesionalData = ProfesionalData())